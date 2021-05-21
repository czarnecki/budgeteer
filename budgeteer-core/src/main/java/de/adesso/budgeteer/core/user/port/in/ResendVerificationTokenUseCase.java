package de.adesso.budgeteer.core.user.port.in;

import de.adesso.budgeteer.core.user.EmailAlreadyVerifiedException;

public interface ResendVerificationTokenUseCase {
    void resendVerificationToken(long userId) throws EmailAlreadyVerifiedException;
}
