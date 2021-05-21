package org.wickedsource.budgeteer.persistence.user;

import org.springframework.data.repository.CrudRepository;

public interface ForgotPasswordTokenRepository extends CrudRepository<ForgotPasswordTokenEntity, Long> {

    boolean existsByToken(String token);

    ForgotPasswordTokenEntity findByToken(String token);

    void deleteByToken(String token);
}
