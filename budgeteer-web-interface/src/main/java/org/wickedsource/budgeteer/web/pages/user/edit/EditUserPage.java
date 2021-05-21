package org.wickedsource.budgeteer.web.pages.user.edit;

import de.adesso.budgeteer.core.user.port.in.GetUserWithEmailUseCase;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wickedsource.budgeteer.service.user.EditUserData;
import org.wickedsource.budgeteer.service.user.UserService;
import org.wickedsource.budgeteer.web.Mount;
import org.wickedsource.budgeteer.web.pages.base.dialogpage.DialogPageWithBacklink;
import org.wickedsource.budgeteer.web.pages.project.administration.WebUserMapper;
import org.wickedsource.budgeteer.web.pages.user.edit.edituserform.EditUserForm;

import static org.wicketstuff.lazymodel.LazyModel.from;
import static org.wicketstuff.lazymodel.LazyModel.model;

@Mount({"user/edit/${id}"})
public class EditUserPage extends DialogPageWithBacklink {

    @SpringBean
    private GetUserWithEmailUseCase getUserWithEmailUseCase;

    @SpringBean
    private WebUserMapper webUserMapper;

    public EditUserPage(Class<? extends WebPage> backlinkPage, PageParameters backlinkParameters) {
        super(backlinkPage, backlinkParameters);
        addComponents(backlinkParameters);
    }

    private void addComponents(PageParameters backlinkParameters) {
        var userWithEmail = webUserMapper.toWebUserWithEmail(getUserWithEmailUseCase.getUserWithEmail(backlinkParameters.get("userId").toLong()));
        var form = new EditUserForm("form", Model.of(userWithEmail));
        add(form);
        add(createBacklink("backlink1"));
        form.add(createBacklink("backlink2"));
    }
}
