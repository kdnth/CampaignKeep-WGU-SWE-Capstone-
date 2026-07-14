package com.kdnth.campaignkeep.spell;

import com.kdnth.campaignkeep.base.AccessDeniedException;
import com.kdnth.campaignkeep.campaign.CampaignMemberRepository;
import com.kdnth.campaignkeep.campaign.CampaignNonplayableCharacterRepository;
import com.kdnth.campaignkeep.campaign.CampaignPlayableCharacterRepository;
import com.kdnth.campaignkeep.character.Character;
import com.kdnth.campaignkeep.character.CharacterRepository;
import com.kdnth.campaignkeep.dndclass.DndClass;
import com.kdnth.campaignkeep.dndclass.DndClassRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
public class SpellServiceImpl implements SpellService {

    static final long HIGH_ELF_SUBRACE_ID = 1L;
    static final String WIZARD_CLASS_INDEX = "wizard";

    private final SpellRepository spellRepository;
    private final CharacterSpellRepository characterSpellRepository;
    private final CharacterRepository characterRepository;
    private final DndClassRepository dndClassRepository;
    private final CampaignPlayableCharacterRepository campaignPlayableCharacterRepository;
    private final CampaignNonplayableCharacterRepository campaignNonplayableCharacterRepository;
    private final CampaignMemberRepository campaignMemberRepository;
    private final Dnd5eApiClient dnd5eApiClient;

    private volatile Set<String> wizardSpellIndicesCache;

    public SpellServiceImpl(
            SpellRepository spellRepository,
            CharacterSpellRepository characterSpellRepository,
            CharacterRepository characterRepository,
            DndClassRepository dndClassRepository,
            CampaignPlayableCharacterRepository campaignPlayableCharacterRepository,
            CampaignNonplayableCharacterRepository campaignNonplayableCharacterRepository,
            CampaignMemberRepository campaignMemberRepository,
            Dnd5eApiClient dnd5eApiClient
    ) {
        this.spellRepository = spellRepository;
        this.characterSpellRepository = characterSpellRepository;
        this.characterRepository = characterRepository;
        this.dndClassRepository = dndClassRepository;
        this.campaignPlayableCharacterRepository = campaignPlayableCharacterRepository;
        this.campaignNonplayableCharacterRepository = campaignNonplayableCharacterRepository;
        this.campaignMemberRepository = campaignMemberRepository;
        this.dnd5eApiClient = dnd5eApiClient;
    }

    @Override
    public List<SpellSummaryResponse> listSpells(
            String search,
            Short level,
            MagicSchool school,
            String classIndex,
            Short maxLevel
    ) {
        syncAllSpellsIfEmpty();

        Set<String> classSpellIndices = classIndex != null && !classIndex.isBlank()
                ? getWizardSpellIndicesIfWizard(classIndex)
                : null;

        String normalizedSearch = search != null ? search.trim().toLowerCase(Locale.ROOT) : "";

        return spellRepository.findAll().stream()
                .filter(spell -> spell.getApiIndex() != null)
                .filter(spell -> classSpellIndices == null || classSpellIndices.contains(spell.getApiIndex()))
                .filter(spell -> level == null || spell.getLevel().equals(level))
                .filter(spell -> maxLevel == null || spell.getLevel() <= maxLevel)
                .filter(spell -> school == null || spell.getSchool() == school)
                .filter(spell -> normalizedSearch.isEmpty()
                        || spell.getName().toLowerCase(Locale.ROOT).contains(normalizedSearch))
                .map(SpellMapper::toSummary)
                .sorted((a, b) -> {
                    int levelCompare = Short.compare(a.level(), b.level());
                    if (levelCompare != 0) {
                        return levelCompare;
                    }
                    return a.name().compareToIgnoreCase(b.name());
                })
                .toList();
    }

    @Override
    public SpellDetailResponse getSpellById(Long id) {
        Spell spell = spellRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Spell not found."));
        return SpellMapper.toDetail(spell);
    }

    @Override
    @Transactional
    public SpellDetailResponse getSpellByApiIndex(String apiIndex) {
        return SpellMapper.toDetail(findOrFetchByApiIndex(apiIndex));
    }

    @Override
    public List<SpellDetailResponse> getCharacterSpells(Long characterId, Long callerId) {
        Character character = loadCharacterForView(characterId, callerId);
        return characterSpellRepository.findByCharacterId(character.getId()).stream()
                .map(link -> SpellMapper.toDetail(link.getSpell()))
                .sorted((a, b) -> {
                    int levelCompare = Short.compare(a.level(), b.level());
                    if (levelCompare != 0) {
                        return levelCompare;
                    }
                    return a.name().compareToIgnoreCase(b.name());
                })
                .toList();
    }

    @Override
    @Transactional
    public SpellDetailResponse addSpellToCharacter(Long characterId, Long spellId, Long callerId) {
        Character character = loadCharacterForEdit(characterId, callerId);
        Spell spell = spellRepository.findById(spellId)
                .orElseThrow(() -> new NoSuchElementException("Spell not found."));

        if (characterSpellRepository.existsByCharacter_IdAndSpell_Id(characterId, spellId)) {
            return SpellMapper.toDetail(spell);
        }

        CharacterSpell link = new CharacterSpell();
        link.setCharacter(character);
        link.setSpell(spell);
        characterSpellRepository.save(link);
        return SpellMapper.toDetail(spell);
    }

