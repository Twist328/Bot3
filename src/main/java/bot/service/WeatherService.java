package bot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class WeatherService {

    private static final String API_KEY = "d934a6dc84c5578a6352162d25ccc620";
    private static final String BASE_URL =
            "https://api.openweathermap.org/data/2.5/weather?q=%s&units=metric&lang=ru&appid=%s";

    private final ObjectMapper mapper = new ObjectMapper();

    public String getWeatherByCity(String cityInput) {
        try {
            // Корректно кодируем кириллические (и любые) символы
            String encodedCity = URLEncoder.encode(cityInput.trim(), StandardCharsets.UTF_8);
            String urlStr = String.format(BASE_URL, encodedCity, API_KEY);

            JsonNode root = mapper.readTree(new URL(urlStr));

            String name = root.path("name").asText();
            double temp = root.path("main").path("temp").asDouble();
            int humidity = root.path("main").path("humidity").asInt();
            String description = root.path("weather").get(0).path("description").asText();
            String icon = root.path("weather").get(0).path("icon").asText();

            return String.format(Locale.ROOT,
                    "📍 Город: %s\n🌡 Температура: %.1f°C\n💧 Влажность: %d%%\n🌥️ Погода: %s\n%s",
                    name, temp, humidity, capitalize(description),
                    getIconUrl(icon));

        } catch (IOException e) {
            return "❌ Не удалось найти город: " + cityInput + "\nПопробуйте ввести название иначе.";
        }
    }

    private String capitalize(String input) {
        if (input == null || input.isEmpty()) return input;
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    private String getIconUrl(String icon) {
        return "http://openweathermap.org/img/w/" + icon + ".png";
    }
}