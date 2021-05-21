package org.wickedsource.budgeteer.web.pages.user.resettoken;

import de.adesso.budgeteer.core.user.EmailAlreadyVerifiedException;
import de.adesso.budgeteer.core.user.port.in.ResendVerificationTokenUseCase;
import de.adesso.budgeteer.core.user.port.out.GetUserWithEmailPort;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wickedsource.budgeteer.web.BudgeteerSession;
import org.wickedsource.budgeteer.web.Mount;
import org.wickedsource.budgeteer.web.components.customFeedback.CustomFeedbackPanel;
import org.wickedsource.budgeteer.web.pages.base.dialogpage.DialogPage;
import org.wickedsource.budgeteer.web.pages.dashboard.DashboardPage;

@Mount("resettoken")
public class ResetTokenPage extends DialogPage {

    @SpringBean
    private GetUserWithEmailPort getUserWithEmailPort;

    @SpringBean
    private ResendVerificationTokenUseCase resendVerificationTokenUseCase;

    public ResetTokenPage() {
        addComponents();
    }

    private void addComponents() {
        IModel<String> model = () -> getUserWithEmailPort.getUserWithEmail(BudgeteerSession.get().getLoggedInUser().getId()).getEmail();
        var form = new Form<>("resetTokenForm", model) {
            @Override
            protected void onSubmit() {
                try {
                    resendVerificationTokenUseCase.resendVerificationToken(BudgeteerSession.get().getLoggedInUser().getId());
                } catch (EmailAlreadyVerifiedException e) {
                    error(getString("message.alreadyEnabled"));
                }
            }
        };
        add(form);
        form.add(new CustomFeedbackPanel("feedback"));
        form.add(new Label("mail", () -> getUserWithEmailPort.getUserWithEmail(BudgeteerSession.get().getLoggedInUser().getId()).getEmail()));
        form.add(new Button("submitButton"));
        form.add(new BookmarkablePageLink<>("backlink", DashboardPage.class));
    }
}