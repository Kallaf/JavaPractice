package javaproject.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javaproject.utils.exceptions.ConversionNotSupportedException;

public class CurrencyConverter {
    private final Map<String, Map<String, Double>> graph;
    public CurrencyConverter(List<Object[]> exchangeRates) {
        this.graph = buildGraph(exchangeRates);
    }

    public double convert(double amount, String fromCurrency, String toCurrency) 
        throws ConversionNotSupportedException {
        double rate = findRate(fromCurrency, toCurrency);
        return amount * rate;
    }


    private Map<String, Map<String, Double>> buildGraph(List<Object[]> exchangeRates) {
        Map<String, Map<String, Double>> graph = new HashMap<>();
        for (Object[] rate : exchangeRates) {
            String from = (String) rate[0];
            String to = (String) rate[1];
            Double value = (Double) rate[2];
            Double inverseValue = (Double) (1.0 / value);
            graph.computeIfAbsent(from, k -> new HashMap<>()).put(to, value);
            graph.computeIfAbsent(to, k -> new HashMap<>()).put(from, inverseValue);
        }
        return graph;
    }
    
    private double findRate(String fromCurrency, String toCurrency) throws ConversionNotSupportedException {
        double rate = dfs(fromCurrency, toCurrency, 1.0, new HashMap<>());
        if (rate == -1) {
            throw new ConversionNotSupportedException(fromCurrency, toCurrency);
        }
        return rate;
    }

    private double dfs(String fromCurrency, String toCurrency, double accumulatedRate, Map<String, Boolean> visited) {
        if (fromCurrency.equals(toCurrency)) {
            return accumulatedRate;
        }
        visited.put(fromCurrency, true);
        if (graph.containsKey(fromCurrency)) {
            for (Map.Entry<String, Double> entry : graph.get(fromCurrency).entrySet()) {
                String nextCurrency = entry.getKey();
                double rate = entry.getValue();
                if (!visited.containsKey(nextCurrency)) {
                    double result = dfs(nextCurrency, toCurrency, accumulatedRate * rate, visited);
                    if (result != -1) {
                        return result;
                    }
                }
            }
        }
        return -1;
    }

}