package org.karnak.ui.gateway;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import org.dcm4che6.util.TagUtils;
import org.karnak.data.gateway.Destination;
import org.karnak.data.gateway.IdTypes;

public class ExternalPseudonymView extends HorizontalLayout {

    private Binder<Destination> binder;
    private Checkbox pseudonymAsPatientName;
    private Checkbox externalPseudonymCheckbox;
    private TextField delimiter;
    private TextField tag;
    private TextField position;
    private HorizontalLayout horizontalLayoutPseudonymInDicom;
    private Div verticalLayoutExeternalPseudonym;
    private Select<String> extidListBox;
    private boolean unBindAllFields;


    final String [] extidSentence = {"Pseudonym is already store in KARNAK", "Pseudonym is in a DICOM tag"};
    private IdTypes idTypes;

    public ExternalPseudonymView(Binder<Destination> binder) {
        this.binder = binder;
        idTypes = IdTypes.EXTID;

        externalPseudonymCheckbox = new Checkbox();
        externalPseudonymCheckbox.setLabel("Use an external pseudonym");
        externalPseudonymCheckbox.setMinWidth("25%");
        externalPseudonymCheckbox.addValueChangeListener(event -> {
            if (event != null) {
                if(event.getValue()) {
                    add(verticalLayoutExeternalPseudonym);
                } else {
                    remove(verticalLayoutExeternalPseudonym);
                    unBindAll(true);
                }
            }
        });

        verticalLayoutExeternalPseudonym = new Div();
        pseudonymAsPatientName = new Checkbox("Use external pseudonym as Patient Name");
        delimiter = new TextField("Delimiter");
        tag = new TextField("Tag");
        position = new TextField("Position");
        horizontalLayoutPseudonymInDicom = new HorizontalLayout(tag, delimiter, position);

        extidListBox = new Select<>();
        extidListBox.setWidthFull();
        extidListBox.setItems(extidSentence);
        extidListBox.setValue(extidSentence[0]);

        verticalLayoutExeternalPseudonym.add(pseudonymAsPatientName);
        verticalLayoutExeternalPseudonym.add(extidListBox);

        extidListBox.addValueChangeListener(valueChangeEvent -> {
            if(valueChangeEvent.getValue().equals(extidSentence[0])){
                idTypes = IdTypes.EXTID;
                verticalLayoutExeternalPseudonym.remove(horizontalLayoutPseudonymInDicom);
                unBindAll(true);
            }else{
                idTypes = IdTypes.ADD_EXTID;
                verticalLayoutExeternalPseudonym.add(horizontalLayoutPseudonymInDicom);
                unBindAll(false);
            }
        });


        setBinder();

        add(externalPseudonymCheckbox);
    }

    public void setBinder() {

        binder.forField(pseudonymAsPatientName)
                .bind(destination -> destination.getExternalPseudonym().getPseudonymAsPatientName(),
                        (destination, value) -> destination.getExternalPseudonym().setPseudonymAsPatientName(value));


        binder.forField(tag)
                .withValidator(tag -> {
                            final String cleanTag = tag.replaceAll("[(),]", "").toUpperCase();
                            try {
                                TagUtils.intFromHexString(cleanTag);
                            } catch (Exception e) {
                                return false;
                            }
                            return unBindAllFields || unBindSoreInDicomFields() || (tag != null && !tag.equals("") && cleanTag.length() == 8);
                        },
                        "Choose a valid tag\n")
                .bind(destination -> {
                    if(destination.getExternalPseudonym() != null) {
                        return destination.getExternalPseudonym().getTag();
                    } else {
                        return null;
                    }
                }, (destination, value) -> {
                    destination.getExternalPseudonym().setTag(value);
                });

        binder.forField(delimiter)
                .withValidator(delimiter -> unBindAllFields || unBindSoreInDicomFields() || !(delimiter.equals("") && !position.getValue().equals("")),
                        "Choose a delimiter when a position is defined\n")
                .bind(destination -> {
                    if(destination.getExternalPseudonym() != null) {
                        return destination.getExternalPseudonym().getDelimiter();
                    } else {
                        return "";
                    }
                }, (destination, value) -> {
                    destination.getExternalPseudonym().setDelimiter(value);
                });

        binder.forField(position)
                .withConverter(new StringToIntegerConverter("Must be a numeric value"))
                .withValidator(position -> unBindAllFields || unBindSoreInDicomFields() || !(position == null && !delimiter.equals("")),
                        "Choose a position when a delimiter is defined\n")
                .bind(destination -> {
                    if(destination.getExternalPseudonym() != null) {
                        return destination.getExternalPseudonym().getPosition();
                    } else {
                        return 0;
                    }
                }, (destination, value) -> {
                    destination.getExternalPseudonym().setPosition(value);
                });

        binder.forField(extidListBox)
                .bind(destination -> {
                    if(destination.getExternalPseudonym() != null) {
                        if(destination.getIdTypes().equals(IdTypes.ADD_EXTID)){
                            return extidSentence[1];
                        } else {
                            return extidSentence[0];
                        }
                    } else {
                        return "";
                    }
                }, (destination, value) -> {
                    if(value.equals(extidSentence[1])){
                        destination.setIdTypes(IdTypes.ADD_EXTID);
                    } else {
                        destination.setIdTypes(IdTypes.EXTID);
                    }
                });

        binder.forField(externalPseudonymCheckbox)
                .bind(destination -> {
                    if (destination.getIdTypes().equals(IdTypes.PID)) {
                        unBindAll(true);
                        return false;
                    } else {
                        add(verticalLayoutExeternalPseudonym);
                        unBindAll(false);
                        return true;
                    }
                }, (destination, value) -> {
                    if (value) {
                        destination.setIdTypes(idTypes);
                    } else {
                        destination.setIdTypes(IdTypes.PID);
                    }
                });
    }

    public boolean unBindSoreInDicomFields(){
        return (extidListBox.getValue().equals(extidSentence[0]));
    }

    public void unBindAll(boolean value){
        unBindAllFields = value;
    }

    public IdTypes getIdTypes() {
        return idTypes;
    }
}
