package me.vlastachu.currencyconverter.util;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import me.vlastachu.currencyconverter.model.ValCurs;
import me.vlastachu.currencyconverter.model.Valute;

import static org.junit.Assert.*;

public class XmlParserTest {
    private final String xml = "\n" +
            "<ValCurs Date=\"07.10.2017\" name=\"Foreign Currency Market\">\n" +
            "<Valute ID=\"R01010\">\n" +
            "<NumCode>036</NumCode>\n" +
            "<CharCode>AUD</CharCode>\n" +
            "<Nominal>1</Nominal>\n" +
            "<Name>Австралийский доллар</Name>\n" +
            "<Value>44,8978</Value>\n" +
            "</Valute>\n" +
            "</ValCurs>";
    @Test
    public void read() throws Exception {
        ValCurs valCurs = XmlParser.read(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8.name())));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(valCurs.getDate());
        assertEquals(calendar.get(Calendar.YEAR), 2017);
        assertEquals(calendar.get(Calendar.MONTH), 9);
        assertEquals(calendar.get(Calendar.DAY_OF_MONTH), 7);
        assertEquals(valCurs.getName(), "Foreign Currency Market");
        assertEquals(valCurs.getValutes().size(), 1);

        Valute valute = valCurs.getValutes().get(0);
        assertEquals(valute.getId(), "R01010");
        assertEquals(valute.getNumCode(), 36);
        assertEquals(valute.getCharCode(), "AUD");
        assertEquals(valute.getNominal(), 1);
        assertEquals(valute.getName(), "Австралийский доллар");
        float val = valute.getValue();
        assertTrue(val > 44.8977 && val < 44.8979);
    }

    @Test(expected = Exception.class)
    public void exceptionEmptyRead() throws Exception{
        ValCurs valCurs = XmlParser.read(new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8.name())));
    }

    @Test
    public void writeRead() throws Exception {
        Calendar calendar = Calendar.getInstance();
        Valute valute_ = new Valute("R01010", 36, "AUD", 1, "Австралийский доллар", 44.8978f);
        List<Valute> list = new ArrayList<>();
        list.add(valute_);
        calendar.set(Calendar.YEAR, 2017);
        calendar.set(Calendar.MONTH, 9);
        calendar.set(Calendar.DAY_OF_MONTH, 7);
        ValCurs valCurs_ = new ValCurs(list, calendar.getTime(), "Foreign Currency Market");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XmlParser.write(valCurs_, baos);
        String src = baos.toString(StandardCharsets.UTF_8.name());

        ValCurs valCurs = XmlParser.read(new ByteArrayInputStream(src.getBytes(StandardCharsets.UTF_8.name())));
        calendar.setTime(valCurs.getDate());
        assertEquals(calendar.get(Calendar.YEAR), 2017);
        assertEquals(calendar.get(Calendar.MONTH), 9);
        assertEquals(calendar.get(Calendar.DAY_OF_MONTH), 7);
        assertEquals(valCurs.getName(), "Foreign Currency Market");
        assertEquals(valCurs.getValutes().size(), 1);

        Valute valute = valCurs.getValutes().get(0);
        assertEquals(valute.getId(), "R01010");
        assertEquals(valute.getNumCode(), 36);
        assertEquals(valute.getCharCode(), "AUD");
        assertEquals(valute.getNominal(), 1);
        assertEquals(valute.getName(), "Австралийский доллар");
        float val = valute.getValue();
        assertTrue(val > 44.8977 && val < 44.8979);
    }
}