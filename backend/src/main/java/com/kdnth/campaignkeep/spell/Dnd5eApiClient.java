package com.kdnth.campaignkeep.spell;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class Dnd5eApiClient {

    private final RestClient restClient;

    public Dnd5eApiClient(
            @Value("${dnd5eapi.base-url:https://www.dnd5eapi.co/api/2014}") String baseUrl,
            RestClient.Builder restClientBuilder
    ) {
        this.restClient = restClientBuilder
                .baseUrl(baseUrl)
                .build();
    }

    public List<String> fetchAllSpellIndices() {
        Dnd5eApiIndexList response = restClient.get()
                .uri("/spells")
                .retrieve()
                .body(Dnd5eApiIndexList.class);
        if (response == null || response.results() == null) {
            return List.of();
        }
        return response.results().stream()
                .map(Dnd5eApiIndexItem::index)
                .toList();
    }

    public Set<String> fetchClassSpellIndices(String classIndex) {
        Dnd5eApiIndexList response = restClient.get()
                .uri("/classes/{classIndex}/spells", classIndex)
                .retrieve()
                .body(Dnd5eApiIndexList.class);
        if (response == null || response.results() == null) {
            return Set.of();
        }
        return response.results().stream()
                .map(Dnd5eApiIndexItem::index)
                .collect(Collectors.toSet());
    }

    public Dnd5eSpellResponse fetchSpell(String apiIndex) {
        return restClient.get()
                .uri("/spells/{apiIndex}", apiIndex)
                .retrieve()
                .body(Dnd5eSpellResponse.class);
    }
}
