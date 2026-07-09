<script setup lang="ts">
import CharacterCard from '@/components/CharacterCard.vue'
</script>

<template>
  <div class="grid grid-cols-[repeat(auto-fill,minmax(18rem,1fr))] gap-4">
    <CharacterCard
      name="John Doe"
      raceName="human"
      className="fighter"
      :campaigns="['Epic Quest']"
    />
    <CharacterCard
      name="Jane Smith"
      raceName="elf"
      className="fighter"
      :campaigns="['Mystic Journey']"
    />
    <CharacterCard
      name="Bob Johnson"
      raceName="dwarf"
      className="fighter"
      :campaigns="['Epic Quest', 'Mystic Journey']"
    />
    <CharacterCard
      name="Barg Bons"
      raceName="human"
      className="wizard"
      :campaigns="['Epic Quest']"
    />
    <CharacterCard
      name="Janice Smoth"
      raceName="elf"
      className="wizard"
      :campaigns="['Mystic Journey']"
    />
    <CharacterCard
      name="Lala Poppins"
      raceName="dwarf"
      className="wizard"
      :campaigns="['Epic Quest', 'Mystic Journey']"
    />
    <CharacterCard
      name="Beef Stew"
      raceName="human"
      className="rogue"
      :campaigns="['Epic Quest']"
    />
    <CharacterCard
      name="Boogle Wonton"
      raceName="elf"
      className="rogue"
      :campaigns="['Mystic Journey']"
    />
    <CharacterCard
      name="Shawn White"
      raceName="dwarf"
      className="rogue"
      :campaigns="['Epic Quest', 'Mystic Journey']"
    />
  </div>
</template>
