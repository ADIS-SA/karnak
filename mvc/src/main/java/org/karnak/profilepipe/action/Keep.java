package org.karnak.profilepipe.action;

import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.util.TagUtils;
import org.karnak.profilepipe.utils.HMAC;
import org.slf4j.MDC;

import java.util.Iterator;

public class Keep extends AbstractAction {

    public Keep(String symbol) {
        super(symbol);
    }

    @Override
    public void execute(DicomObject dcm, int tag, Iterator<DicomElement> iterator, HMAC hmac) {
        final String tagValueIn = dcm.getString(tag).orElse(null);
        LOGGER.trace(CLINICAL_MARKER, PATTERN_WITH_INOUT, MDC.get("SOPInstanceUID"), TagUtils.toString(tag), symbol, tagValueIn, tagValueIn);
    }
}
