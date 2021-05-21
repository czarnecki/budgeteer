package org.wickedsource.budgeteer.web.pages.user.forgotpassword;

import de.adesso.budgeteer.core.user.MailNotEnabledException;
import de.adesso.budgeteer.core.user.MailNotFoundException;
import de.adesso.budgeteer.core.user.MailNotVerifiedException;
import de.adesso.budgeteer.core.user.port.in.ResetPasswordUseCase;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.html.form.EmailTextField;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wickedsource.budgeteer.web.Mount;
import org.wickedsource.budgeteer.web.components.customFeedback.CustomFeedbackPanel;
import org.wickedsource.budgeteer.web.pages.base.dialogpage.DialogPage;
import org.wickedsource.budgeteer.web.pages.user.login.LoginPage;

import static org.wicketstuff.lazymodel.LazyModel.from;
import static org.wicketstuff.lazymodel.LazyModel.model;

@Mount("/forgotpassword")
public class ForgotPasswordPage extends DialogPage {

    @SpringBean
    private ResetPasswordUseCase resetPasswordUseCase;

    public ForgotPasswordPage() {
        Injector.get().inject(this);
        Form<ForgotPasswordData> form = new Form<ForgotPasswordData>("forgotPasswordForm", model(from(new ForgotPasswordData()))) {
            @Override
            protected void onSubmit() {
                try {
                    resetPasswordUseCase.resetPassword(getModelObject().getMail());
                    success(getString("message.success"));
                } catch (MailNotFoundException e) {
                    error(getString("message.mailNotFound"));
                } catch (MailNotVerifiedException e) {
                    error(getString("message.mailNotVerified"));
                } catch (MailNotEnabledException e) {
                    error("Email support is not enabled");
                }
            }
        };
        add(form);
        form.add(new CustomFeedbackPanel("feedback"));
        form.add(new EmailTextField("mail", model(from(form.getModel()).getMail())).setRequired(true));
        form.add(new BookmarkablePageLink<LoginPage>("loginLink", LoginPage.class));
    }
}
