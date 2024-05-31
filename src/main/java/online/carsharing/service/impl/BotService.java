package online.carsharing.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import online.carsharing.entity.User;
import online.carsharing.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Controller
@RequiredArgsConstructor
public class BotService extends TelegramLongPollingBot {
    @Autowired
    private UserService userService;

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
            Long chatId = update.getMessage().getChatId();
            String command = update.getMessage().getText();

            if (command.equals("/addMe")) {
                handleAddMeCommand(chatId, update);
            } else if (command.equals("/me")) {
                handleMeCommand(chatId, update);
            } else {
                sendMessage(update, "Unknown command");
            }
        }
    }

    private void handleAddMeCommand(Long chatId, Update update) {
        if (!userService.existsByChatId(chatId)) {
            User user = new User();
            user.setChatId(chatId);
            user.setSubscribe(false);
            userService.save(user);
            sendMessage(update, "Thats OK!");
        } else {
            sendMessage(update, "You are already in the table");
        }
    }

    private void handleMeCommand(Long chatId, Update update) {
        try {
            sendMessage(update, userService.inTable(chatId).toString());
        } catch (Exception e) {
            sendMessage(update, "Error fetching user info");
            e.printStackTrace();
        }
    }

    private void sendMessage(Update update, String text) {
        Long chatId = update.hasMessage() ? update.getMessage().getChatId() : update.getCallbackQuery().getFrom().getId();
        try {
            execute(SendMessage.builder().chatId(chatId.toString()).text(text).build());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
