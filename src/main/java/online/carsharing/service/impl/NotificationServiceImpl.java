package online.carsharing.service.impl;

import lombok.RequiredArgsConstructor;
import online.carsharing.service.NotificationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Controller
@RequiredArgsConstructor
public class NotificationServiceImpl extends TelegramLongPollingBot implements NotificationService {

    @Value("${bot.botname}")
    private String botName;

    @Value("${bot.bottoken}")
    private String botToken;

    @Override
    public String getBotUsername() {
        return this.botName;
    }

    @Override
    public String getBotToken() {
        return this.botToken;
    }

    @Async
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String command = update.getMessage().getText();
            switch (command) {
                case "/rentalStatus":
                    currentRentals(update);
                    break;
                default:
                    sendMessage(update, "Unknown command");
                    break;
            }
        }
    }
    @Override
    public void currentRentals(Update update) {
        try {
            sendMessage(update, "Hi1");
        } catch (Exception e) {
            sendMessage(update, "Error fetching user info");
            e.printStackTrace();
        }
    }

    @Override
    public void sendMessage(Update update, String text) {
        Long chatId = update.hasMessage()
                ? update.getMessage().getChatId()
                : update.getCallbackQuery().getFrom().getId();
        try {
            execute(SendMessage.builder().chatId(chatId.toString()).text(text).build());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}

