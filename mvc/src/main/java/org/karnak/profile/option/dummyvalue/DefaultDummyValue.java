package org.karnak.profile.option.dummyvalue;

import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.VR;
import org.dcm4che6.util.UIDUtils;

public class DefaultDummyValue {
    public DefaultDummyValue() {
    }

    public String execute(VR vr, DicomObject dcm, int tag, String patientID) {
        String stringValue = dcm.getString(tag).orElse(null);
        if (stringValue != null) {
            String dummyValue = switch (vr) {
                case AE, CS, LO, LT, PN, SH, ST, UN, UT, UC, UR -> unknownValue();
                case DS, FL, FD, IS, SL, SS, UL, US -> zeroValue();
                case AS -> AS();
                case DA -> DA();
                case DT -> DT();
                case TM -> TM();
                case UI -> UI();
                default -> notImplemented();
            };
            return dummyValue;
        }
        return null;
    }

    private String notImplemented() {
        return null;
    }

    private String unknownValue() {
        return "UNKNOWN";
    }

    private String zeroValue() {
        return "0";
    }

    private String AS() {
        return "045Y";
    }

    private String TM() {
        return "111111";
    }

    private String DA() {
        return "19991111";
    }

    private String DT() {
        return "19991111111111";
    }

    private String UI() {
        return UIDUtils.randomUID();
    }
}
