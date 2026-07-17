package com.kdnth.campaignkeep.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountTokenRepository extends JpaRepository<AccountToken, Long> {

    Optional<AccountToken> findByTokenHashAndType(String tokenHash, AccountTokenType type);

    Optional<AccountToken> findFirstByUserAndTypeOrderByCreatedOnDesc(User user, AccountTokenType type);

    List<AccountToken> findByUserAndTypeAndConsumedAtIsNull(User user, AccountTokenType type);
}
