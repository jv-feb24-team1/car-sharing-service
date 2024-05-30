package online.carsharing.service.impl;

import lombok.RequiredArgsConstructor;
import online.carsharing.entity.User;
import online.carsharing.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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

//    @Value("${bot.botname}")
//    private String botName;
//
//    @Value("${bot.bottoken}")
//    private String botToken;

    @Override
    public String getBotUsername() {
        return "testuser30052024_bot";
    }

    @Override
    public String getBotToken() {
        return "7180426233:AAHm-PmgYLcjG1yliXeVUlAHUerw0ziiMvA";
    }

    @Async
    @Override
    public void onUpdateReceived(Update update) {
        Long chatId = update.getMessage().getChatId();
        boolean hasText = update.hasMessage() && update.getMessage().hasText();
        String command = update.getMessage().getText();
        if (hasText && command.equals("/addMe")) {
            if (!userService.existsByChatId(chatId)) {
                User user = new User();
                user.setChatId(chatId);
                user.setSubscribe(false);
                userService.save(user);
                sendMessage(update, "Thats OK!");
            } else {
                sendMessage(update, "U are in table");
            }
        } else if (hasText && command.equals("/me")) {
            sendMessage(update, userService.inTable(chatId).toString());
        }
    }

    public void sendMessage(Update update, String text) {
        try {
            execute(
                    SendMessage.builder()
                            .chatId(update.hasMessage() ? update.getMessage().getChatId()
                                    : update.getCallbackQuery().getFrom().getId())
                            .text(text)
                            .build());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
