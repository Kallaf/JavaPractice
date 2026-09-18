package javaproject.utils.exceptions;

public class ConversionNotSupportedException extends Exception {
    public ConversionNotSupportedException(String fromCurrency, String toCurrency) {
        super("Conversion not supported: " + fromCurrency + " to " + toCurrency);
    }
}