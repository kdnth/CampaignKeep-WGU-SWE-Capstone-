package com.kdnth.campaignkeep.item;

import com.kdnth.campaignkeep.campaign.Campaign;
import com.kdnth.campaignkeep.campaign.CampaignService;
import com.kdnth.campaignkeep.dndclass.DndClassRepository;
import com.kdnth.campaignkeep.spell.Dnd5eApiClient;
import com.kdnth.campaignkeep.user.User;
import com.kdnth.campaignkeep.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private static final Set<String> SUPPORTED_PACKS = Set.of(
            "burglars-pack",
            "diplomats-pack",
            "dungeoneers-pack",
            "explorers-pack",
            "scholars-pack"
    );

    private final ItemRepository itemRepository;
    private final ClassStartingEquipmentOptionRepository startingOptionRepository;
    private final DndClassRepository dndClassRepository;
    private final CampaignService campaignService;
    private final UserRepository userRepository;
    private final ArmorCategoryRepository armorCategoryRepository;
    private final WeaponCategoryRepository weaponCategoryRepository;
    private final Dnd5eApiClient dnd5eApiClient;

    public ItemServiceImpl(
            ItemRepository itemRepository,
            ClassStartingEquipmentOptionRepository startingOptionRepository,
            DndClassRepository dndClassRepository,
            CampaignService campaignService,
            UserRepository userRepository,
            ArmorCategoryRepository armorCategoryRepository,
            WeaponCategoryRepository weaponCategoryRepository,
            Dnd5eApiClient dnd5eApiClient
    ) {
        this.itemRepository = itemRepository;
        this.startingOptionRepository = startingOptionRepository;
        this.dndClassRepository = dndClassRepository;
        this.campaignService = campaignService;
        this.userRepository = userRepository;
        this.armorCategoryRepository = armorCategoryRepository;
        this.weaponCategoryRepository = weaponCategoryRepository;
        this.dnd5eApiClient = dnd5eApiClient;
    }

    @Override
    public ItemDetailResponse getItemById(Long id, Long callerId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Item not found."));
        if (item.getCampaign() == null) {
            return ItemMapper.toDetail(item);
        }
        Long campaignId = item.getCampaign().getId();
        campaignService.requireMembership(campaignId, callerId);
        return ItemMapper.toDetail(item);
    }

    @Override
    public List<ItemDetailResponse> listCampaignItems(Long campaignId, Long callerId) {
        campaignService.requireMembership(campaignId, callerId);
        return itemRepository.findByCampaignIsNullOrCampaign_Id(campaignId).stream()
                .map(ItemMapper::toDetail)
                .toList();
    }

    @Override
    @Transactional
    public ItemDetailResponse createCampaignItem(Long campaignId, CreateCampaignItemRequest request, Long callerId) {
        campaignService.requireMaster(campaignId, callerId);
        Campaign campaign = campaignService.requireMaster(campaignId, callerId).getCampaign();
        User creator = userRepository.findById(callerId)
                .orElseThrow(() -> new NoSuchElementException("User not found."));

        Item item = switch (request.itemType()) {
            case weapon -> createWeapon(request, campaign, creator);
            case armor -> createArmor(request, campaign, creator);
            case tool -> createSimpleItem(new ToolItem(), request, campaign, creator);
            case gear -> createSimpleItem(new GearItem(), request, campaign, creator);
            case jewelry -> createSimpleItem(new JewelryItem(), request, campaign, creator);
            case clothing -> createSimpleItem(new ClothingItem(), request, campaign, creator);
        };

        return ItemMapper.toDetail(itemRepository.save(item));
    }

    @Override
    public StartingEquipmentOptionsResponse getStartingEquipmentOptions(Long classId) {
        if (!dndClassRepository.existsById(classId)) {
            throw new NoSuchElementException("Class not found.");
        }

        Map<String, List<ItemDetailResponse>> grouped = startingOptionRepository
                .findByDndClass_IdOrderBySortOrderAsc(classId).stream()
                .collect(Collectors.groupingBy(
                        ClassStartingEquipmentOption::getOptionGroup,
                        LinkedHashMap::new,
                        Collectors.mapping(opt -> ItemMapper.toDetail(opt.getItem()), Collectors.toList())
                ));

        return new StartingEquipmentOptionsResponse(grouped);
    }

    @Override
    public List<EquipmentPackResponse> listStartingPacks() {
        List<EquipmentPackResponse> packs = new ArrayList<>();
        for (String packIndex : SUPPORTED_PACKS) {
            packs.add(getStartingPack(packIndex));
        }
        return packs;
    }

    @Override
    public EquipmentPackResponse getStartingPack(String packIndex) {
        String normalized = normalizePackIndex(packIndex);
        if (!SUPPORTED_PACKS.contains(normalized)) {
            throw new IllegalArgumentException("Unsupported starting pack: " + packIndex);
        }
        Dnd5eEquipmentResponse pack = dnd5eApiClient.fetchEquipment(normalized);
        if (pack == null) {
            throw new NoSuchElementException("Pack not found in 5e API: " + normalized);
        }
        List<PackContentResponse> contents = new ArrayList<>();
        if (pack.contents() != null) {
            for (Dnd5eEquipmentContent content : pack.contents()) {
                if (content.item() == null) {
                    continue;
                }
                contents.add(new PackContentResponse(
                        content.item().index(),
                        content.item().name(),
                        content.quantity() != null ? content.quantity() : 1
                ));
            }
        }
        return new EquipmentPackResponse(
                pack.index(),
                pack.name(),
                EquipmentMapper.costInGp(pack.cost()),
                contents
        );
    }

    @Override
    @Transactional
    public Item findOrCreateFromApi(String apiIndex) {
        return itemRepository.findByApiIndex(apiIndex)
                .orElseGet(() -> {
                    Dnd5eEquipmentResponse response = dnd5eApiClient.fetchEquipment(apiIndex);
                    if (response == null) {
                        throw new NoSuchElementException("Equipment not found in 5e API: " + apiIndex);
                    }
                    return itemRepository.save(mapApiEquipment(response));
                });
    }

    String normalizePackIndex(String packIndex) {
        if (packIndex == null || packIndex.isBlank()) {
            throw new IllegalArgumentException("Pack index is required.");
        }
        return packIndex.trim().toLowerCase(Locale.ROOT).replace('_', '-');
    }

    private Item mapApiEquipment(Dnd5eEquipmentResponse response) {
        String category = response.equipment_category() != null
                ? response.equipment_category().index()
                : "adventuring-gear";

        return switch (category) {
            case "weapon" -> mapWeapon(response);
            case "armor" -> mapArmor(response);
            case "tools" -> mapSimple(new ToolItem(), response);
            default -> mapSimple(new GearItem(), response);
        };
    }

    private Weapon mapWeapon(Dnd5eEquipmentResponse response) {
        Weapon weapon = new Weapon();
        applyApiBaseFields(weapon, response);
        weapon.setDamage(response.damage() != null ? response.damage().damage_dice() : "1d4");
        if (response.damage() != null && response.damage().damage_type() != null) {
            EquipmentMapper.parseDamageType(response.damage().damage_type().index())
                    .ifPresent(weapon::setDamageType);
        }
        if (weapon.getDamageType() == null) {
            weapon.setDamageType(DamageType.slashing);
        }
        weapon.setWeaponRange(EquipmentMapper.parseWeaponRange(response.weapon_range()));
        if (response.range() != null && response.range().normal() != null) {
            weapon.setRange(response.range().normal().shortValue());
        }
        if (response.weapon_category() != null) {
            weaponCategoryRepository.findByNameIgnoreCase(response.weapon_category())
                    .ifPresent(weapon::setWeaponCategory);
        }
        return weapon;
    }

    private Armor mapArmor(Dnd5eEquipmentResponse response) {
        Armor armor = new Armor();
        applyApiBaseFields(armor, response);
        String categoryName = response.armor_category() != null
                ? response.armor_category().toLowerCase(Locale.ROOT)
                : "light";
        if ("shield".equals(categoryName)) {
            armor.setArmorCategory(armorCategoryRepository.findByNameIgnoreCase("shield")
                    .orElseThrow(() -> new IllegalStateException("Shield armor category missing.")));
            armor.setBaseAc(response.armor_class() != null && response.armor_class().base() != null
                    ? response.armor_class().base().shortValue()
                    : (short) 2);
            armor.setMaxDexBonus((short) 0);
        } else {
            armor.setArmorCategory(armorCategoryRepository.findByNameIgnoreCase(categoryName)
                    .orElseGet(() -> armorCategoryRepository.findByNameIgnoreCase("light")
                            .orElseThrow(() -> new IllegalStateException("Armor categories missing."))));
            if (response.armor_class() != null) {
                armor.setBaseAc(response.armor_class().base() != null
                        ? response.armor_class().base().shortValue()
                        : (short) 10);
                if (Boolean.FALSE.equals(response.armor_class().dex_bonus())) {
                    armor.setMaxDexBonus((short) 0);
                } else if (response.armor_class().max_bonus() != null) {
                    armor.setMaxDexBonus(response.armor_class().max_bonus().shortValue());
                }
            } else {
                armor.setBaseAc((short) 10);
            }
        }
        if (response.str_minimum() != null && response.str_minimum() > 0) {
            armor.setStrMinimum(response.str_minimum().shortValue());
        }
        return armor;
    }

    private Item mapSimple(Item item, Dnd5eEquipmentResponse response) {
        applyApiBaseFields(item, response);
        return item;
    }

    private void applyApiBaseFields(Item item, Dnd5eEquipmentResponse response) {
        item.setApiIndex(response.index());
        item.setName(response.name());
        item.setDescription(EquipmentMapper.joinDesc(response.desc()));
        item.setValue(EquipmentMapper.costInGp(response.cost()));
        item.setWeight(EquipmentMapper.weightToShort(response.weight()));
        item.setRarity(ItemRarity.common);
        item.setIsMagical(false);
    }

    private Weapon createWeapon(CreateCampaignItemRequest request, Campaign campaign, User creator) {
        if (request.damage() == null || request.damageType() == null || request.weaponRange() == null) {
            throw new IllegalArgumentException("Weapon items require damage, damage type, and weapon range.");
        }
        Weapon weapon = new Weapon();
        applyBaseFields(weapon, request, campaign, creator);
        weapon.setDamage(request.damage());
        weapon.setDamageType(request.damageType());
        weapon.setRange(request.range());
        weapon.setWeaponRange(request.weaponRange());
        if (request.weaponCategoryId() != null) {
            weapon.setWeaponCategory(weaponCategoryRepository.findById(request.weaponCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Weapon category not found.")));
        }
        return weapon;
    }

    private Armor createArmor(CreateCampaignItemRequest request, Campaign campaign, User creator) {
        if (request.armorCategoryId() == null || request.baseAc() == null) {
            throw new IllegalArgumentException("Armor items require armor category and base AC.");
        }
        Armor armor = new Armor();
        applyBaseFields(armor, request, campaign, creator);
        armor.setArmorCategory(armorCategoryRepository.findById(request.armorCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Armor category not found.")));
        armor.setBaseAc(request.baseAc());
        armor.setMaxDexBonus(request.maxDexBonus());
        armor.setStrMinimum(request.strMinimum());
        return armor;
    }

    private Item createSimpleItem(Item item, CreateCampaignItemRequest request, Campaign campaign, User creator) {
        applyBaseFields(item, request, campaign, creator);
        return item;
    }

    private void applyBaseFields(Item item, CreateCampaignItemRequest request, Campaign campaign, User creator) {
        item.setName(request.name());
        item.setDescription(request.description());
        item.setValue(request.value() != null ? request.value() : 0);
        item.setWeight(request.weight());
        item.setRarity(request.rarity() != null ? request.rarity() : ItemRarity.common);
        item.setIsMagical(Boolean.TRUE.equals(request.isMagical()));
        item.setCampaign(campaign);
        item.setCreatedBy(creator);
    }
}
