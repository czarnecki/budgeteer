package org.wickedsource.budgeteer.web.pages.project.administration;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class WebUserWithEmail extends WebUser {

    private String email;
    private String currentPassword;
    private String newPassword;
    private String newPasswordConfirmation;

    public WebUserWithEmail(long id, String name, String email) {
        super(id, name);
        this.email = email;
    }
}
