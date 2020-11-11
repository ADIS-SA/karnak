package org.karnak.profilepipe.action;

import org.dcm4che6.data.VR;
import org.karnak.profilepipe.profiles.CleanPixelData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public abstract class AbstractAction implements ActionItem {
    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractAction.class);
    protected static final Marker CLINICAL_MARKER = MarkerFactory.getMarker("CLINICAL");
    protected static final String PATTERN_WITH_INOUT = "TAG={} ACTION={} OLD={} NEW={}";
    protected static final String PATTERN_WITH_IN = "TAG={} ACTION={} OLD={}";
    protected static final String ADD_METHOD = "a";

    protected final String symbol;
    protected String dummyValue;
    protected int newTag;
    protected VR vr;

    public AbstractAction(String symbol) {
        this.symbol = symbol;
        this.dummyValue = null;
        this.vr = null;
    }

    public AbstractAction(String symbol, String dummyValue) {
        this.symbol = symbol;
        this.dummyValue = dummyValue;
        this.vr = null;
    }

    public AbstractAction(String symbol, VR vr, String dummyValue){
        this.symbol = symbol;
        this.vr = vr;
        this.dummyValue = dummyValue;
    }

    public AbstractAction(String symbol, int newTag, VR vr, String dummyValue){
        this.symbol = symbol;
        this.newTag = newTag;
        this.vr = vr;
        this.dummyValue = dummyValue;
    }

    public static AbstractAction convertAction(String action) {
        if (action == null) {
            return null;
        }
        return switch (action) {
            case "Z" -> new ReplaceNull("Z");
            case "X" -> new Remove("X");
            case "K" -> new Keep("K");
            case "U" -> new UID("U");
            case "DDum" -> new DefaultDummy("DDum");
            case "D" -> new Replace("D");
            default -> null;
        };
    }

    public String getSymbol() {
        return symbol;
    }

    public String getDummyValue() {
        return dummyValue;
    }

    public void setDummyValue(String dummyValue) {
        this.dummyValue = dummyValue;
    }

    public VR getVr() { return vr; }

    public void setVr(VR vr) { this.vr = vr; }
}
