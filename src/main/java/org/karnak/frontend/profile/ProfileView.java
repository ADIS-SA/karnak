/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.frontend.profile;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.function.Predicate;
import org.karnak.backend.data.entity.ProfileEntity;
import org.karnak.backend.model.profilebody.ProfilePipeBody;
import org.karnak.backend.service.profilepipe.ProfilePipeService;
import org.karnak.frontend.MainLayout;
import org.karnak.frontend.profile.component.editprofile.ProfileComponent;
import org.karnak.frontend.profile.component.editprofile.ProfileElementMainView;
import org.karnak.frontend.profile.component.errorprofile.ProfileError;
import org.karnak.frontend.profile.component.errorprofile.ProfileErrorView;
import org.karnak.frontend.profile.component.ProfileGrid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;

@Route(value = "profile", layout = MainLayout.class)
@PageTitle("KARNAK - Profiles")
@Secured({"ADMIN"})
@SuppressWarnings("serial")
public class ProfileView extends HorizontalLayout implements HasUrlParameter<String> {

  public static final String VIEW_NAME = "Profiles";

  private static final Logger LOGGER = LoggerFactory.getLogger(ProfileView.class);

  private final ProfileLogic profileLogic;

  private final ProfileComponent profileComponent;
  private final ProfileElementMainView profileElementMainView;
  private final ProfileGrid profileGrid;
  private final ProfileErrorView profileErrorView;
  private final transient ProfilePipeService profilePipeService;
  private VerticalLayout barAndGridLayout;
  private HorizontalLayout profileHorizontalLayout;
  private Upload uploadProfile;
  private MemoryBuffer memoryBuffer;

  @Autowired
  public ProfileView(final ProfileLogic profileLogic, final ProfilePipeService profilePipeService) {
    this.profileLogic = profileLogic;
    this.profileLogic.setProfileView(this);
    this.profilePipeService = profilePipeService;

    profileGrid = new ProfileGrid();
    profileComponent = new ProfileComponent(profileLogic);
    profileElementMainView = new ProfileElementMainView();
    profileErrorView = new ProfileErrorView();
    profileHorizontalLayout = new HorizontalLayout(profileComponent, profileElementMainView);

    initComponents();
    buildLayout();

    addEventUploadProfile();
    addEventGridSelection();

    add(barAndGridLayout, profileHorizontalLayout);
  }

  @Override
  public void setParameter(BeforeEvent beforeEvent, @OptionalParameter String parameter) {
    if (parameter != null) {
      final Long idProfilePipe = profileLogic.enter(parameter);
      ProfileEntity currentProfileEntity = null;
      if (idProfilePipe != null) {
        currentProfileEntity = profileLogic.retrieveProfile(idProfilePipe);
      }
      profileGrid.selectRow(currentProfileEntity);
      profileComponent.setProfile(currentProfileEntity);
      profileElementMainView.setProfile(currentProfileEntity);
      remove(profileErrorView);
      add(profileHorizontalLayout);
    }
  }

  private void buildLayout() {
    setSizeFull();
    profileComponent.setWidth("45%");
    profileElementMainView.setWidth("55%");
    profileErrorView.setWidth("75%");
    profileHorizontalLayout.setWidth("75%");
    profileHorizontalLayout.getStyle().set("overflow-y", "auto");

    barAndGridLayout = new VerticalLayout();
    barAndGridLayout.add(uploadProfile);
    barAndGridLayout.add(profileGrid);
    barAndGridLayout.setFlexGrow(0, uploadProfile);
    barAndGridLayout.setFlexGrow(1, profileGrid);
    barAndGridLayout.setWidth("25%");
  }

  private void initComponents() {
    initUploadProfile();
    profileGrid.setItems(profileLogic);
  }

  private void initUploadProfile() {
    memoryBuffer = new MemoryBuffer();
    // https://github.com/vaadin/vaadin-upload-flow/blob/6fa9cc429e1d0894704fb962e0df375a9d0439c8/vaadin-upload-flow-integration-tests/src/main/java/com/vaadin/flow/component/upload/tests/it/UploadView.java#L122
    uploadProfile = new Upload(memoryBuffer);
    uploadProfile.setDropLabel(new Span("Drag and drop your profile here"));
  }

  private void addEventUploadProfile() {
    uploadProfile.addSucceededListener(e -> setProfileComponent(memoryBuffer.getInputStream()));
  }

  private void addEventGridSelection() {
    profileGrid.asSingleSelect().addValueChangeListener(event -> navigateProfile(event.getValue()));
  }

  /**
   * Navigation to the profile in parameter
   *
   * @param profileEntity Profile to navigate to
   */
  public void navigateProfile(ProfileEntity profileEntity) {
    if (profileEntity == null) {
      UI.getCurrent().navigate(ProfileView.class, "");
    } else {
      String profileID = String.valueOf(profileEntity.getId());
      UI.getCurrent().navigate(ProfileView.class, profileID);
    }
  }

  private void setProfileComponent(InputStream stream) {
    try {
      ProfilePipeBody profilePipe = readProfileYaml(stream);
      ArrayList<ProfileError> profileErrors = profilePipeService.validateProfile(profilePipe);
      Predicate<ProfileError> errorPredicate = profileError -> profileError.getError() != null;
      if (profileErrors.stream().noneMatch(errorPredicate)) {
        final ProfileEntity newProfileEntity =
            profilePipeService.saveProfilePipe(profilePipe, false);
        profileErrorView.removeAll();
        profileGrid.selectRow(newProfileEntity);
        profileComponent.setProfile(newProfileEntity);
        profileElementMainView.setProfile(newProfileEntity);
      } else {
        profileGrid.deselectAll();
        profileErrorView.setView(profileErrors);
        remove(profileHorizontalLayout);
        add(profileErrorView);
      }
    } catch (YAMLException e) {
      LOGGER.error("Unable to read uploaded YAML", e);
      profileErrorView.setView(
          "Unable to read uploaded YAML file.\n"
              + "Please make sure it is a YAML file and respects the YAML structure.");
    }
  }

  private ProfilePipeBody readProfileYaml(InputStream stream) {
    final Yaml yaml = new Yaml(new Constructor(ProfilePipeBody.class));
    return yaml.load(stream);
  }

  public ProfileErrorView getProfileErrorView() {
    return profileErrorView;
  }

  public HorizontalLayout getProfileHorizontalLayout() {
    return profileHorizontalLayout;
  }
}
