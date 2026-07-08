package com.kdnth.campaignkeep.character;

import com.kdnth.campaignkeep.background.Background;
import com.kdnth.campaignkeep.background.BackgroundRepository;
import com.kdnth.campaignkeep.base.AccessDeniedException;
import com.kdnth.campaignkeep.campaign.CampaignMemberRepository;
import com.kdnth.campaignkeep.campaign.CampaignNonplayableCharacterRepository;
import com.kdnth.campaignkeep.campaign.CampaignPlayableCharacterRepository;
import com.kdnth.campaignkeep.campaign.CampaignService;
import com.kdnth.campaignkeep.dndclass.DndClass;
import com.kdnth.campaignkeep.language.Language;
import com.kdnth.campaignkeep.language.LanguageRepository;
import com.kdnth.campaignkeep.race.Race;
import com.kdnth.campaignkeep.race.RaceAbilityPointBonus;
import com.kdnth.campaignkeep.race.RaceAbilityPointBonusRepository;
import com.kdnth.campaignkeep.race.RaceRepository;
import com.kdnth.campaignkeep.race.Subrace;
import com.kdnth.campaignkeep.race.SubraceAbilityPointBonus;
import com.kdnth.campaignkeep.race.SubraceAbilityPointBonusRepository;
import com.kdnth.campaignkeep.race.SubraceRepository;
import com.kdnth.campaignkeep.user.User;
import com.kdnth.campaignkeep.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CharacterServiceImpl implements CharacterService {

    private final RaceAbilityPointBonusRepository raceAbilityPointBonusRepository;
    private final SubraceAbilityPointBonusRepository subraceAbilityPointBonusRepository;
    private final UserRepository userRepository;
    private final RaceRepository raceRepository;
    private final SubraceRepository subraceRepository;
    private final BackgroundRepository backgroundRepository;
    private final CharacterRepository characterRepository;
    private final CharacterClassRepository characterClassRepository;
    private final CharacterLanguageRepository characterLanguageRepository;
    private final LanguageRepository languageRepository;
    private final CampaignMemberRepository campaignMemberRepository;
    private final CampaignPlayableCharacterRepository campaignPlayableCharacterRepository;
    private final CampaignNonplayableCharacterRepository campaignNonplayableCharacterRepository;
    private final CampaignService campaignService;

    public CharacterServiceImpl(RaceAbilityPointBonusRepository raceAbilityPointBonusRepository,
                                SubraceAbilityPointBonusRepository subraceAbilityPointBonusRepository,
                                UserRepository userRepository, RaceRepository raceRepository,
                                SubraceRepository subraceRepository, BackgroundRepository backgroundRepository,
                                CharacterRepository characterRepository,
                                CharacterClassRepository characterClassRepository,
                                CharacterLanguageRepository characterLanguageRepository,
                                LanguageRepository languageRepository,
                                CampaignMemberRepository campaignMemberRepository,
                                CampaignPlayableCharacterRepository campaignPlayableCharacterRepository,
                                CampaignNonplayableCharacterRepository campaignNonplayableCharacterRepository,
                                CampaignService campaignService) {
        this.raceAbilityPointBonusRepository = raceAbilityPointBonusRepository;
        this.subraceAbilityPointBonusRepository = subraceAbilityPointBonusRepository;
        this.userRepository = userRepository;
        this.raceRepository = raceRepository;
        this.subraceRepository = subraceRepository;
        this.backgroundRepository = backgroundRepository;
        this.characterRepository = characterRepository;
        this.characterClassRepository = characterClassRepository;
        this.characterLanguageRepository = characterLanguageRepository;
        this.languageRepository = languageRepository;
        this.campaignMemberRepository = campaignMemberRepository;
        this.campaignPlayableCharacterRepository = campaignPlayableCharacterRepository;
        this.campaignNonplayableCharacterRepository = campaignNonplayableCharacterRepository;
        this.campaignService = campaignService;
    }

    @Override
    public PlayableCharacter createPlayableCharacter(CreatePlayableCharacterRequest request, Long callerId) {
        User player = userRepository.findById(callerId)
                .orElseThrow(() -> new AccessDeniedException("Player not found"));
        Race race = raceRepository.findById(request.raceId())
                .orElseThrow(() -> new AccessDeniedException("Race not found"));
        Subrace subrace = request.subraceId() != null ? subraceRepository.findById(request.subraceId())
                .orElseThrow(() -> new AccessDeniedException("Subrace not found")) : null;
        Background background = request.backgroundId() != null ? backgroundRepository.findById(request.backgroundId())
                .orElseThrow(() -> new AccessDeniedException("Background not found")) : null;

        PlayableCharacter character = new PlayableCharacter();
        character.setPlayer(player);
        character.setRace(race);
        character.setSubrace(subrace);
        character.setBackground(background);
        character.setStatus(CharacterStatus.alive);

        character.setStrength(request.strength());
        character.setDexterity(request.dexterity());
        character.setConstitution(request.constitution());
        character.setIntelligence(request.intelligence());
        character.setWisdom(request.wisdom());
        character.setCharisma(request.charisma());

        applyRacialBonuses(character, race, subrace);

        character.setHitPoints(request.hitPoints());
        character.setArmorClass(request.armorClass());
        character.setInitiativeBonus(calculateModifier(character.getDexterity()));
        character.setSpeed(race.getSpeed());

        return characterRepository.save(character);
    }

    private short calculateModifier(short abilityScore) {
        return (short) Math.floorDiv(abilityScore - 10, 2);
    }

    @Override
    @Transactional
    public NonplayableCharacter createNonplayableCharacter(CreateNonplayableCharacterRequest request, Long callerId) {
        User creator = userRepository.findById(callerId)
                                     .orElseThrow(() -> new NoSuchElementException("User not found."));
        Race race = findRace(request.raceId());
        Subrace subrace = findSubrace(request.subraceId());
        Background background = findBackground(request.backgroundId());

        NonplayableCharacter character = new NonplayableCharacter();
        character.setCreatedBy(creator);
        populateSharedFields(character, request.name(), race, subrace, background,
                             request.strength(), request.dexterity(), request.constitution(),
                             request.intelligence(), request.wisdom(), request.charisma(),
                             request.hitPoints(), request.armorClass());

        NonplayableCharacter saved = characterRepository.save(character);

        assignLanguages(saved, request.languageIds());

        return saved;
    }

    @Override
    public Character getCharacter(Long characterId, Long callerId) {
        Character character = characterRepository.findById(characterId)
                                                 .orElseThrow(() -> new NoSuchElementException("Character not found."));
        requireOwner(character, callerId);
        return character;
    }

    @Override
    @Transactional
    public void deleteCharacter(Long characterId, Long callerId) {
        Character character = characterRepository.findById(characterId)
                                                 .orElseThrow(() -> new NoSuchElementException("Character not found."));
        requireOwner(character, callerId);

        campaignService.detachCharacterFromAllCampaigns(characterId);

        characterRepository.delete(character);
    }

    @Override
    public Character getCharacterForCampaignView(Long characterId, Long campaignId, Long callerId) {
        campaignService.requireMaster(campaignId, callerId);

        boolean attached = campaignPlayableCharacterRepository.existsByCharacterIdAndCampaignId(campaignId, characterId) || campaignNonplayableCharacterRepository.existsByCharacterIdAndCampaignId(campaignId, characterId);
        if (!attached) {
            throw new NoSuchElementException("Character is not part of this campaign");
        }

        return characterRepository.findById(characterId).orElseThrow(() -> new NoSuchElementException("Character not found."));
    }

    @Override
    public CharacterResponse toResponse(Character character) {
        List<CharacterClass> classes = characterClassRepository.findByCharacterId(character.getId());
        List<CharacterLanguage> languages = characterLanguageRepository.findByCharacterId(character.getId());
        return CharacterMapper.toResponse(character, classes, languages);
    }

    @Override
    public List<CharacterResponse> toResponseList(List<? extends Character> characters) {
        return characters.stream().map(this::toResponse).toList();
    }

    private void populateSharedFields(
            Character character, String name, Race race, Subrace subrace, Background background,
            Short strength, Short dexterity, Short constitution,
            Short intelligence, Short wisdom, Short charisma,
            Short hitPoints, Short armorClass
    ) {
        character.setName(name);
        character.setRace(race);
        character.setSubrace(subrace);
        character.setBackground(background);
        character.setStatus(CharacterStatus.alive);

        character.setStrength(strength);
        character.setDexterity(dexterity);
        character.setConstitution(constitution);
        character.setIntelligence(intelligence);
        character.setWisdom(wisdom);
        character.setCharisma(charisma);

        applyRacialBonuses(character, race, subrace);

        character.setHitPoints(hitPoints);
        character.setArmorClass(armorClass);
        character.setInitiativeBonus(calculateModifier(character.getDexterity()));
        character.setSpeed(race.getSpeed());
    }

    private void assignClass(PlayableCharacter character, DndClass dndClass) {
        CharacterClass characterClass = new CharacterClass();
        characterClass.setCharacter(character);
        characterClass.setDndClass(dndClass);
        characterClassRepository.save(characterClass);
    }

    private void assignLanguages(Character character, List<Long> languageIds) {
        if (languageIds == null || languageIds.isEmpty()) {
            return;
        }
        for (Long languageId : languageIds) {
            Language language = languageRepository.findById(languageId)
                                                  .orElseThrow(() -> new NoSuchElementException("Language not found: " + languageId));
            CharacterLanguage characterLanguage = new CharacterLanguage();
            characterLanguage.setCharacter(character);
            characterLanguage.setLanguage(language);
            characterLanguageRepository.save(characterLanguage);
        }
    }

    private Race findRace(Long raceId) {
        return raceRepository.findById(raceId)
                             .orElseThrow(() -> new NoSuchElementException("Race not found."));
    }

    private Subrace findSubrace(Long subraceId) {
        if (subraceId == null) {
            return null;
        }
        return subraceRepository.findById(subraceId)
                                .orElseThrow(() -> new NoSuchElementException("Subrace not found."));
    }

    private Background findBackground(Long backgroundId) {
        if (backgroundId == null) {
            return null;
        }
        return backgroundRepository.findById(backgroundId)
                                   .orElseThrow(() -> new NoSuchElementException("Background not found."));
    }

    private void requireOwner(Character character, Long callerId) {
        if (!character.canBeEditedBy(callerId)) {
            throw new AccessDeniedException("You do not have permission to edit this character.");
        }
    }
    
    private void applyRacialBonuses(Character character, Race race, Subrace subrace) {
        List<RaceAbilityPointBonus> raceBonuses = raceAbilityPointBonusRepository.findByRaceId(race.getId());
        for (RaceAbilityPointBonus bonus : raceBonuses) {
            applyBonus(character, bonus.getAbility().getName(), bonus.getValue());
        }

        if (subrace != null) {
            List<SubraceAbilityPointBonus> subraceBonuses = subraceAbilityPointBonusRepository.findBySubraceId(
                    subrace.getId());
            for (SubraceAbilityPointBonus bonus : subraceBonuses) {
                applyBonus(character, bonus.getAbility().getName(), bonus.getValue());
            }
        }
    }

    private void applyBonus(Character character, String abilityName, Short value) {
        switch (abilityName.toUpperCase()) {
            case "STR" -> character.setStrength((short) (character.getStrength() + value));
            case "DEX" -> character.setDexterity((short) (character.getDexterity() + value));
            case "CON" -> character.setConstitution((short) (character.getConstitution() + value));
            case "INT" -> character.setIntelligence((short) (character.getIntelligence() + value));
            case "WIS" -> character.setWisdom((short) (character.getWisdom() + value));
            case "CHA" -> character.setCharisma((short) (character.getCharisma() + value));
            default -> throw new IllegalArgumentException("Invalid ability name: " + abilityName);
        }


    }


}
