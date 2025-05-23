package bot;

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
    private String lastIconCode; // для GIF

    public String getWeatherByCity(String cityInput) {
        try {
            String encodedCity = URLEncoder.encode(cityInput.trim(), StandardCharsets.UTF_8);
            String urlStr = String.format(BASE_URL, encodedCity, API_KEY);
            JsonNode root = mapper.readTree(new URL(urlStr));

            String name = root.path("name").asText();
            double temp = root.path("main").path("temp").asDouble();
            int humidity = root.path("main").path("humidity").asInt();
            String description = root.path("weather").get(0).path("description").asText();
            String icon = root.path("weather").get(0).path("icon").asText();
            this.lastIconCode = icon;

            String emoji = switch (icon.charAt(0)) {
                case '0', '1' -> "☀️";
                case '2' -> "🌤️";
                case '3', '4' -> "🌧️";
                case '5', '6' -> "🌩️";
                case '7', '8' -> "☁️";
                case '9' -> "❄️";
                default -> "🌍";
            };

            return String.format(Locale.ROOT,
                    "%s 📍 Город: %s\n🌡 Температура: %.1f°C\n💧 Влажность: %d%%\n🌥️ Погода: %s\n\n<a href=\"https://openweathermap.org\">Источник</a>",
                    emoji, name, temp, humidity, capitalize(description));

        } catch (IOException e) {
            return "❌ Не удалось найти город: " + cityInput + "\nПопробуйте ввести название иначе.";
        }
    }
    public String getGifForIcon() {
        if (lastIconCode == null || lastIconCode.isEmpty()) return null;

        return switch (lastIconCode.charAt(0)) {
            case '0', '1' -> "https://media.giphy.com/media/3o7aD2saalBwwftBIY/giphy.gif"; // солнце
            case '2' -> "https://media.giphy.com/media/26xBsziN5wHzr0LtO/giphy.gif";      // облачно
            case '3', '4' -> "https://media.giphy.com/media/3o6ZtaO9BZHcOjmErm/giphy.gif";  // дождь
            case '9' -> "https://media.giphy.com/media/l0MYt5jPR6QX5pnqM/giphy.gif";        // снег
            default -> null;
        };
    }

    private String capitalize(String input) {
        if (input == null || input.isEmpty()) return input;
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    private String getEmojiForDescription(String description) {
        return switch (description.toLowerCase()) {
            case "ясно" -> "☀️";
            case "небольшая облачность" -> "🌤️";
            case "переменная облачность", "облачно с прояснениями" -> "⛅";
            case "пасмурно", "пасмурная погода" -> "☁️";
            case "дождь", "небольшой дождь", "ливень" -> "🌧️";
            case "гроза" -> "⛈️";
            case "снег", "небольшой снег" -> "❄️";
            case "туман", "дымка" -> "🌫️";
            default -> "🌈";
        };
    }
}
