package org.karnak.ui.profile;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.karnak.profilepipe.profilebody.ProfileBody;
import org.karnak.profilepipe.profilebody.ProfilePipeBody;

public class ProfileComponent extends VerticalLayout {
    private ProfilePipeBody profilePipe;

    ProfileComponent(ProfilePipeBody profilePipe) {
        setSizeFull();
        this.profilePipe = profilePipe;
        createComponent();
    }

    public void createComponent() {
        TitleValue name = new TitleValue("Name", profilePipe.getName());
        TitleValue version = new TitleValue("Profile version", profilePipe.getVersion());
        TitleValue minVersion = new TitleValue("Min. version KARNAK required", profilePipe.getMinimumkarnakversion());
        TitleValue defaultIssuerOfPatientID = new TitleValue("Default issuer of PatientID", profilePipe.getDefaultIssuerOfPatientID());
        add(name, version, minVersion, defaultIssuerOfPatientID);
        for (ProfileBody profile: profilePipe.getProfiles()) {
            add(new TitleValue("Profile name", profile.getName()));
        }
    }

    public ProfilePipeBody getProfilePipe() {
        return profilePipe;
    }

    public void setProfilePipe(ProfilePipeBody profilePipe) {
        this.profilePipe = profilePipe;
    }
}
