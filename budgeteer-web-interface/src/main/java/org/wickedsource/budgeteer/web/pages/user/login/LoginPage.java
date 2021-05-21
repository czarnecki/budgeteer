package org.wickedsource.budgeteer.web.pages.user.login;

import de.adesso.budgeteer.core.user.InvalidLoginCredentialsException;
import de.adesso.budgeteer.core.user.port.in.LoginUseCase;
import de.adesso.budgeteer.core.user.port.in.VerifyEmailUseCase;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wickedsource.budgeteer.service.user.TokenStatus;
import org.wickedsource.budgeteer.service.user.UserService;
import org.wickedsource.budgeteer.web.BudgeteerSession;
import org.wickedsource.budgeteer.web.BudgeteerSettings;
import org.wickedsource.budgeteer.web.Mount;
import org.wickedsource.budgeteer.web.components.customFeedback.CustomFeedbackPanel;
import org.wickedsource.budgeteer.web.pages.base.dialogpage.DialogPage;
import org.wickedsource.budgeteer.web.pages.project.administration.WebUserMapper;
import org.wickedsource.budgeteer.web.pages.project.select.SelectProjectPage;
import org.wickedsource.budgeteer.web.pages.user.forgotpassword.ForgotPasswordPage;
import org.wickedsource.budgeteer.web.pages.user.register.RegisterPage;
import org.wickedsource.budgeteer.web.pages.user.resettoken.ResetTokenPage;

import static org.wicketstuff.lazymodel.LazyModel.from;
import static org.wicketstuff.lazymodel.LazyModel.model;

@Mount("/login")
public class LoginPage extends DialogPage {

    @SpringBean
    private UserService userService;

    @SpringBean
    private VerifyEmailUseCase verifyEmailUseCase;

    @SpringBean
    private LoginUseCase loginUseCase;

    @SpringBean
    private WebUserMapper webUserMapper;

    @SpringBean
    private BudgeteerSettings settings;

    public LoginPage() {
        build();
    }

    public LoginPage(PageParameters pageParameters) {
        var verificationToken = pageParameters.get("verificationtoken").toString();

        try {
            verifyEmailUseCase.verifyEmail(verificationToken);
        } catch (IllegalArgumentException e) {
            error("token invalid");
            error("token expired");
        }

        if (!hasErrorMessage()) {
            success(getString("message.tokenValid"));
        }

        build();
    }

    private void build() {
        Form<LoginCredentials> form = new Form<>("loginForm", model(from(new LoginCredentials()))) {
            @Override
            protected void onSubmit() {
                try {
                    var user = webUserMapper.toWebUser(loginUseCase.login(getModelObject().getUsername(), getModelObject().getPassword()));
                    BudgeteerSession.get().login(user);
                    SelectProjectPage nextPage = new SelectProjectPage(LoginPage.class, getPageParameters());
                    setResponsePage(nextPage);
                } catch (InvalidLoginCredentialsException e) {
                    error(getString("message.invalidLogin"));
                }
            }
        };
        add(form);

        form.add(new CustomFeedbackPanel("feedback"));
        form.add(new RequiredTextField<>("username", model(from(form.getModel()).getUsername())));
        form.add(new PasswordTextField("password", model(from(form.getModel()).getPassword())));
        form.add(new BookmarkablePageLink<RegisterPage>("registerLink", RegisterPage.class));
        form.add(new BookmarkablePageLink<ForgotPasswordPage>("forgotPasswordLink", ForgotPasswordPage.class));
    }
}
