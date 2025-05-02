package bot;


import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import io.github.cdimascio.dotenv.Dotenv;

public class WeatherBot extends TelegramLongPollingBot {

    private static final Dotenv dotenv = Dotenv.load();

    private final String botToken = dotenv.get("TELEGRAM_BOT_TOKEN");
    private final String botUsername = dotenv.get("BOT_USERNAME");

    private final BotCommandHandler handler = new BotCommandHandler(this);

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update != null) {
            try {
                handler.handle(update);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
}
