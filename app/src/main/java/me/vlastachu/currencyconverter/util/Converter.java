package me.vlastachu.currencyconverter.util;

import me.vlastachu.currencyconverter.model.Valute;

public class Converter {
    public static float convert(float fromValue, Valute fromValute, Valute toValute) {
        return fromValue * ((fromValute.getValue() * fromValute.getNominal()) / (toValute.getValue() * toValute.getNominal()));
    }
}
