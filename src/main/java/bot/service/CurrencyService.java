package bot.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class CurrencyService {

    private final Map<String, Double> fakeRates = Map.of(
            "USD", 74.99,
            "EUR", 85.36,
            "GBP", 101.63,
            "JPY", 0.64
    );

    public String getRate(String currencyCode) {
        String upper = currencyCode.toUpperCase();
        if (!fakeRates.containsKey(upper)) {
            return "❗ Валюта не поддерживается: " + currencyCode;
        }

        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
        return String.format("💱 Курс %s: %.2f RUB\\n🕒 %s", upper, fakeRates.get(upper), time);
    }
}
