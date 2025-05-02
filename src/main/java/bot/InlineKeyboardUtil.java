package bot;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

public class InlineKeyboardUtil {

    public static InlineKeyboardMarkup getMainMenu() {
        List<InlineKeyboardButton> row1 = List.of(
                createButton("📍 Погода", "WEATHER"),
                createButton("📅 Дата", "DATE")
        );
        List<InlineKeyboardButton> row2 = List.of(
                createButton("⏰ Время", "TIME"),
                createButton("💱 Курс ЦБ", "CBR")
        );
        List<InlineKeyboardButton> row3 = List.of(
                createButton("🆘 Помощь", "HELP"),
                createButton("▶️ Старт", "START")
        );

        return InlineKeyboardMarkup.builder()
                .keyboard(List.of(row1, row2, row3))
                .build();
    }

    private static InlineKeyboardButton createButton(String text, String callbackData) {
        return InlineKeyboardButton.builder()
                .text(text)
                .callbackData(callbackData)
                .build();
    }
}
