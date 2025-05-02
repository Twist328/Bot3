package bot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.objects.InputFile;
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
    private void sendGif(String chatId, String caption, String gifUrl) throws TelegramApiException {
        SendAnimation gif = SendAnimation.builder()
                .chatId(chatId)
                .caption(caption)
                .animation(new InputFile(gifUrl))
                .build();
        sender.execute(gif);
    }

    private void handleCallback(CallbackQuery callback) throws TelegramApiException {
        String data = callback.getData();
        Message message = callback.getMessage();

        AnswerCallbackQuery ack = AnswerCallbackQuery.builder()
                .callbackQueryId(callback.getId())
                .build();
        sender.execute(ack);

        switch (data) {
            case "START" -> edit(message, "👋 Добро пожаловать! Выберите команду ниже:", InlineKeyboardUtil.getMainMenu());
            case "DATE" -> edit(message, "📅 Сегодня: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")), InlineKeyboardUtil.getMainMenu());
            case "TIME" -> edit(message, "⏰ Сейчас: " + LocalTime.now().withSecond(0).format(DateTimeFormatter.ofPattern("HH:mm")), InlineKeyboardUtil.getMainMenu());
            case "HELP" -> edit(message, "🆘 Я могу показать:\n— Погоду 🌦\n— Дату 📅\n— Время ⏰\n— Курсы валют 💱 от ЦБ РФ", InlineKeyboardUtil.getMainMenu());
            case "WEATHER" -> edit(message, "🌍 Введите название города (на русском или английском):", InlineKeyboardUtil.getMainMenu());
            case "CBR" -> edit(message, cbrService.getRates(), InlineKeyboardUtil.getMainMenu());
            default -> edit(message, "⚠ Неизвестная команда", InlineKeyboardUtil.getMainMenu());
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
            String reply = weatherService.getWeatherByCity(text);
            send(chatId, reply, InlineKeyboardUtil.getMainMenu());
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