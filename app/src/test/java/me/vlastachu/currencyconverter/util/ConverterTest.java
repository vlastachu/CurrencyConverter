package me.vlastachu.currencyconverter.util;

import org.junit.Test;

import me.vlastachu.currencyconverter.model.Valute;

import static org.junit.Assert.*;

public class ConverterTest {
    @Test
    public void convert() throws Exception {
        Valute from = new Valute("", 0, "", 1, "", 9f);
        Valute to = new Valute("", 0, "", 1, "", 3f);
        assertEquals(3f, Converter.convert(1f, from, to), 0.1);
    }

    @Test
    public void convertNominal() throws Exception {
        Valute from = new Valute("", 0, "", 10, "", 9f);
        Valute to = new Valute("", 0, "", 1, "", 3f);
        assertEquals(30f, Converter.convert(1f, from, to), 0.1);
    }
}