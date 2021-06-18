package de.adesso.budgeteer.core.user.port.out;

import java.time.LocalDateTime;

public interface GetVerificationTokenExpirationDate {
    LocalDateTime getVerificationTokenExpirationDate(String token);
}
