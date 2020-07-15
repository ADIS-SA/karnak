package org.karnak.ui.profile;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.selection.SingleSelect;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.karnak.data.profile.Profile;
import org.karnak.data.profile.ProfilePipe;
import org.karnak.profilepipe.profilebody.ProfilePipeBody;
import org.karnak.ui.MainLayout;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;

@Route(value = "profile", layout = MainLayout.class)
@PageTitle("Profile configuration")
public class ProfileView extends HorizontalLayout {
    public static final String VIEW_NAME = "Profile";

    private VerticalLayout profileOutput = new VerticalLayout();
    private ProfileComponent profileComponent = new ProfileComponent();
    private Upload uploadProfile;
    private ProfileNameGrid profileNameGrid;
    private final ProfilePipeService profilePipeService;

    public ProfileView() {
        profilePipeService = new ProfilePipeServiceImpl();
        setSizeFull();
        VerticalLayout barAndGridLayout = createTopLayoutGrid();
        add(barAndGridLayout);
        add(profileOutput);
    }

    private VerticalLayout createTopLayoutGrid() {
        HorizontalLayout topLayout = createTopBar();
        profileNameGrid = new ProfileNameGrid();
        SingleSelect<Grid<ProfilePipe>, ProfilePipe> profilePipeSingleSelect =
                profileNameGrid.asSingleSelect();

        profilePipeSingleSelect.addValueChangeListener(e -> {
            ProfilePipe profileSelected = e.getValue();
            profileComponent.setProfilePipe(profileSelected);
        });

        VerticalLayout barAndGridLayout = new VerticalLayout();
        barAndGridLayout.add(topLayout);
        barAndGridLayout.add(profileNameGrid);
        barAndGridLayout.setFlexGrow(0, topLayout);
        barAndGridLayout.setFlexGrow(1, profileNameGrid);
        barAndGridLayout.setSizeFull();
        return barAndGridLayout;
    }

    private HorizontalLayout createTopBar() {
        MemoryBuffer memoryBuffer = new MemoryBuffer();
        // https://github.com/vaadin/vaadin-upload-flow/blob/6fa9cc429e1d0894704fb962e0df375a9d0439c8/vaadin-upload-flow-integration-tests/src/main/java/com/vaadin/flow/component/upload/tests/it/UploadView.java#L122
        uploadProfile = new Upload(memoryBuffer);
        uploadProfile.addSucceededListener(e -> {
            setProfileComponent(e.getMIMEType(), memoryBuffer.getInputStream());
        });

        HorizontalLayout layout = new HorizontalLayout();
        layout.add(uploadProfile);
        return layout;
    }

    private void setProfileComponent(String mimeType,
                                      InputStream stream) {
        if (mimeType.equals("application/x-yaml")) {
            ProfilePipeBody profilePipe = readProfileYaml(stream);
            profilePipeService.updateProfilePipe(profilePipe);
            profileNameGrid.updatedProfilePipesView();
        } else {
            profileComponent.setError();
        }

        profileOutput.removeAll();
        profileOutput.add(profileComponent);
    }

    private ProfilePipeBody readProfileYaml(InputStream stream) {
        final Yaml yaml = new Yaml(new Constructor(ProfilePipeBody.class));
        return yaml.load(stream);
    }
}
