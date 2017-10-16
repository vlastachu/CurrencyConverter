package me.vlastachu.currencyconverter.util;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.transform.RegistryMatcher;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import me.vlastachu.currencyconverter.model.ValCurs;

public class XmlParser {
    private static DateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.US);
    private static RegistryMatcher m = new RegistryMatcher();
    static {
        m.bind(Date.class, new DateFormatTransformer(format));
        m.bind(Float.class, new FloatFormatTransformer());
    }

    public static ValCurs read(InputStream stream) throws Exception {
        Serializer serializer = new Persister(m);
        return serializer.read(ValCurs.class, stream);
    }

    public static void write(ValCurs valCurs, OutputStream os) throws Exception {
        Serializer serializer = new Persister(m);
        serializer.write(valCurs, os);
    }
}
