package de.adesso.budgeteer.core.user.service;

import de.adesso.budgeteer.core.user.port.in.VerifyEmailUseCase;
import de.adesso.budgeteer.core.user.port.out.DeleteVerificationTokenByTokenPort;
import de.adesso.budgeteer.core.user.port.out.VerificationTokenExistsPort;
import de.adesso.budgeteer.core.user.port.out.GetVerificationTokenExpirationDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VerifyEmailService implements VerifyEmailUseCase {

    private final VerificationTokenExistsPort verificationTokenExistsPort;
    private final GetVerificationTokenExpirationDate getVerificationTokenExpirationDate;
    private final DeleteVerificationTokenByTokenPort deleteVerificationTokenByTokenPort;

    @Override
    public void verifyEmail(String token) {
        if (!verificationTokenExistsPort.verificationTokenExists(token)) {
            throw new IllegalArgumentException("invalid token");
        }
        if (getVerificationTokenExpirationDate.getVerificationTokenExpirationDate(token).isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("expired token");
        }
        deleteVerificationTokenByTokenPort.deleteVerificationTokenByToken(token);
    }
}
