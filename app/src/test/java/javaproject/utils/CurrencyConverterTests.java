package javaproject.utils;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import javaproject.utils.exceptions.ConversionNotSupportedException;


public class CurrencyConverterTests {
    private final CurrencyConverter converter;
    public CurrencyConverterTests() {
        List<Object[]> rates = new ArrayList<>();
        rates.add(new Object[]{"USD", "JPY", 100.0});
        rates.add(new Object[]{"JPY", "CHN", 20.0});
        rates.add(new Object[]{"CHN", "THAI", 200.0});

        converter = new CurrencyConverter(rates);
    }
    @Test
    public void testConversionNotSupported() {
        try {
            converter.convert(100, "USD", "EUR");
            assert false : "Expected ConversionNotSupportedException to be thrown";
        } catch (ConversionNotSupportedException e) {
            assert e.getMessage().equals("Conversion not supported: USD to EUR");
        }
    }

    @Test
    public void testSuccessfulConversion() {
        try {
            double result = converter.convert(1, "USD", "THAI");
            assert result == 100.0 * 20.0 * 200.0 : "Expected conversion result to be 400000.0";
        } catch (ConversionNotSupportedException e) {
            assert false : "Did not expect ConversionNotSupportedException to be thrown";
        }
    }
}