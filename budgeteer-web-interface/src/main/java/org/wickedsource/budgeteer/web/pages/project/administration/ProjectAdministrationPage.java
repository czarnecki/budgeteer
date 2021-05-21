package org.wickedsource.budgeteer.web.pages.project.administration;

import de.adesso.budgeteer.core.common.DateRange;
import de.adesso.budgeteer.core.project.port.in.*;
import de.adesso.budgeteer.core.user.port.in.GetUsersInProjectUseCase;
import de.adesso.budgeteer.core.user.port.in.GetUsersNotInProjectUseCase;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LambdaModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wickedsource.budgeteer.web.BudgeteerSession;
import org.wickedsource.budgeteer.web.Mount;
import org.wickedsource.budgeteer.web.components.customFeedback.CustomFeedbackPanel;
import org.wickedsource.budgeteer.web.components.daterange.DateRangePickerBehavior;
import org.wickedsource.budgeteer.web.pages.base.basepage.BasePage;
import org.wickedsource.budgeteer.web.pages.base.basepage.breadcrumbs.BreadcrumbsModel;
import org.wickedsource.budgeteer.web.pages.base.delete.DeleteDialog;
import org.wickedsource.budgeteer.web.pages.dashboard.DashboardPage;
import org.wickedsource.budgeteer.web.pages.project.model.WebProject;
import org.wickedsource.budgeteer.web.pages.project.model.WebProjectMapper;
import org.wickedsource.budgeteer.web.pages.project.model.WebProjectWithDate;
import org.wickedsource.budgeteer.web.pages.project.select.SelectProjectPage;
import org.wickedsource.budgeteer.web.pages.user.login.LoginPage;

import java.util.HashMap;
import java.util.List;

@Mount("/administration")
public class ProjectAdministrationPage extends BasePage {

    @SpringBean
    private GetUsersInProjectUseCase getUsersInProjectUseCase;

    @SpringBean
    private GetUsersNotInProjectUseCase getUsersNotInProjectUseCase;

    @SpringBean
    private AddUserToProjectUseCase addUserToProjectUseCase;

    @SpringBean
    private RemoveUserFromProjectUseCase removeUserFromProjectUseCase;

    @SpringBean
    private GetProjectWithDateUseCase getProjectWithDateUseCase;

    @SpringBean
    private UpdateProjectUseCase updateProjectUseCase;

    @SpringBean
    private DeleteProjectUseCase deleteProjectUseCase;

    @SpringBean
    private WebProjectMapper webProjectMapper;

    @SpringBean
    private WebUserMapper webUserMapper;

    public ProjectAdministrationPage() {
        add(new CustomFeedbackPanel("feedback"));
        add(createUserList(() -> webUserMapper.toWebUser(getUsersInProjectUseCase.getUsersInProject(BudgeteerSession.get().getProjectId()))));
        add(createDeleteProjectButton());
        add(createAddUserForm());
        add(createEditProjectForm());
    }

    private Form<WebProjectWithDate> createEditProjectForm() {
        var form = new Form<>("projectChangeForm", Model.of(webProjectMapper.toWebProjectWithDate(getProjectWithDateUseCase.getProjectWithDate(BudgeteerSession.get().getProjectId())))) {
            @Override
            protected void onSubmit() {
                var project = getModelObject();
                updateProjectUseCase.updateProject(new UpdateProjectUseCase.UpdateProjectCommand(project.getId(), project.getName(), project.getDateRange()));
                success(getString("project.saved"));
            }
        };
        form.add(new RequiredTextField<>("projectTitle", LambdaModel.of(form.getModel(), WebProject::getName, WebProject::setName)));
        form.add(new TextField<>("projectStart", LambdaModel.of(form.getModel(), WebProjectWithDate::getDateRange, WebProjectWithDate::setDateRange), DateRange.class)
                .add(new DateRangePickerBehavior(new HashMap<>())));
        return form;
    }

    private ListView<WebUser> createUserList(IModel<List<WebUser>> model) {
        var thisUser = BudgeteerSession.get().getLoggedInUser();
        return new ListView<>("userList", model) {
            @Override
            protected void populateItem(final ListItem<WebUser> item) {
                item.add(new Label("username", item.getModel().map(WebUser::getName)));
                var deleteButton = new Link<Void>("deleteButton") {
                    @Override
                    public void onClick() {
                        setResponsePage(new DeleteDialog() {
                            @Override
                            protected void onYes() {
                                removeUserFromProjectUseCase.removeUserFromProject(item.getModelObject().getId(), BudgeteerSession.get().getProjectId());
                                setResponsePage(ProjectAdministrationPage.class, getPageParameters());
                            }

                            @Override
                            protected void onNo() {
                                setResponsePage(ProjectAdministrationPage.class, getPageParameters());
                            }

                            @Override
                            protected String confirmationText() {
                                return ProjectAdministrationPage.this.getString("delete.person.confirmation");
                            }
                        });
                    }
                };
                // a user may not delete herself/himself
                if (item.getModelObject().equals(thisUser))
                    deleteButton.setVisible(false);
                item.add(deleteButton);
            }
        };
    }

    private Form<WebUser> createAddUserForm() {
        var form = new Form<WebUser>("addUserForm", Model.of()) {
            @Override
            protected void onSubmit() {
                if (getModel().isPresent().getObject()) {
                    addUserToProjectUseCase.addUserToProject(getModelObject().getId(), BudgeteerSession.get().getProjectId());
                }
            }
        };

        var userChoice = new DropDownChoice<>("userChoice", form.getModel(),
                () -> webUserMapper.toWebUser(getUsersNotInProjectUseCase.getUsersNotInProject(BudgeteerSession.get().getProjectId())),
                new UserChoiceRenderer());
        userChoice.setRequired(true);
        form.add(userChoice);
        return form;
    }

    private Link<Void> createDeleteProjectButton() {
        return new Link<>("deleteProjectButton") {
            @Override
            public void onClick() {
                setResponsePage(new DeleteDialog() {
                    @Override
                    protected void onYes() {
                        deleteProjectUseCase.deleteProject(BudgeteerSession.get().getProjectId());
                        BudgeteerSession.get().setProjectSelected(false);

                        setResponsePage(new SelectProjectPage(LoginPage.class, new PageParameters()));
                    }

                    @Override
                    protected void onNo() {
                        setResponsePage(ProjectAdministrationPage.class, getPageParameters());
                    }

                    @Override
                    protected String confirmationText() {
                        return ProjectAdministrationPage.this.getString("delete.project.confirmation");
                    }
                });
            }
        };
    }

    @Override
    protected BreadcrumbsModel getBreadcrumbsModel() {
        return new BreadcrumbsModel(DashboardPage.class, ProjectAdministrationPage.class);
    }

}
