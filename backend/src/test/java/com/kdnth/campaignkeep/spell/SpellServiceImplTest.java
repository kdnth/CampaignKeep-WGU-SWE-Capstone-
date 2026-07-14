package com.kdnth.campaignkeep.spell;

import com.kdnth.campaignkeep.base.AccessDeniedException;
import com.kdnth.campaignkeep.campaign.CampaignMemberRepository;
import com.kdnth.campaignkeep.campaign.CampaignNonplayableCharacterRepository;
import com.kdnth.campaignkeep.campaign.CampaignPlayableCharacterRepository;
import com.kdnth.campaignkeep.character.CharacterRepository;
import com.kdnth.campaignkeep.character.PlayableCharacter;
import com.kdnth.campaignkeep.dndclass.DndClass;
import com.kdnth.campaignkeep.dndclass.DndClassRepository;
import com.kdnth.campaignkeep.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpellServiceImplTest {

    private static final Long CHARACTER_ID = 10L;
    private static final Long USER_ID = 20L;
    private static final Long SPELL_ID = 100L;
    private static final Long WIZARD_CLASS_ID = 2L;

    @Mock
    private SpellRepository spellRepository;
    @Mock
    private CharacterSpellRepository characterSpellRepository;
    @Mock
    private CharacterRepository characterRepository;
    @Mock
    private DndClassRepository dndClassRepository;
    @Mock
    private CampaignPlayableCharacterRepository campaignPlayableCharacterRepository;
    @Mock
    private CampaignNonplayableCharacterRepository campaignNonplayableCharacterRepository;
    @Mock
    private CampaignMemberRepository campaignMemberRepository;
    @Mock
    private Dnd5eApiClient dnd5eApiClient;

    @InjectMocks
    private SpellServiceImpl spellService;

    private PlayableCharacter character;
    private Spell fireBolt;

    @BeforeEach
    void setUp() {
        User player = new User();
        player.setId(USER_ID);

        character = new PlayableCharacter();
        character.setId(CHARACTER_ID);
        character.setPlayer(player);

        fireBolt = new Spell();
        fireBolt.setId(SPELL_ID);
        fireBolt.setApiIndex("fire-bolt");
        fireBolt.setName("Fire Bolt");
        fireBolt.setLevel((short) 0);
        fireBolt.setSchool(MagicSchool.evocation);
        fireBolt.setRangeType(SpellRangeType.ranged);
        fireBolt.setConcentration(false);
        fireBolt.setRitual(false);
        fireBolt.setHasVerbal(true);
        fireBolt.setHasSomatic(true);
        fireBolt.setHasMaterial(false);
    }

    @Test
    void getSpellById_returnsCachedSpell() {
        when(spellRepository.findById(SPELL_ID)).thenReturn(Optional.of(fireBolt));

        SpellDetailResponse response = spellService.getSpellById(SPELL_ID);

        assertEquals("Fire Bolt", response.name());
        assertEquals(MagicSchool.evocation, response.school());
    }

    @Test
    void getSpellByApiIndex_fetchesAndCachesOnMiss() {
        when(spellRepository.findByApiIndex("fire-bolt")).thenReturn(Optional.empty());
        when(dnd5eApiClient.fetchSpell("fire-bolt")).thenReturn(apiResponse("fire-bolt", "Fire Bolt", 0));
        when(spellRepository.save(any(Spell.class))).thenAnswer(invocation -> {
            Spell saved = invocation.getArgument(0);
            saved.setId(SPELL_ID);
            return saved;
        });

        SpellDetailResponse response = spellService.getSpellByApiIndex("fire-bolt");

        assertEquals("Fire Bolt", response.name());
        verify(spellRepository).save(any(Spell.class));
    }

    @Test
    void addSpellToCharacter_persistsLinkForOwner() {
        when(characterRepository.findById(CHARACTER_ID)).thenReturn(Optional.of(character));
        when(spellRepository.findById(SPELL_ID)).thenReturn(Optional.of(fireBolt));
        when(characterSpellRepository.existsByCharacter_IdAndSpell_Id(CHARACTER_ID, SPELL_ID)).thenReturn(false);

        SpellDetailResponse response = spellService.addSpellToCharacter(CHARACTER_ID, SPELL_ID, USER_ID);

        assertEquals("Fire Bolt", response.name());
        verify(characterSpellRepository).save(any(CharacterSpell.class));
    }

    @Test
    void addSpellToCharacter_deniesNonOwner() {
        when(characterRepository.findById(CHARACTER_ID)).thenReturn(Optional.of(character));

        assertThrows(AccessDeniedException.class, () ->
                spellService.addSpellToCharacter(CHARACTER_ID, SPELL_ID, 999L));
        verify(characterSpellRepository, never()).save(any());
    }

    @Test
    void validateCreationSpells_rejectsSpellsForFighter() {
        DndClass fighter = classWithIndex("fighter");
        when(dndClassRepository.findById(1L)).thenReturn(Optional.of(fighter));

        assertThrows(IllegalArgumentException.class, () ->
                spellService.validateCreationSpells(1L, null, List.of(SPELL_ID)));
    }

    @Test
    void validateCreationSpells_requiresWizardCantripAndLevelOneCounts() {
        DndClass wizard = classWithIndex(SpellServiceImpl.WIZARD_CLASS_INDEX);
        when(dndClassRepository.findById(WIZARD_CLASS_ID)).thenReturn(Optional.of(wizard));
        when(dnd5eApiClient.fetchClassSpellIndices(SpellServiceImpl.WIZARD_CLASS_INDEX))
                .thenReturn(Set.of(
                        "fire-bolt", "light", "mage-hand",
                        "magic-missile", "shield", "sleep", "grease", "jump", "alarm"
                ));

        Spell magicMissile = spellWithLevel(101L, "magic-missile", "Magic Missile", (short) 1);

        when(spellRepository.findAllById(List.of(SPELL_ID))).thenReturn(List.of(fireBolt));
        assertThrows(IllegalArgumentException.class, () ->
                spellService.validateCreationSpells(WIZARD_CLASS_ID, null, List.of(SPELL_ID)));

        when(spellRepository.findAllById(any())).thenReturn(List.of(
                fireBolt, copyWithId(fireBolt, 2L, "light", (short) 0),
                copyWithId(fireBolt, 3L, "mage-hand", (short) 0),
                magicMissile, copyWithId(magicMissile, 102L, "shield", (short) 1),
                copyWithId(magicMissile, 103L, "sleep", (short) 1),
                copyWithId(magicMissile, 104L, "grease", (short) 1),
                copyWithId(magicMissile, 105L, "jump", (short) 1),
                copyWithId(magicMissile, 106L, "alarm", (short) 1)
        ));

        spellService.validateCreationSpells(
                WIZARD_CLASS_ID,
                null,
                List.of(SPELL_ID, 2L, 3L, 101L, 102L, 103L, 104L, 105L, 106L)
        );
    }

    private DndClass classWithIndex(String index) {
        DndClass dndClass = new DndClass();
        dndClass.setId(WIZARD_CLASS_ID);
        dndClass.setIndex(index);
        dndClass.setName(index);
        return dndClass;
    }

    private Spell spellWithLevel(Long id, String apiIndex, String name, short level) {
        Spell spell = new Spell();
        spell.setId(id);
        spell.setApiIndex(apiIndex);
        spell.setName(name);
        spell.setLevel(level);
        spell.setSchool(MagicSchool.evocation);
        spell.setRangeType(SpellRangeType.ranged);
        return spell;
    }

    private Spell copyWithId(Spell template, Long id, String apiIndex, short level) {
        Spell spell = spellWithLevel(id, apiIndex, apiIndex, level);
        spell.setSchool(template.getSchool());
        return spell;
    }

    private Dnd5eSpellResponse apiResponse(String index, String name, int level) {
        return new Dnd5eSpellResponse(
                index,
                name,
                List.of("A spell description."),
                List.of(),
                "60 feet",
                List.of("V", "S"),
                null,
                false,
                "Instantaneous",
                false,
                "1 action",
                level,
                new Dnd5eNamedResource("evocation", "Evocation"),
                List.of(new Dnd5eNamedResource("wizard", "Wizard"))
        );
    }
}
