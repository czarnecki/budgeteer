package de.adesso.budgeteer.core.user.port.in;

import de.adesso.budgeteer.core.user.ExpiredForgottenPasswordToken;
import de.adesso.budgeteer.core.user.InvalidForgottenPasswordToken;
import lombok.Value;

public interface ChangeForgottenPasswordUseCase {
    void changeForgottenPassword(ChangeForgottenPasswordCommand command) throws InvalidForgottenPasswordToken, ExpiredForgottenPasswordToken;

    @Value
    class ChangeForgottenPasswordCommand {
        String token;
        String password;
    }
}
