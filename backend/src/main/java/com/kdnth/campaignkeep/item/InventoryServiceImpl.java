package com.kdnth.campaignkeep.item;

import com.kdnth.campaignkeep.base.AccessDeniedException;
import com.kdnth.campaignkeep.base.ConflictException;
import com.kdnth.campaignkeep.campaign.CampaignMemberRepository;
import com.kdnth.campaignkeep.campaign.CampaignNonplayableCharacterRepository;
import com.kdnth.campaignkeep.campaign.CampaignPlayableCharacterRepository;
import com.kdnth.campaignkeep.campaign.CampaignService;
import com.kdnth.campaignkeep.character.Character;
import com.kdnth.campaignkeep.character.CharacterClassRepository;
import com.kdnth.campaignkeep.character.CharacterRepository;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
public class InventoryServiceImpl implements InventoryService {

    private static final short PROFICIENCY_BONUS = 2;

    private final CharacterRepository characterRepository;
    private final CharacterItemRepository characterItemRepository;
    private final ItemRepository itemRepository;
    private final ClassStartingEquipmentOptionRepository startingOptionRepository;
    private final CharacterClassRepository characterClassRepository;
    private final CampaignService campaignService;
    private final CampaignPlayableCharacterRepository campaignPlayableCharacterRepository;
    private final CampaignNonplayableCharacterRepository campaignNonplayableCharacterRepository;
    private final CampaignMemberRepository campaignMemberRepository;
    private final ItemService itemService;

    public InventoryServiceImpl(
            CharacterRepository characterRepository,
            CharacterItemRepository characterItemRepository,
            ItemRepository itemRepository,
            ClassStartingEquipmentOptionRepository startingOptionRepository,
            CharacterClassRepository characterClassRepository,
            CampaignService campaignService,
            CampaignPlayableCharacterRepository campaignPlayableCharacterRepository,
            CampaignNonplayableCharacterRepository campaignNonplayableCharacterRepository,
            CampaignMemberRepository campaignMemberRepository,
            ItemService itemService
    ) {
        this.characterRepository = characterRepository;
        this.characterItemRepository = characterItemRepository;
        this.itemRepository = itemRepository;
        this.startingOptionRepository = startingOptionRepository;
        this.characterClassRepository = characterClassRepository;
        this.campaignService = campaignService;
        this.campaignPlayableCharacterRepository = campaignPlayableCharacterRepository;
        this.campaignNonplayableCharacterRepository = campaignNonplayableCharacterRepository;
        this.campaignMemberRepository = campaignMemberRepository;
        this.itemService = itemService;
    }

    @Override
    public CharacterInventoryResponse getInventory(Long characterId, Long callerId) {
        Character character = loadCharacterForView(characterId, callerId);
        return buildInventoryResponse(character);
    }

    @Override
    @Transactional
    public CharacterInventoryResponse submitStartingEquipment(
            Long characterId,
            SubmitStartingEquipmentRequest request,
            Long callerId
    ) {
        Character character = loadCharacterForEdit(characterId, callerId);
        if (Boolean.TRUE.equals(character.getStartingEquipmentChosen())) {
            throw new ConflictException("Starting equipment has already been chosen.");
        }

        Long classId = characterClassRepository.findByCharacterId(characterId).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Character has no class."))
                .getDndClass()
                .getId();

        List<ClassStartingEquipmentOption> options =
                startingOptionRepository.findByDndClass_IdOrderBySortOrderAsc(classId);

        validateStartingPick(options, "weapon", request.weaponItemId());
        if (request.armorItemId() != null) {
            validateStartingPick(options, "armor", request.armorItemId());
        }
        if (request.shieldItemId() != null) {
            validateStartingPick(options, "shield", request.shieldItemId());
        }

        grantItemToCharacter(character, request.weaponItemId(), (short) 1);
        if (request.armorItemId() != null) {
            grantItemToCharacter(character, request.armorItemId(), (short) 1);
        }
        if (request.shieldItemId() != null) {
            grantItemToCharacter(character, request.shieldItemId(), (short) 1);
        }

        character.setStartingEquipmentChosen(true);
        autoEquipStartingItems(character);
        recalculateArmorClass(character);
        characterRepository.save(character);

        return buildInventoryResponse(character);
    }

