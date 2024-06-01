package online.carsharing.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import online.carsharing.entity.Car;
import online.carsharing.entity.UserChatId;
import online.carsharing.repository.car.CarRepository;
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

    private final UserChatIdRepository userChatIdRepository;
    private final CarRepository carRepository;

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

            if (command.startsWith("/addMe")) {
                addUserChatId(update);
            } else if (command.startsWith("/getModel")) {
                String[] parts = command.split(" ");
                if (parts.length == 2) {
                    try {
                        Long id = Long.parseLong(parts[1]);
                        returnStatus(update, id);
                    } catch (NumberFormatException e) {
                        sendMessage(update, "Invalid ID format. Please use a number.");
                    }
                } else {
                    sendMessage(update, "Invalid command format. Please use /returnStatus <ID>.");
                }
            } else {
                sendMessage(update, "Unknown command");
            }
        }
    }

    private void addUserChatId(Update update) {
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
    public void returnStatus(Update update, Long id) {
        try {
            String model = carRepository.modelCheckById(id);
            if (model != null) {
                sendMessage(update, "Model: " + model);
            } else {
                sendMessage(update, "No car found with ID: " + id);
            }
        } catch (Exception e) {
            sendMessage(update, "Error fetching car info");
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

    @Override
    public void carCreation(Car car) {
        List<Long> chatIds = userChatIdRepository.findAllChatIds();
        String messageText = "New car available: " + car.toString();

        for (Long chatId : chatIds) {
            if (chatId != null) {
                try {
                    execute(SendMessage
                            .builder()
                            .chatId(chatId
                                    .toString())
                            .text(messageText)
                            .build());
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
