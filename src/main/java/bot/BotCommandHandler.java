package bot;

import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class BotCommandHandler {

    private final AbsSender sender;
    private final WeatherService weatherService = new WeatherService();
    private final CbrService cbrService = new CbrService();

    public BotCommandHandler(AbsSender sender) {
        this.sender = sender;
    }

    public void handle(Update update) throws TelegramApiException {
        if (update.hasCallbackQuery()) {
            handleCallback(update.getCallbackQuery());
        } else if (update.hasMessage() && update.getMessage().hasText()) {
            handleText(update.getMessage());
        }
    }

    private void sendGif(String chatId, String caption, String gifPath) throws TelegramApiException {
        SendAnimation gif = SendAnimation.builder()
                .chatId(chatId)
                .caption(caption)
                .animation(new InputFile(new File(gifPath)))
                .build();
        sender.execute(gif);
    }

    private void handleCallback(CallbackQuery callback) throws TelegramApiException {
        String data = callback.getData();
        Message message = callback.getMessage();
        String chatId = message.getChatId().toString();

        sender.execute(AnswerCallbackQuery.builder()
                .callbackQueryId(callback.getId())
                .build());

        switch (data) {
            case "START" -> edit(message, "\uD83D\uDC4B Добро пожаловать! Выберите команду ниже:", InlineKeyboardUtil.getMainMenu());

            case "DATE" -> {
                sendGif(chatId, "\uD83D\uDCC6 На календаре у нас сегодня:", "src/main/resources/animations/artboard.gif");
                String dateText = "📅 " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy (EEEE)"));
                SendMessage msg = SendMessage.builder()
                        .chatId(chatId)
                        .text(dateText)
                        .replyMarkup(InlineKeyboardUtil.getMainMenu())
                        .build();
                sender.execute(msg);
            }

            case "TIME" -> {
                sendGif(chatId, "⏰ Текущее время:", "src/main/resources/animations/G9dTM.gif");
                String timeText = "🕒 " + LocalTime.now().withSecond(0).format(DateTimeFormatter.ofPattern("HH:mm"))+ "  MCK";
                SendMessage msg = SendMessage.builder()
                        .chatId(chatId)
                        .text(timeText)
                        .replyMarkup(InlineKeyboardUtil.getMainMenu())
                        .build();
                sender.execute(msg);
            }

            case "HELP" -> edit(message, "\uD83D\uDD1A Я могу показать:\n— Погоду 🌦\n— Дату 📅\n— Время ⏰\n— Курсы валют 💱 от ЦБ РФ", InlineKeyboardUtil.getMainMenu());

            case "WEATHER" -> {
                SendAnimation gif = SendAnimation.builder()
                        .chatId(chatId)
                        .caption("\uD83D\uDCCD Введите название города, чтобы узнать погоду")
                        .animation(new InputFile(new File("src/main/resources/animations/3VTR.gif")))
                        .build();
                sender.execute(gif);
            }

            case "CBR" -> {
                sendGif(chatId, "\uD83D\uDCB8 Курсы валют ЦБ РФ:", "src/main/resources/animations/vgif-ru-Считаю, а они не кончаются.gif");
                SendMessage msg = SendMessage.builder()
                        .chatId(chatId)
                        .text(cbrService.getRates())
                        .replyMarkup(InlineKeyboardUtil.getMainMenu())
                        .build();
                sender.execute(msg);
            }

            default -> edit(message, "⚠️ Неизвестная команда", InlineKeyboardUtil.getMainMenu());
        }
    }

    private void handleText(Message message) throws TelegramApiException {
        String text = message.getText().trim();
        String chatId = message.getChatId().toString();

        if (text.startsWith("/start")) {
            send(chatId, "👋 Привет! Я погодный бот. Нажми кнопку ниже ⬇️", InlineKeyboardUtil.getMainMenu());
            String gifUrl = weatherService.getGifForIcon();
            if (gifUrl != null) {
                sendGif(chatId, "🌤 Погода на сегодня в картинке:", gifUrl);
            }
        } else {
            SendAnimation gif = SendAnimation.builder()
                    .chatId(chatId)
                    .caption("\uD83C\uDF27 Прогноз для: " + text)
                    .animation(new InputFile(new File("src/main/resources/animations/99px_ru_animacii_22384_dojdlivaja_pogoda_na_ozere.gif")))
                    .build();
            sender.execute(gif);

            String weatherData = weatherService.getWeatherByCity(text);
            if (weatherData == null || weatherData.isBlank()) {
                weatherData = "❗ Город не найден. Попробуйте ещё раз.";

            }

            SendMessage msg = SendMessage.builder()
                    .chatId(chatId)
                    .text(weatherData)
                    .parseMode("HTML")
                    .replyMarkup(InlineKeyboardUtil.getMainMenu())
                    .disableWebPagePreview(true)
                    .build();
            sender.execute(msg);
        }
    }

    private void send(String chatId, String text, InlineKeyboardMarkup markup) throws TelegramApiException {
        SendMessage msg = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .replyMarkup(markup)
                .build();
        sender.execute(msg);
    }

    private void edit(Message message, String text, InlineKeyboardMarkup markup) throws TelegramApiException {
        EditMessageText editMessage = EditMessageText.builder()
                .chatId(message.getChatId().toString())
                .messageId(message.getMessageId())
                .text(text)
                .replyMarkup(markup)
                .build();
        sender.execute(editMessage);
    }
}