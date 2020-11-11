package org.karnak.ui.forwardnode;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.binder.Binder;
import org.karnak.data.gateway.Destination;
import org.karnak.data.gateway.IdTypes;
import org.karnak.data.gateway.Project;
import org.karnak.ui.data.ProjectDataProvider;
import org.karnak.ui.project.MainViewProjects;
import org.karnak.ui.util.UIS;

public class LayoutDesidentification extends Div {
    private Binder<Destination> destinationBinder;

    private Checkbox checkboxDesidentification;
    private Label labelDisclaimer;
    private Checkbox checkboxUseAsPatientName;
    private ProjectDropDown projectDropDown;
    private ExtidPresentInDicomTagView extidPresentInDicomTagView;
    private DesidentificationName desidentificationName;
    private Div div;
    private ProjectDataProvider projectDataProvider;
    private WarningNoProjectsDefined warningNoProjectsDefined;

    private final String LABEL_CHECKBOX_DESIDENTIFICATION = "Activate de-identification";
    private final String LABEL_DISCLAIMER_DEIDENTIFICATION = "In order to ensure complete de-identification, visual verification of metadata and images is necessary.";

    private Select<String> extidListBox;
    final String [] extidSentence = {"Pseudonym are generate automatically","Pseudonym is already store in KARNAK", "Pseudonym is in a DICOM tag"};

    public LayoutDesidentification(Binder<Destination> destinationBinder) {
        projectDataProvider = new ProjectDataProvider();
        this.destinationBinder = destinationBinder;
        projectDropDown = new ProjectDropDown();
        desidentificationName = new DesidentificationName();

        warningNoProjectsDefined = new WarningNoProjectsDefined();
        warningNoProjectsDefined.setTextBtnCancel("Continue");
        warningNoProjectsDefined.setTextBtnValidate("Create a project");

        setElements();
        setBinder();
        setEventCheckboxDesidentification();
        setEventExtidListBox();
        setEventWarningDICOM();

        add(UIS.setWidthFull(new HorizontalLayout(checkboxDesidentification, div)));

        if (checkboxDesidentification.getValue()) {
            div.add(labelDisclaimer,projectDropDown, desidentificationName, extidListBox);
        }

        projectDropDown.addValueChangeListener(event -> {
            setTextOnSelectionProject(event.getValue());
        });
    }

    private void setElements() {
        checkboxDesidentification = new Checkbox(LABEL_CHECKBOX_DESIDENTIFICATION);
        checkboxDesidentification.setValue(true);
        checkboxDesidentification.setMinWidth("25%");

        labelDisclaimer = new Label(LABEL_DISCLAIMER_DEIDENTIFICATION);
        labelDisclaimer.getStyle().set("color", "red");
        labelDisclaimer.setMinWidth("75%");
        labelDisclaimer.getStyle().set("right", "0px");

        projectDropDown.setLabel("Choose a project");
        projectDropDown.setWidth("100%");

        extidListBox = new Select<>();
        extidListBox.setLabel("Pseudonym type");
        extidListBox.setWidth("100%");
        extidListBox.getStyle().set("right", "0px");
        extidListBox.setItems(extidSentence);

        checkboxUseAsPatientName = new Checkbox("Use as Patient Name");

        extidPresentInDicomTagView = new ExtidPresentInDicomTagView(destinationBinder);
        div = new Div();
        div.setWidth("100%");

    }

    private void setEventWarningDICOM() {
        warningNoProjectsDefined.getBtnCancel().addClickListener(btnEvent -> {
            checkboxDesidentification.setValue(false);
            warningNoProjectsDefined.close();
        });
        warningNoProjectsDefined.getBtnValidate().addClickListener(btnEvent -> {
            warningNoProjectsDefined.close();
            navigateToProject();
        });
    }

    private void navigateToProject() {
        getUI().ifPresent(nav -> {
            nav.navigate(MainViewProjects.VIEW_NAME.toLowerCase());
        });
    }

    private void setEventCheckboxDesidentification(){
        checkboxDesidentification.addValueChangeListener(event -> {
            if (event.getValue() != null) {
                if (event.getValue()) {
                    if (projectDataProvider.getAllProjects().size() > 0) {
                        div.add(labelDisclaimer, projectDropDown, desidentificationName, extidListBox);
                        setTextOnSelectionProject(projectDropDown.getValue());
                    } else {
                        warningNoProjectsDefined.open();
                    }
                } else {
                    div.remove(labelDisclaimer, projectDropDown, desidentificationName);
                    extidListBox.setValue(extidSentence[0]);
                    checkboxUseAsPatientName.clear();
                    extidPresentInDicomTagView.clear();
                    div.remove(extidListBox);
                    remove(checkboxUseAsPatientName);
                    div.remove(extidPresentInDicomTagView);
                }
            }
        });
    }

    private void setTextOnSelectionProject(Project project) {
        if (project != null && project.getProfile() != null) {
            desidentificationName.setShowValue(String.format("The profile %s will be used", project.getProfile().getName()));
        } else if (project != null && project.getProfile() == null) {
            desidentificationName.setShowValue("No profiles defined in the project");
        } else {
            desidentificationName.removeAll();
        }
    }

    private void setEventExtidListBox() {
        extidListBox.addValueChangeListener(event -> {
            if (event.getValue() != null) {
                if (event.getValue().equals(extidSentence[0])) {
                    checkboxUseAsPatientName.clear();
                    extidPresentInDicomTagView.clear();
                    div.remove(checkboxUseAsPatientName);
                    div.remove(extidPresentInDicomTagView);
                } else {
                    div.add(UIS.setWidthFull(checkboxUseAsPatientName));
                    if (event.getValue().equals(extidSentence[1])) {
                        extidPresentInDicomTagView.clear();
                        div.remove(extidPresentInDicomTagView);
                    } else {
                        extidPresentInDicomTagView.enableComponent();
                        div.add(extidPresentInDicomTagView);
                    }
                }
            }
        });
    }


    private void setBinder() {
        destinationBinder.forField(checkboxDesidentification)
                .bind(Destination::getDesidentification, Destination::setDesidentification);
        destinationBinder.forField(projectDropDown)
                .withValidator(project ->
                        project != null || (project == null && checkboxDesidentification.getValue() == false),
                        "Choose a project")
                .bind(Destination::getProject, Destination::setProject);

        destinationBinder.forField(extidListBox)
                .withValidator(type -> type != null,"Choose pseudonym type\n")
                .bind(destination -> {
                    if (destination.getIdTypes().equals(IdTypes.PID)){
                        return extidSentence[0];
                    } else if(destination.getIdTypes().equals(IdTypes.EXTID)) {
                        return extidSentence[1];
                    } else {
                        return extidSentence[2];
                    }
                }, (destination, s) -> {
                    if (s.equals(extidSentence[0])) {
                        destination.setIdTypes(IdTypes.PID);
                    } else if (s.equals(extidSentence[1])){
                        destination.setIdTypes(IdTypes.EXTID);
                    } else {
                        destination.setIdTypes(IdTypes.ADD_EXTID);
                    }
                });

        destinationBinder.forField(checkboxUseAsPatientName)
                .bind(Destination::getPseudonymAsPatientName, Destination::setPseudonymAsPatientName);
    }
}
