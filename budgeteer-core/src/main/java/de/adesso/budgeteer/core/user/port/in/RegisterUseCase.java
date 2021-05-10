package de.adesso.budgeteer.core.user.port.in;

public interface RegisterUseCase {
    void register(String username, String mail, String password);
}
