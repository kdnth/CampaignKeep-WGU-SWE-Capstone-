package com.kdnth.campaignkeep;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Integration-style smoke test: boots the full Spring context against a real datasource.
 * Requires the same env vars as a normal app start ({@code DB_URL} must be a JDBC URL).
 * Without them, this class is skipped so Mockito unit suites can still run in isolation.
 */
@SpringBootTest
@EnabledIfEnvironmentVariable(named = "DB_URL", matches = "jdbc:.*")
class CampaignkeepApplicationTests {

	@Test
	void contextLoads() {
	}

}