    @Override
    @Transactional
    public CharacterInventoryResponse updateInventoryItem(
            Long characterId,
            Long inventoryItemId,
            UpdateInventoryItemRequest request,
            Long callerId
    ) {
        Character character = loadCharacterForEdit(characterId, callerId);
        CharacterItem characterItem = loadCharacterItem(characterId, inventoryItemId);

        if (request.quantity() != null) {
            if (!isStackable(characterItem.getItem())) {
                throw new IllegalArgumentException("This item cannot have its quantity changed.");
            }
            characterItem.setQuantity(request.quantity());
        }

        if (Boolean.TRUE.equals(request.unequip())) {
            unequipItem(characterItem);
        } else if (request.equipToSlot() != null) {
            equipItem(character, characterItem, request.equipToSlot());
        }

        recalculateArmorClass(character);
        characterRepository.save(character);
        return buildInventoryResponse(character);
    }

    @Override
    @Transactional
    public void removeInventoryItem(Long characterId, Long inventoryItemId, Long callerId) {
        Character character = loadCharacterForEdit(characterId, callerId);
        CharacterItem characterItem = loadCharacterItem(characterId, inventoryItemId);
        if (characterItem.getEquippedSlot() != null) {
            throw new ConflictException("Cannot remove an equipped item. Unequip it first.");
        }
        characterItemRepository.delete(characterItem);
        recalculateArmorClass(character);
        characterRepository.save(character);
    }

    @Override
    @Transactional
    public CharacterInventoryResponse updateGold(Long characterId, UpdateGoldRequest request, Long callerId) {
        Character character = loadCharacterForEdit(characterId, callerId);
        character.setGold(request.gold());
        characterRepository.save(character);
        return buildInventoryResponse(character);
    }

    @Override
    public List<CharacterAttackResponse> getAttacks(Long characterId, Long callerId) {
        Character character = loadCharacterForView(characterId, callerId);
        return characterItemRepository.findByCharacter_Id(character.getId()).stream()
                .filter(ci -> ci.getEquippedSlot() == EquipmentSlot.main_hand
                        || ci.getEquippedSlot() == EquipmentSlot.off_hand)
                .map(ci -> toAttackResponse(character, ci))
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    @Transactional
    public CharacterInventoryResponse grantItem(
            Long campaignId,
            Long characterId,
            GrantItemRequest request,
            Long callerId
    ) {
        campaignService.requireMaster(campaignId, callerId);
        boolean attached = campaignPlayableCharacterRepository
                .existsByCharacterIdAndCampaignId(characterId, campaignId)
                || campaignNonplayableCharacterRepository
                .existsByCharacterIdAndCampaignId(characterId, campaignId);
        if (!attached) {
            throw new IllegalArgumentException("Character is not attached to this campaign.");
        }

        Character character = characterRepository.findById(characterId)
                .orElseThrow(() -> new NoSuchElementException("Character not found."));

        Item item = itemRepository.findById(request.itemId())
                .orElseThrow(() -> new NoSuchElementException("Item not found."));
        if (item.getCampaign() != null && !item.getCampaign().getId().equals(campaignId)) {
            throw new AccessDeniedException("Item is not available in this campaign.");
        }

        ensureWeightCapacity(character, item, request.quantity());
        grantItemToCharacter(character, request.itemId(), request.quantity());
        characterRepository.save(character);
        return buildInventoryResponse(character);
    }

    @Override
    @Transactional
    public CharacterInventoryResponse submitStartingPack(
            Long characterId,
            SubmitStartingPackRequest request,
            Long callerId
    ) {
        Character character = loadCharacterForEdit(characterId, callerId);
        EquipmentPackResponse pack = itemService.getStartingPack(request.packIndex());
        for (PackContentResponse content : pack.contents()) {
            Item item = itemService.findOrCreateFromApi(content.apiIndex());
            grantItemToCharacter(character, item.getId(), (short) content.quantity());
        }
        characterRepository.save(character);
        return buildInventoryResponse(character);
    }

    private void autoEquipStartingItems(Character character) {
        List<CharacterItem> items = characterItemRepository.findByCharacter_Id(character.getId());
        for (CharacterItem ci : items) {
            Item item = (Item) Hibernate.unproxy(ci.getItem());
            if (item instanceof Weapon) {
                if (items.stream().noneMatch(other ->
                        other.getEquippedSlot() == EquipmentSlot.main_hand)) {
                    equipItem(character, ci, EquipmentSlot.main_hand);
                }
            } else if (item instanceof Armor armor) {
                if (armor.isShield()) {
                    equipItem(character, ci, EquipmentSlot.shield);
                } else {
                    equipItem(character, ci, EquipmentSlot.armor);
                }
            }
        }
    }

    private void equipItem(Character character, CharacterItem characterItem, EquipmentSlot slot) {
        Item item = (Item) Hibernate.unproxy(characterItem.getItem());

        if (slot == EquipmentSlot.armor || slot == EquipmentSlot.shield) {
            if (!(item instanceof Armor armor)) {
                throw new IllegalArgumentException("Only armor can be equipped in armor or shield slots.");
            }
            if (slot == EquipmentSlot.shield && !armor.isShield()) {
                throw new IllegalArgumentException("Only a shield can be equipped in the shield slot.");
            }
            if (slot == EquipmentSlot.armor && armor.isShield()) {
                throw new IllegalArgumentException("A shield cannot be equipped as body armor.");
            }
            if (armor.getStrMinimum() != null && character.getStrength() < armor.getStrMinimum()) {
                throw new IllegalArgumentException("Strength requirement not met for this armor.");
            }
        } else {
            if (!(item instanceof Weapon)) {
                throw new IllegalArgumentException("Only weapons can be equipped in hand slots.");
            }
        }

        characterItemRepository.findByCharacter_IdAndEquippedSlot(character.getId(), slot)
                .ifPresent(occupied -> occupied.setEquippedSlot(null));

        characterItem.setEquippedSlot(slot);
        characterItemRepository.save(characterItem);
    }

    private void unequipItem(CharacterItem characterItem) {
        characterItem.setEquippedSlot(null);
        characterItemRepository.save(characterItem);
    }

    private void grantItemToCharacter(Character character, Long itemId, short quantity) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Item not found: " + itemId));

        if (isStackable(item)) {
            characterItemRepository.findByCharacter_IdAndItem_Id(character.getId(), itemId)
                    .ifPresentOrElse(
                            existing -> existing.setQuantity((short) (existing.getQuantity() + quantity)),
                            () -> {
                                CharacterItem ci = new CharacterItem();
                                ci.setCharacter(character);
                                ci.setItem(item);
                                ci.setQuantity(quantity);
                                characterItemRepository.save(ci);
                            }
                    );
        } else {
            for (int i = 0; i < quantity; i++) {
                CharacterItem ci = new CharacterItem();
                ci.setCharacter(character);
                ci.setItem(item);
                ci.setQuantity((short) 1);
                characterItemRepository.save(ci);
            }
        }
    }

