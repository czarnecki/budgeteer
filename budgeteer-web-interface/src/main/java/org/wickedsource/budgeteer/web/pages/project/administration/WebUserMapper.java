package org.wickedsource.budgeteer.web.pages.project.administration;

import de.adesso.budgeteer.core.user.domain.User;
import de.adesso.budgeteer.core.user.domain.UserWithEmail;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class WebUserMapper {
    public WebUser toWebUser(User user) {
        return new WebUser(user.getId(), user.getName());
    }

    public WebUserWithEmail toWebUserWithEmail(UserWithEmail userWithEmail) {
        return new WebUserWithEmail(userWithEmail.getId(), userWithEmail.getName(), userWithEmail.getEmail());
    }

    public List<WebUser> toWebUser(List<User> users) {
        return users.stream().map(this::toWebUser).collect(Collectors.toList());
    }
}
