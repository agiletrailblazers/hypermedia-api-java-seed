package com.atb.hypermedia.api.config.converters;

import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodFormatter;

import java.beans.PropertyEditorSupport;

public class JodaPeriodPropertyEditor extends PropertyEditorSupport {

    private PeriodFormatter formatter = ISOPeriodFormat.standard();

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        setValue(formatter.parsePeriod(text));
    }
}