    @Override
    @Transactional
    public void removeSpellFromCharacter(Long characterId, Long spellId, Long callerId) {
        loadCharacterForEdit(characterId, callerId);
        if (!characterSpellRepository.existsByCharacter_IdAndSpell_Id(characterId, spellId)) {
            throw new NoSuchElementException("Spell is not in this character's spellbook.");
        }
        characterSpellRepository.deleteByCharacter_IdAndSpell_Id(characterId, spellId);
    }

    @Override
    @Transactional
    public void assignSpellsToCharacter(Character character, List<Long> spellIds) {
        if (spellIds == null || spellIds.isEmpty()) {
            return;
        }
        for (Long spellId : spellIds) {
            Spell spell = spellRepository.findById(spellId)
                    .orElseThrow(() -> new NoSuchElementException("Spell not found: " + spellId));
            CharacterSpell link = new CharacterSpell();
            link.setCharacter(character);
            link.setSpell(spell);
            characterSpellRepository.save(link);
        }
    }

    @Override
    public void validateCreationSpells(Long classId, Long subraceId, List<Long> spellIds) {
        DndClass dndClass = dndClassRepository.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("Class not found."));

        boolean isWizard = WIZARD_CLASS_INDEX.equals(dndClass.getIndex());
        boolean isHighElf = subraceId != null && subraceId.equals(HIGH_ELF_SUBRACE_ID);
        boolean requiresSpells = isWizard || isHighElf;

        List<Long> ids = spellIds != null ? spellIds : List.of();

        if (!requiresSpells) {
            if (!ids.isEmpty()) {
                throw new IllegalArgumentException("This character cannot have spells.");
            }
            return;
        }

        if (ids.isEmpty()) {
            throw new IllegalArgumentException("Spell selection is required for this character.");
        }

        Set<String> wizardIndices = getWizardSpellIndices();
        List<Spell> selectedSpells = spellRepository.findAllById(ids);
        if (selectedSpells.size() != ids.size()) {
            throw new IllegalArgumentException("One or more selected spells were not found.");
        }

        for (Spell spell : selectedSpells) {
            if (spell.getApiIndex() == null || !wizardIndices.contains(spell.getApiIndex())) {
                throw new IllegalArgumentException("Spell is not on the wizard spell list: " + spell.getName());
            }
        }

        long cantripCount = selectedSpells.stream().filter(spell -> spell.getLevel() == 0).count();
        long levelOneCount = selectedSpells.stream().filter(spell -> spell.getLevel() == 1).count();

        if (isWizard) {
            int requiredCantrips = isHighElf ? 4 : 3;
            if (cantripCount != requiredCantrips) {
                throw new IllegalArgumentException(
                        "Wizard must select exactly " + requiredCantrips + " cantrips.");
            }
            if (levelOneCount != 6) {
                throw new IllegalArgumentException("Wizard must select exactly 6 level-1 spells.");
            }
            if (selectedSpells.size() != requiredCantrips + 6) {
                throw new IllegalArgumentException("Wizard spell selection must be cantrips and level-1 spells only.");
            }
            return;
        }

        if (cantripCount != 1 || levelOneCount != 0 || selectedSpells.size() != 1) {
            throw new IllegalArgumentException("High Elf must select exactly 1 wizard cantrip.");
        }
    }

    @Override
    public Set<String> getWizardSpellIndices() {
        if (wizardSpellIndicesCache == null) {
            synchronized (this) {
                if (wizardSpellIndicesCache == null) {
                    wizardSpellIndicesCache = Set.copyOf(dnd5eApiClient.fetchClassSpellIndices(WIZARD_CLASS_INDEX));
                }
            }
        }
        return wizardSpellIndicesCache;
    }

    @Transactional
    protected void syncAllSpellsIfEmpty() {
        if (spellRepository.countByApiIndexIsNotNull() > 0) {
            return;
        }
        synchronized (this) {
            if (spellRepository.countByApiIndexIsNotNull() > 0) {
                return;
            }
            List<String> indices = dnd5eApiClient.fetchAllSpellIndices();
            List<Spell> toSave = new ArrayList<>();
            for (String apiIndex : indices) {
                if (spellRepository.findByApiIndex(apiIndex).isPresent()) {
                    continue;
                }
                Dnd5eSpellResponse response = dnd5eApiClient.fetchSpell(apiIndex);
                if (response != null) {
                    toSave.add(SpellMapper.toEntity(response));
                }
            }
            spellRepository.saveAll(toSave);
        }
    }

    private Spell findOrFetchByApiIndex(String apiIndex) {
        return spellRepository.findByApiIndex(apiIndex)
                .orElseGet(() -> {
                    Dnd5eSpellResponse response = dnd5eApiClient.fetchSpell(apiIndex);
                    if (response == null) {
                        throw new NoSuchElementException("Spell not found: " + apiIndex);
                    }
                    return spellRepository.save(SpellMapper.toEntity(response));
                });
    }

    private Set<String> getWizardSpellIndicesIfWizard(String classIndex) {
        if (WIZARD_CLASS_INDEX.equals(classIndex)) {
            return getWizardSpellIndices();
        }
        return dnd5eApiClient.fetchClassSpellIndices(classIndex);
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
        throw new AccessDeniedException("You do not have permission to view this character's spells.");
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
