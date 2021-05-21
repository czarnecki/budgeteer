package org.wickedsource.budgeteer.web.pages.user.edit.edituserform;

import de.adesso.budgeteer.core.user.InvalidLoginCredentialsException;
import de.adesso.budgeteer.core.user.MailAlreadyInUseException;
import de.adesso.budgeteer.core.user.UsernameAlreadyInUseException;
import de.adesso.budgeteer.core.user.port.in.UpdateUserUseCase;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LambdaModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wickedsource.budgeteer.service.user.UserService;
import org.wickedsource.budgeteer.web.components.customFeedback.CustomFeedbackPanel;
import org.wickedsource.budgeteer.web.pages.project.administration.WebUser;
import org.wickedsource.budgeteer.web.pages.project.administration.WebUserWithEmail;

import static org.wicketstuff.lazymodel.LazyModel.from;
import static org.wicketstuff.lazymodel.LazyModel.model;

public class EditUserForm extends Form<WebUserWithEmail> {

    @SpringBean
    private UserService userService;

    @SpringBean
    private UpdateUserUseCase updateUserUseCase;

    public EditUserForm(String id, IModel<WebUserWithEmail> model) {
        super(id, model);
        addComponents();
    }

    private void addComponents() {
        CustomFeedbackPanel feedbackPanel = new CustomFeedbackPanel("feedback");
        add(feedbackPanel);

        var usernameRequiredTextField = new RequiredTextField<>("username", LambdaModel.of(getModel(), WebUser::getName, WebUser::setName));
        var emailTextField = new EmailTextField("mail", LambdaModel.of(getModel(), WebUserWithEmail::getEmail, WebUserWithEmail::setEmail));
        var currentPasswordTextField = new PasswordTextField("currentPassword", LambdaModel.of(getModel(), WebUserWithEmail::getCurrentPassword, WebUserWithEmail::setCurrentPassword));
        var newPasswordTextField = new PasswordTextField("newPassword", LambdaModel.of(getModel(), WebUserWithEmail::getNewPassword, WebUserWithEmail::setNewPassword));
        var newPasswordConfirmationTextField = new PasswordTextField("newPasswordConfirmation", LambdaModel.of(getModel(), WebUserWithEmail::getNewPasswordConfirmation, WebUserWithEmail::setNewPasswordConfirmation));

        usernameRequiredTextField.setRequired(true);
        emailTextField.setRequired(true);
        currentPasswordTextField.setRequired(false);
        newPasswordTextField.setRequired(false);
        newPasswordConfirmationTextField.setRequired(false);

        add(usernameRequiredTextField);
        add(emailTextField);
        add(currentPasswordTextField);
        add(newPasswordTextField);
        add(newPasswordConfirmationTextField);

        /*
         * The checks of the input fields must be done manually,
         * because setDefaultFormProcessing cannot be set to true,
         * otherwise the following error will be thrown:
         *
         * Last cause: Attempt to set a model object on a component without a model! Either pass an IModel to the constructor or use #setDefaultModel(new SomeModel(object)). Component: form:actualPassword
         */
        Button submitButton = new Button("submitButton") {
            @Override
            public void onSubmit() {
                EditUserForm.this.onSubmit();
                if (usernameRequiredTextField.getInput().isEmpty()) {
                    error(getString("form.username.Required"));
                    return;
                }

                if (emailTextField.getInput().isEmpty()) {
                    error(getString("form.mail.Required"));
                    return;
                }

                if (currentPasswordTextField.getInput().isEmpty() && (!newPasswordTextField.getInput().isEmpty() || !newPasswordConfirmationTextField.getInput().isEmpty())) {
                    error(getString("form.currentPassword.Required"));
                    return;
                }

                if (!currentPasswordTextField.getInput().isEmpty()) {
                    if (newPasswordTextField.getInput().isEmpty()) {
                        error(getString("form.newPassword.Required"));
                        return;
                    }

                    if (newPasswordConfirmationTextField.getInput().isEmpty()) {
                        error(getString("form.newPasswordConfirmation.Required"));
                        return;
                    }

                    if (!newPasswordTextField.getInput().equals(newPasswordConfirmationTextField.getInput())) {
                        error(getString("message.wrongPasswordConfirmation"));
                        return;
                    }
                }

                var userWithEmail = EditUserForm.this.getModelObject();
                var command = new UpdateUserUseCase.UpdateUserCommand(userWithEmail.getId(),
                        userWithEmail.getName(),
                        userWithEmail.getEmail(),
                        userWithEmail.getCurrentPassword(),
                        userWithEmail.getNewPassword());

                try {
                    updateUserUseCase.updateUser(command);
                } catch (UsernameAlreadyInUseException e) {
                    error(getString("message.duplicateUserName"));
                } catch (MailAlreadyInUseException e) {
                    error(getString("message.duplicateMail"));
                } catch (InvalidLoginCredentialsException e) {
                    error(getString("message.wrongPassword"));
                }
            }
        };
        submitButton.setDefaultFormProcessing(true);
        add(submitButton);
    }
}
