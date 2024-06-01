package online.carsharing.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import online.carsharing.entity.Car;
import online.carsharing.entity.UserChatId;
import online.carsharing.repository.user.UserChatIdRepository;
import online.carsharing.service.NotificationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl extends TelegramLongPollingBot implements NotificationService {
    private static final String CAR_CREATION_TG_RESPONSE =
            "New car added! \nModel: *%s*\nBrand: *%s*\nType: *%s*\nDaily fee: *%s*";

    private final UserChatIdRepository userChatIdRepository;

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
            String command = update.getMessage().getText().trim();

            if (command.startsWith("/addMe") || command.startsWith("Start")) {
                addUserChatId(update);
            } else {
                sendMessage(update, "Unknown command");
            }
        }
    }

    @Override
    public void addUserChatId(Update update) {
        Long userId = update.getMessage().getFrom().getId().longValue();
        Long chatId = update.getMessage().getChatId();
        UserChatId userChatId = userChatIdRepository.findByUserId(userId);
        if (userChatId == null) {
            userChatId = new UserChatId();
            userChatId.setUserId(userId);
        }
        userChatId.setChatId(chatId);
        userChatIdRepository.save(userChatId);
        sendMessage(update, "You have been added successfully.");
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

    @Override
    public void carCreation(Car car) {
        List<Long> chatIds = userChatIdRepository.findAllChatIds();
        String messageText = String.format(CAR_CREATION_TG_RESPONSE,
                car.getModel(), car.getBrand(),
                car.getType().toString(),
                car.getDailyFee().toString());

        for (Long chatId : chatIds) {
            if (chatId != null) {
                try {
                    SendMessage message = SendMessage.builder()
                            .chatId(chatId.toString())
                            .text(messageText)
                            .parseMode("Markdown")
                            .build();
                    execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
