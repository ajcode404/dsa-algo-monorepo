package io.github.ajcode404.wise;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface CurrencyConversionService {

    double convert(String from, String to, double amount);
}

interface ExchangeRateProvider {

    double fetchRate(String from, String to);
}

class CurrencyConversionServiceImpl implements CurrencyConversionService {

    private final ExchangeRateProvider provider;
    private final Map<String, Double> cache = new ConcurrentHashMap<>();

    public CurrencyConversionServiceImpl(ExchangeRateProvider provider) {
        this.provider = provider;
    }

    @Override
    public double convert(String from, String to, double amount) {
        String key = key(from, to);
        // try cached first;
        Double rate = cache.get(key);
        if (rate == null) {
            rate = provider.fetchRate(from, to);
        }
        return amount * rate;
    }

    public void updateRate(String from, String to, double newRate) {
        cache.put(key(from, to), newRate);
    }

    private String key(String from, String to) {
        return from + "->" + to;
    }

    public void clearCache() {
        cache.clear();
    }
}

class FixedRateProvider implements ExchangeRateProvider {

    @Override
    public double fetchRate(String from, String to) {
        if (from.equals("USD") && to.equals("EUR")) return 0.91;
        if (from.equals("EUR") && to.equals("USD")) return 1.00;
        throw new IllegalStateException("Currency conversion from " + from + " -> " + to + " not supported");
    }

    public static void main(String[] args) {
        CurrencyConversionServiceImpl service =
                new CurrencyConversionServiceImpl(new FixedRateProvider());

        double result = service.convert("USD", "EUR", 100);
        System.out.println(result); // 91.0

        service.updateRate("USD", "EUR", 0.95);
        System.out.println(service.convert("USD", "EUR", 100)); // 95.0
    }
}

