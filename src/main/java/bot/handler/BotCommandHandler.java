package bot.handler;

import bot.service.WeatherService;
import bot.service.CurrencyService;
import bot.util.Emoji;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BotCommandHandler {

    private final AbsSender sender;
    private final WeatherService weatherService = new WeatherService();
    private final CurrencyService currencyService = new CurrencyService();

    public BotCommandHandler(AbsSender sender) {
        this.sender = sender;
    }

    public void handle(Message message) throws TelegramApiException {
        String text = message.getText().trim().toUpperCase();

        switch (text) {
            case "/START" -> send(message, "Привет! Я умный погодный бот ☀️🌍\\nВведите название города или выберите команду.");
            case "ПОГОДА" -> send(message, "Введите город, чтобы узнать погоду " + Emoji.SUN);
            case "ВРЕМЯ" -> send(message, "Текущее время (МСК): " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")) + " " + Emoji.CLOCK);
            case "ДАТА" -> send(message, "Сегодня: " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + " " + Emoji.CALENDAR);
            case "/HELP" -> send(message, "Доступные команды:\\n" +
                    "/start — запустить бота\\n" +
                    "погода — узнать погоду\\n" +
                    "время — текущее время\\n" +
                    "дата — сегодняшняя дата\\n" +
                    "или просто введите название города 🌎");
            case "USD", "EUR", "GBP", "JPY" -> send(message, currencyService.getRate(text));
            default -> {
                String reply = weatherService.getWeatherByCity(text);
                send(message, reply);
            }
        }
    }

    private void send(Message message, String text) throws TelegramApiException {
        SendMessage response = SendMessage.builder()
                .chatId(message.getChatId().toString())
                .text(text)
                .build();
        sender.execute(response);
    }
}
