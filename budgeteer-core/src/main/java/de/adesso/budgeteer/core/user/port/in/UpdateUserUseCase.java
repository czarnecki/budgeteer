package de.adesso.budgeteer.core.user.port.in;

public interface UpdateUserUseCase {
    void updateUser(UpdateUserCommand command);

    class UpdateUserCommand {
        long id;
        String name;
        String email;
        String currentPassword;
        String newPassword;
        String passwordConfirmation;
    }
}
