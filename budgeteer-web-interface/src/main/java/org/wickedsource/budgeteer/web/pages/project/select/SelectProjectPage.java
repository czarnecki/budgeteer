package org.wickedsource.budgeteer.web.pages.project.select;

import de.adesso.budgeteer.core.project.ProjectNameAlreadyInUseException;
import de.adesso.budgeteer.core.project.port.in.CreateProjectUseCase;
import de.adesso.budgeteer.core.project.port.in.GetDefaultProjectUseCase;
import de.adesso.budgeteer.core.project.port.in.GetUsersProjectsUseCase;
import de.adesso.budgeteer.core.project.port.in.UpdateDefaultProjectUseCase;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wickedsource.budgeteer.web.BudgeteerSession;
import org.wickedsource.budgeteer.web.Mount;
import org.wickedsource.budgeteer.web.components.customFeedback.CustomFeedbackPanel;
import org.wickedsource.budgeteer.web.pages.base.dialogpage.DialogPageWithBacklink;
import org.wickedsource.budgeteer.web.pages.dashboard.DashboardPage;
import org.wickedsource.budgeteer.web.pages.project.model.WebProjectMapper;
import org.wickedsource.budgeteer.web.pages.project.model.WebProject;
import org.wickedsource.budgeteer.web.pages.user.login.LoginPage;

import java.io.Serializable;

@Mount("/selectProject")
public class SelectProjectPage extends DialogPageWithBacklink {

    @SpringBean
    private CreateProjectUseCase createProjectUseCase;

    @SpringBean
    private GetDefaultProjectUseCase getDefaultProjectUseCase;

    @SpringBean
    private GetUsersProjectsUseCase getUsersProjectsUseCase;

    @SpringBean
    private UpdateDefaultProjectUseCase updateDefaultProjectUseCase;

    @SpringBean
    private WebProjectMapper webProjectMapper;

    private final CustomFeedbackPanel feedbackPanel;

    public SelectProjectPage() {
        this(LoginPage.class, new PageParameters());
    }

    public SelectProjectPage(Class<? extends WebPage> backlinkPage, PageParameters backlinkParameters) {
        super(backlinkPage, backlinkParameters);

        add(createBacklink("backlink1"));
        add(createLogoutlink());
        add(createNewProjectForm());
        add(createChooseProjectForm());
        var feedbackForm = new Form<String>("feedbackForm", new Model<>());
        feedbackPanel = new CustomFeedbackPanel("feedback");
        feedbackPanel.setOutputMarkupId(true);
        feedbackForm.add(feedbackPanel);
        add(feedbackForm);
    }

    /**
     * Construct a new SelectProjectPage and show a error message.
     *
     * @param propertyKey A key to a String property of the error message
     *                    you want to display.
     * @see #error(Serializable)
     */
    public SelectProjectPage(String propertyKey) {
        this();

        if (propertyKey != null && !propertyKey.isEmpty()) {
            this.error(this.getString(propertyKey));
        }
    }

    private Form<String> createNewProjectForm() {
        var form = new Form<String>("newProjectForm", new Model<>()) {
            @Override
            protected void onSubmit() {
                try {
                    var project = webProjectMapper.toWebProject(createProjectUseCase.createProject(new CreateProjectUseCase.CreateProjectCommand(getModelObject(), BudgeteerSession.get().getLoggedInUser().getId())));
                    BudgeteerSession.get().setProjectId(project.getId());
                    setResponsePage(DashboardPage.class);
                } catch (ProjectNameAlreadyInUseException exception) {
                    this.error(getString("newProjectForm.projectName.AlreadyInUse"));
                }

            }
        };
        form.add(new RequiredTextField<>("projectName", form.getModel()));
        return form;
    }

    private Form<WebProject> createChooseProjectForm() {
        var defaultProject = Model.of(getDefaultProjectUseCase.getDefaultProject(BudgeteerSession.get().getLoggedInUser().getId()).map(webProjectMapper::toWebProject).orElse(null));
        var form = new Form<>("chooseProjectForm", defaultProject);
        form.setOutputMarkupId(true);
        var choice = new DropDownChoice<>("projectChoice", form.getModel(),
                () -> webProjectMapper.toWebProject(getUsersProjectsUseCase.getUsersProjects(BudgeteerSession.get().getLoggedInUser().getId())),
                new ProjectChoiceRenderer());
        choice.setRequired(true);
        choice.add(new OnChangeAjaxBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(feedbackPanel);
            }
        });
        form.add(choice);

        AjaxSubmitLink markProjectAsDefault = new AjaxSubmitLink("markProject") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                try {
                    updateDefaultProjectUseCase.updateDefaultProject(BudgeteerSession.get().getLoggedInUser().getId(), form.getModelObject().getId());
                    info(getString("chooseProjectForm.defaultProject.successful"));
                } catch (Exception e) {
                    error(getString("chooseProjectForm.defaultProject.failed"));
                }
                target.add(form, feedbackPanel);
            }
        };

        AjaxSubmitLink goButton = new AjaxSubmitLink("goButton") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                BudgeteerSession.get().setProjectId(form.getModelObject().getId());
                setResponsePage(DashboardPage.class);
            }
        };
        form.add(markProjectAsDefault);
        form.add(goButton);
        return form;
    }

    private Link<Void> createLogoutlink() {
        return new Link<>("logoutLink") {
            @Override
            public void onClick() {
                BudgeteerSession.get().logout();
                setResponsePage(LoginPage.class);
            }
        };
    }

}
