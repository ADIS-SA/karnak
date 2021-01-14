package org.karnak.frontend.kheops;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import java.util.ArrayList;
import java.util.List;
import org.karnak.backend.data.entity.KheopsAlbumsEntity;
import org.karnak.frontend.util.UIS;

public class SwitchingAlbumsView extends CustomField<List<KheopsAlbumsEntity>> {

  private final NewSwitchingAlbum newSwitchingAlbum;
  private final GridSwitchingAlbums gridSwitchingAlbums;
  private final Binder<KheopsAlbumsEntity> newSwitchingAlbumBinder;
  private final ListDataProvider<KheopsAlbumsEntity> dataProviderSwitchingAlbums;
  private final List<KheopsAlbumsEntity> kheopsAlbumsEntityList;
  private final Checkbox checkboxSwitchingAlbums;
  private final VerticalLayout layout;

  public SwitchingAlbumsView() {
    gridSwitchingAlbums = new GridSwitchingAlbums();
    dataProviderSwitchingAlbums =
        (ListDataProvider<KheopsAlbumsEntity>) gridSwitchingAlbums.getDataProvider();
    newSwitchingAlbum = new NewSwitchingAlbum();
    newSwitchingAlbumBinder = newSwitchingAlbum.getBinder();
    kheopsAlbumsEntityList = new ArrayList<>();
    layout = new VerticalLayout();
    checkboxSwitchingAlbums = new Checkbox("Switching in different KHEOPS albums");
    add(UIS.setWidthFull(checkboxSwitchingAlbums), layout);
    setEventCheckBox();
    setEventButtonAdd();
  }

  private void setEventCheckBox() {
    checkboxSwitchingAlbums.addValueChangeListener(
        event -> {
          if (event.getValue() != null) {
            addComponent(event.getValue());
          }
        });
  }

  private void setCheckboxSwitchingAlbumsValue() {
    checkboxSwitchingAlbums.setValue(!dataProviderSwitchingAlbums.getItems().isEmpty());
  }

  public void addComponent(Boolean value) {
    if (value == true) {
      layout.add(newSwitchingAlbum, gridSwitchingAlbums);
    } else {
      newSwitchingAlbum.clear();
      layout.removeAll();
    }
  }

  public VerticalLayout getComponent() {
    return layout;
  }

  private void setEventButtonAdd() {
    newSwitchingAlbum
        .getButtonAdd()
        .addClickListener(
            event -> {
              KheopsAlbumsEntity newKheopsAlbumsEntity = new KheopsAlbumsEntity();
              if (newSwitchingAlbumBinder.writeBeanIfValid(newKheopsAlbumsEntity)) {
                dataProviderSwitchingAlbums.getItems().add(newKheopsAlbumsEntity);
                dataProviderSwitchingAlbums.refreshAll();
                newSwitchingAlbum.clear();
              }
            });
  }

  @Override
  protected List<KheopsAlbumsEntity> generateModelValue() {
    return kheopsAlbumsEntityList;
  }

  @Override
  public List<KheopsAlbumsEntity> getValue() {
    return checkboxSwitchingAlbums.getValue()
        ? new ArrayList<>(dataProviderSwitchingAlbums.getItems())
        : null;
  }

  @Override
  public void setValue(List<KheopsAlbumsEntity> kheopsAlbumEntities) {
    dataProviderSwitchingAlbums.getItems().removeAll(dataProviderSwitchingAlbums.getItems());
    gridSwitchingAlbums.clearEditorEditButtons();
    dataProviderSwitchingAlbums
        .getItems()
        .addAll(kheopsAlbumEntities != null ? kheopsAlbumEntities : new ArrayList<>());
    dataProviderSwitchingAlbums.refreshAll();
    setCheckboxSwitchingAlbumsValue();
  }

  @Override
  protected void setPresentationValue(List<KheopsAlbumsEntity> kheopsAlbumEntities) {
  }
}
