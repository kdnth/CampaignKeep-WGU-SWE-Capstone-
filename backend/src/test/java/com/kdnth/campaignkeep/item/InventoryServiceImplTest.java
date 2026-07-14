package com.kdnth.campaignkeep.item;

import com.kdnth.campaignkeep.base.AccessDeniedException;
import com.kdnth.campaignkeep.base.ConflictException;
import com.kdnth.campaignkeep.campaign.CampaignMemberRepository;
import com.kdnth.campaignkeep.campaign.CampaignNonplayableCharacterRepository;
import com.kdnth.campaignkeep.campaign.CampaignPlayableCharacterRepository;
import com.kdnth.campaignkeep.campaign.CampaignService;
import com.kdnth.campaignkeep.character.CharacterClass;
import com.kdnth.campaignkeep.character.CharacterClassRepository;
import com.kdnth.campaignkeep.character.CharacterRepository;
import com.kdnth.campaignkeep.character.PlayableCharacter;
import com.kdnth.campaignkeep.dndclass.DndClass;
import com.kdnth.campaignkeep.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryServiceImplTest {

    private static final Long CHARACTER_ID = 10L;
    private static final Long USER_ID = 20L;
    private static final Long OTHER_USER_ID = 21L;
    private static final Long CLASS_ID = 1L;

    @Mock
    private CharacterRepository characterRepository;
    @Mock
    private CharacterItemRepository characterItemRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ClassStartingEquipmentOptionRepository startingOptionRepository;
    @Mock
    private CharacterClassRepository characterClassRepository;
    @Mock
    private CampaignService campaignService;
    @Mock
    private CampaignPlayableCharacterRepository campaignPlayableCharacterRepository;
    @Mock
    private CampaignNonplayableCharacterRepository campaignNonplayableCharacterRepository;
    @Mock
    private CampaignMemberRepository campaignMemberRepository;
    @Mock
    private ItemService itemService;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    private PlayableCharacter character;
    private ArmorCategory lightCategory;
    private ArmorCategory shieldCategory;

    @BeforeEach
    void setUp() {
        User player = new User();
        player.setId(USER_ID);

        character = new PlayableCharacter();
        character.setId(CHARACTER_ID);
        character.setPlayer(player);
        character.setStrength((short) 16);
        character.setDexterity((short) 14);
        character.setArmorClass((short) 12);
        character.setGold(0);
        character.setStartingEquipmentChosen(false);

        lightCategory = new ArmorCategory();
        lightCategory.setId(1L);
        lightCategory.setName("light");

        shieldCategory = new ArmorCategory();
        shieldCategory.setId(4L);
        shieldCategory.setName("shield");
    }

    @Test
    void recalculateArmorClass_unarmored_usesTenPlusDex() {
        when(characterItemRepository.findByCharacter_Id(CHARACTER_ID)).thenReturn(List.of());

        inventoryService.recalculateArmorClass(character);

        assertEquals(12, character.getArmorClass().intValue());
    }

    @Test
    void recalculateArmorClass_lightArmorAndShield() {
        Armor leather = armor(9L, "Leather Armor", lightCategory, (short) 11, null, null);
        Armor shield = armor(20L, "Shield", shieldCategory, (short) 2, (short) 0, null);

        CharacterItem armorItem = equippedItem(1L, leather, EquipmentSlot.armor);
        CharacterItem shieldItem = equippedItem(2L, shield, EquipmentSlot.shield);

        when(characterItemRepository.findByCharacter_Id(CHARACTER_ID))
                .thenReturn(List.of(armorItem, shieldItem));

        inventoryService.recalculateArmorClass(character);

        assertEquals(15, character.getArmorClass().intValue());
    }

    @Test
    void equipItem_blocksArmorWhenStrengthTooLow() {
        Armor chainMail = heavyArmor();
        CharacterItem item = inventoryItem(1L, chainMail);
        character.setStrength((short) 10);

        when(characterRepository.findById(CHARACTER_ID)).thenReturn(Optional.of(character));
        when(characterItemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(
                IllegalArgumentException.class,
                () -> inventoryService.updateInventoryItem(
                        CHARACTER_ID,
                        1L,
                        new UpdateInventoryItemRequest(EquipmentSlot.armor, false, null),
                        USER_ID
                )
        );
    }

    @Test
    void submitStartingEquipment_setsFlagAndGrantsItems() {
        Weapon longsword = weapon(4L, "Longsword");
        DndClass fighter = new DndClass();
        fighter.setId(CLASS_ID);
        CharacterClass characterClass = new CharacterClass();
        characterClass.setDndClass(fighter);

        ClassStartingEquipmentOption weaponOption = option("weapon", longsword);

        when(characterRepository.findById(CHARACTER_ID)).thenReturn(Optional.of(character));
        when(characterClassRepository.findByCharacterId(CHARACTER_ID)).thenReturn(List.of(characterClass));
        when(startingOptionRepository.findByDndClass_IdOrderBySortOrderAsc(CLASS_ID))
                .thenReturn(List.of(weaponOption));
        when(itemRepository.findById(4L)).thenReturn(Optional.of(longsword));
        when(characterItemRepository.findByCharacter_Id(CHARACTER_ID)).thenReturn(List.of());

        CharacterInventoryResponse response = inventoryService.submitStartingEquipment(
                CHARACTER_ID,
                new SubmitStartingEquipmentRequest(4L, null, null),
                USER_ID
        );

        verify(characterItemRepository).save(any(CharacterItem.class));
        verify(characterRepository).save(character);
        assertEquals(true, character.getStartingEquipmentChosen());
        assertEquals(true, response.startingEquipmentChosen());
    }

    @Test
    void updateGold_requiresOwner() {
        when(characterRepository.findById(CHARACTER_ID)).thenReturn(Optional.of(character));

        assertThrows(
                AccessDeniedException.class,
                () -> inventoryService.updateGold(CHARACTER_ID, new UpdateGoldRequest(50), OTHER_USER_ID)
        );
    }

    @Test
    void grantItem_blocksWhenOverWeightCapacity() {
        GearItem heavyGear = new GearItem();
        heavyGear.setId(41L);
        heavyGear.setName("Heavy load");
        heavyGear.setWeight((short) 500);

        when(campaignPlayableCharacterRepository.existsByCharacterIdAndCampaignId(CHARACTER_ID, 1L))
                .thenReturn(true);
        when(campaignService.requireMaster(1L, USER_ID)).thenReturn(null);
        when(characterRepository.findById(CHARACTER_ID)).thenReturn(Optional.of(character));
        when(itemRepository.findById(41L)).thenReturn(Optional.of(heavyGear));
        when(characterItemRepository.findByCharacter_Id(CHARACTER_ID)).thenReturn(List.of());

        assertThrows(
                ConflictException.class,
                () -> inventoryService.grantItem(1L, CHARACTER_ID, new GrantItemRequest(41L, (short) 1), USER_ID)
        );

        verify(characterItemRepository, never()).save(any());
    }

    private Weapon weapon(Long id, String name) {
        Weapon weapon = new Weapon();
        weapon.setId(id);
        weapon.setName(name);
        weapon.setItemType(ItemType.weapon);
        weapon.setDamage("1d8");
        weapon.setDamageType(DamageType.slashing);
        weapon.setWeaponRange(WeaponRange.melee);
        weapon.setWeight((short) 3);
        return weapon;
    }

    private Armor armor(
            Long id,
            String name,
            ArmorCategory category,
            short baseAc,
            Short maxDex,
            Short strMin
    ) {
        Armor armor = new Armor();
        armor.setId(id);
        armor.setName(name);
        armor.setItemType(ItemType.armor);
        armor.setArmorCategory(category);
        armor.setBaseAc(baseAc);
        armor.setMaxDexBonus(maxDex);
        armor.setStrMinimum(strMin);
        armor.setWeight((short) 10);
        return armor;
    }

    private Armor heavyArmor() {
        ArmorCategory heavy = new ArmorCategory();
        heavy.setId(3L);
        heavy.setName("heavy");
        return armor(17L, "Chain Mail", heavy, (short) 16, (short) 0, (short) 13);
    }

    private CharacterItem inventoryItem(Long id, Item item) {
        CharacterItem ci = new CharacterItem();
        ci.setId(id);
        ci.setCharacter(character);
        ci.setItem(item);
        ci.setQuantity((short) 1);
        return ci;
    }

    private CharacterItem equippedItem(Long id, Item item, EquipmentSlot slot) {
        CharacterItem ci = inventoryItem(id, item);
        ci.setEquippedSlot(slot);
        return ci;
    }

    private ClassStartingEquipmentOption option(String group, Item item) {
        ClassStartingEquipmentOption option = new ClassStartingEquipmentOption();
        option.setOptionGroup(group);
        option.setItem(item);
        return option;
    }
}
