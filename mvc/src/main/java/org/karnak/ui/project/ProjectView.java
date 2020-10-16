package org.karnak.ui.project;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.karnak.data.gateway.Project;
import org.karnak.ui.MainLayout;
import org.karnak.ui.data.ProjectDataProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

@Route(value = "project", layout = MainLayout.class)
@PageTitle("KARNAK - Project")
public class ProjectView extends VerticalLayout {
    public static final String VIEW_NAME = "Project";

    private ProjectDataProvider projectDataProvider;
    private NewProjectForm newProjectForm;
    private GridProject gridProject;
    private Binder<Project> newResearchBinder;

    public ProjectView() {
        newProjectForm = new NewProjectForm();
        projectDataProvider = new ProjectDataProvider();
        gridProject = new GridProject(projectDataProvider);
        newResearchBinder = newProjectForm.getBinder();
        add(newProjectForm, gridProject);
        setEventButtonAdd();
    }

    private void setEventButtonAdd() {
        newProjectForm.getButtonAdd().addClickListener(event -> {
            gridProject.clear();
            Project newProject = new Project();
            if (newResearchBinder.writeBeanIfValid(newProject)) {
                projectDataProvider.save(newProject);
                newProjectForm.clear();
            }
        });
    }

    @Autowired
    private void addEventManager(ApplicationEventPublisher publisher) {
        projectDataProvider.setApplicationEventPublisher(publisher);
    }
}