    private void ensureWeightCapacity(Character character, Item item, short quantity) {
        int additional = (item.getWeight() != null ? item.getWeight() : 0) * quantity;
        int current = calculateTotalWeight(character.getId());
        int capacity = calculateCarryingCapacity(character);
        if (current + additional > capacity) {
            throw new ConflictException("Adding this item would exceed carrying capacity.");
        }
    }

    private boolean isStackable(Item item) {
        Item resolved = (Item) Hibernate.unproxy(item);
        ItemType type = resolved.getItemType();
        return type == ItemType.tool || type == ItemType.gear || type == ItemType.clothing;
    }

    private void validateStartingPick(List<ClassStartingEquipmentOption> options, String group, Long itemId) {
        boolean valid = options.stream()
                .anyMatch(opt -> group.equals(opt.getOptionGroup()) && opt.getItem().getId().equals(itemId));
        if (!valid) {
            throw new IllegalArgumentException("Invalid starting equipment selection for group: " + group);
        }
    }

    void recalculateArmorClass(Character character) {
        List<CharacterItem> equipped = characterItemRepository.findByCharacter_Id(character.getId()).stream()
                .filter(ci -> ci.getEquippedSlot() != null)
                .toList();

        Armor bodyArmor = null;
        boolean hasShield = false;

        for (CharacterItem ci : equipped) {
            Item item = (Item) Hibernate.unproxy(ci.getItem());
            if (item instanceof Armor armor) {
                if (ci.getEquippedSlot() == EquipmentSlot.shield || armor.isShield()) {
                    hasShield = true;
                } else if (ci.getEquippedSlot() == EquipmentSlot.armor) {
                    bodyArmor = armor;
                }
            }
        }

        int dexMod = abilityModifier(character.getDexterity());
        int ac;

        if (bodyArmor == null) {
            ac = 10 + dexMod;
        } else {
            String category = bodyArmor.getArmorCategoryName();
            if ("light".equals(category)) {
                ac = bodyArmor.getBaseAc() + dexMod;
            } else if ("medium".equals(category)) {
                int maxDex = bodyArmor.getMaxDexBonus() != null ? bodyArmor.getMaxDexBonus() : 2;
                ac = bodyArmor.getBaseAc() + Math.min(dexMod, maxDex);
            } else if ("heavy".equals(category)) {
                ac = bodyArmor.getBaseAc();
            } else {
                ac = 10 + dexMod;
            }
        }

        if (hasShield) {
            ac += 2;
        }

        character.setArmorClass((short) ac);
    }

