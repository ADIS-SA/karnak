package org.karnak.ui.extid;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.validator.StringLengthValidator;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class ExternalIDGrid extends Grid<Patient> {
    private Binder<Patient> binder;
    private List<Patient> patientList;
    private Button addNewPatientButton;
    private ListDataProvider<Patient> dataProvider;
    private Button deletePatientButton;
    private Button saveEditPatientButton;
    private Button cancelEditPatientButton;

    private Grid.Column<Patient> extidColumn;
    private Grid.Column<Patient> patientIdColumn;
    private Grid.Column<Patient> patientNameColumn;
    private Grid.Column<Patient> issuerOfPatientIDColumn;
    private Grid.Column<Patient> patientBirthDateColumn;
    private Grid.Column<Patient> patientSexColumn;
    private Grid.Column<Patient> editorColumn;

    private Editor<Patient> editor;
    private Collection<Button> editButtons;

    private TextField externalIdField;
    private TextField patientIdField;
    private TextField patientNameField;
    private TextField issuerOfPatientIdField;
    private DatePicker patientBirthDateField;
    private Select<String> patientSexField;

    private Grid.Column<Patient> deleteColumn;

    private String LABEL_SAVE = "Save";
    private String LABEL_CANCEL = "Cancel";
    private String LABEL_DELETE = "Delete";

    public ExternalIDGrid(){
        binder = new Binder<>(Patient.class);
        patientList = new ArrayList<>();
        dataProvider = (ListDataProvider<Patient>) getDataProvider();

        setSizeFull();
        getElement().addEventListener("keyup", event -> editor.cancel())
                .setFilter("event.key === 'Escape' || event.key === 'Esc'");
        setHeightByRows(true);
        setItems(patientList);
        setElements();
        setBinder();

        editor.addOpenListener(e -> {
            editButtons.stream()
                    .forEach(button -> button.setEnabled(!editor.isOpen()));
            deleteColumn.setVisible(false);
            addNewPatientButton.setVisible(false);
        });

        editor.addCloseListener(e -> {
            editButtons.stream()
                    .forEach(button -> button.setEnabled(!editor.isOpen()));
            deleteColumn.setVisible(true);
            addNewPatientButton.setVisible(true);
        });

        saveEditPatientButton.addClickListener(e -> {
            editor.save();
        });
        saveEditPatientButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        cancelEditPatientButton.addClickListener(e -> editor.cancel());
    }

    private void setElements() {
        extidColumn = addColumn(Patient::getExtid).setHeader("External Pseudonym");
        patientIdColumn = addColumn(Patient::getPatientId).setHeader("Patient ID");
        patientNameColumn = addColumn(Patient::getPatientName).setHeader("Patient Name");
        issuerOfPatientIDColumn = addColumn(Patient::getIssuerOfPatientId).setHeader("Issuer of patient ID");
        patientBirthDateColumn = addColumn(Patient::getPatientBirthDate).setHeader("Patient Birth Date");
        patientSexColumn = addColumn(Patient::getPatientSex).setHeader("Patient Sex");

        editButtons = Collections.newSetFromMap(new WeakHashMap<>());
        editor = getEditor();
        editor.setBinder(binder);
        editor.setBuffered(true);

        externalIdField = new TextField();
        patientIdField = new TextField();
        patientNameField = new TextField();
        issuerOfPatientIdField = new TextField();
        patientBirthDateField = new DatePicker();
        patientSexField = new Select<>();
        patientSexField.setItems("M", "F", "O");

        extidColumn.setEditorComponent(externalIdField);
        patientIdColumn.setEditorComponent(patientIdField);
        patientNameColumn.setEditorComponent(patientNameField);
        issuerOfPatientIDColumn.setEditorComponent(issuerOfPatientIdField);
        patientBirthDateColumn.setEditorComponent(patientBirthDateField);
        patientSexColumn.setEditorComponent(patientSexField);

        editorColumn = addComponentColumn(patient -> {
            Button edit = new Button("Edit");
            edit.addClassName("edit");
            edit.addClickListener(e -> {
                editor.editItem(patient);
                externalIdField.focus();
            });
            edit.setEnabled(!editor.isOpen());
            editButtons.add(edit);
            return edit;
        });

        deleteColumn = addComponentColumn(patient -> {
            deletePatientButton = new Button("Delete");
            deletePatientButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
            deletePatientButton.addClickListener( e -> {
                patientList.remove(patient);
                getDataProvider().refreshAll();
            });
            return deletePatientButton;
        });

        saveEditPatientButton = new Button(LABEL_SAVE);
        cancelEditPatientButton = new Button(LABEL_CANCEL);

        Div buttons = new Div(saveEditPatientButton, cancelEditPatientButton);
        editorColumn.setEditorComponent(buttons);
    }

    public Div setBinder(){
        Div validationStatus = new Div();
        validationStatus.setId("validation");
        validationStatus.getStyle().set("color", "var(--theme-color, red)");
        binder.forField(externalIdField)
                .withValidator(StringUtils::isNotBlank, "External Pseudonym is empty")
                .withValidator(new StringLengthValidator("Length must be between 1 and 50.", 1, 50))
                .withStatusLabel(validationStatus).bind("extid");

        binder.forField(patientIdField)
                .withValidator(StringUtils::isNotBlank, "Patient ID is empty")
                .withValidator(new StringLengthValidator("Length must be between 1 and 50.", 1, 50))
                .withStatusLabel(validationStatus).bind("patientId");

        binder.forField(patientNameField)
                .withValidator(StringUtils::isNotBlank, "Patient Name is empty")
                .withValidator(new StringLengthValidator("Length must be between 1 and 50.", 1, 50))
                .withStatusLabel(validationStatus).bind("patientName");

        binder.forField(issuerOfPatientIdField)
                .withValidator(new StringLengthValidator("Length must be between 0 and 50.", 0, 50))
                .withStatusLabel(validationStatus).bind("issuerOfPatientId");

        binder.forField(patientBirthDateField)
                .asRequired("Please choose a date")
                .withStatusLabel(validationStatus).bind("patientBirthDate");

        binder.forField(patientSexField)
                .withValidator(StringUtils::isNotBlank, "Patient Sex is empty")
                .withValidator(new StringLengthValidator("Length must be 1.", 1, 1))
                .withStatusLabel(validationStatus).bind("patientSex");
        return validationStatus;
    }

    public void setAddNewPatientButton(Button addNewPatientButton) {
        this.addNewPatientButton = addNewPatientButton;
    }
}