package de.adesso.budgeteer.core.user.port.in;

import de.adesso.budgeteer.core.user.domain.User;

public interface GetUserUseCase {
    User getUser(long userId);
}