    private CharacterAttackResponse toAttackResponse(Character character, CharacterItem characterItem) {
        Item item = (Item) Hibernate.unproxy(characterItem.getItem());
        if (!(item instanceof Weapon weapon)) {
            return null;
        }

        int strMod = abilityModifier(character.getStrength());
        int dexMod = abilityModifier(character.getDexterity());
        int abilityMod;
        if (weapon.isFinesse()) {
            abilityMod = Math.max(strMod, dexMod);
        } else if (weapon.getWeaponRange() == WeaponRange.ranged) {
            abilityMod = dexMod;
        } else {
            abilityMod = strMod;
        }

        return new CharacterAttackResponse(
                characterItem.getId(),
                weapon.getName(),
                abilityMod + PROFICIENCY_BONUS,
                weapon.getDamage(),
                abilityMod,
                weapon.getDamageType(),
                weapon.getWeaponRange(),
                weapon.getRange(),
                characterItem.getEquippedSlot()
        );
    }

    private CharacterInventoryResponse buildInventoryResponse(Character character) {
        List<InventoryItemResponse> items = characterItemRepository.findByCharacter_Id(character.getId()).stream()
                .map(ci -> new InventoryItemResponse(
                        ci.getId(),
                        ItemMapper.toDetail(ci.getItem()),
                        ci.getQuantity(),
                        ci.getEquippedSlot()
                ))
                .toList();

        return new CharacterInventoryResponse(
                items,
                calculateTotalWeight(character.getId()),
                calculateCarryingCapacity(character),
                character.getGold() != null ? character.getGold() : 0,
                Boolean.TRUE.equals(character.getStartingEquipmentChosen()),
                character.getArmorClass()
        );
    }

    private int calculateTotalWeight(Long characterId) {
        return characterItemRepository.findByCharacter_Id(characterId).stream()
                .mapToInt(ci -> {
                    short weight = ci.getItem().getWeight() != null ? ci.getItem().getWeight() : 0;
                    return weight * ci.getQuantity();
                })
                .sum();
    }

    private int calculateCarryingCapacity(Character character) {
        return character.getStrength() * 15;
    }

    private static int abilityModifier(short score) {
        return (score - 10) / 2;
    }

    private CharacterItem loadCharacterItem(Long characterId, Long inventoryItemId) {
        CharacterItem characterItem = characterItemRepository.findById(inventoryItemId)
                .orElseThrow(() -> new NoSuchElementException("Inventory item not found."));
        if (!characterItem.getCharacter().getId().equals(characterId)) {
            throw new NoSuchElementException("Inventory item not found.");
        }
        return characterItem;
    }

    private Character loadCharacterForEdit(Long characterId, Long callerId) {
        Character character = characterRepository.findById(characterId)
                .orElseThrow(() -> new NoSuchElementException("Character not found."));
        if (!character.canBeEditedBy(callerId)) {
            throw new AccessDeniedException("You do not have permission to edit this character.");
        }
        return character;
    }

    private Character loadCharacterForView(Long characterId, Long callerId) {
        Character character = characterRepository.findById(characterId)
                .orElseThrow(() -> new NoSuchElementException("Character not found."));
        if (character.canBeEditedBy(callerId)) {
            return character;
        }
        if (canViewThroughCampaignMembership(characterId, callerId)) {
            return character;
        }
        throw new AccessDeniedException("You do not have permission to view this character's inventory.");
    }

    private boolean canViewThroughCampaignMembership(Long characterId, Long callerId) {
        List<Long> campaignIds = new ArrayList<>();
        campaignPlayableCharacterRepository.findByCharacterId(characterId).forEach(link ->
                campaignIds.add(link.getCampaign().getId()));
        campaignNonplayableCharacterRepository.findByCharacterId(characterId).forEach(link ->
                campaignIds.add(link.getCampaign().getId()));

        return campaignIds.stream().anyMatch(campaignId ->
                campaignMemberRepository.findByCampaign_IdAndUser_Id(campaignId, callerId).isPresent());
    }
}
