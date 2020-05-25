package org.karnak.profileschain.profiles;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.VR;
import org.karnak.profileschain.action.Action;

public class UpdateUIDsProfile extends AbstractProfileItem {

    public UpdateUIDsProfile(String name, String codeName) {
        super(name, codeName);
    }

    @Override
    public Action getAction(DicomElement dcmElem) {
        if (dcmElem.vr() == VR.UI) {
            return Action.UID;
        }
        return null;
    }
}
