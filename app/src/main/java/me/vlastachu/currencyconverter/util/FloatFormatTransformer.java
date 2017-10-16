package me.vlastachu.currencyconverter.util;

import org.simpleframework.xml.transform.Transform;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;


/**
 * Created by vlastachu on 09/10/2017.
 */

public class FloatFormatTransformer implements Transform<Float> {
    private final DecimalFormat format;

    public FloatFormatTransformer() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator(',');
        format = new DecimalFormat("0.####");
        format.setDecimalFormatSymbols(symbols);
    }

    @Override
    public Float read(String value) throws Exception {
        return format.parse(value).floatValue();
    }

    @Override
    public String write(Float value) throws Exception {
        return format.format(value);
    }
}
