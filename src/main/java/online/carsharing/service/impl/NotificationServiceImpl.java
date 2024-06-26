package online.carsharing.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import online.carsharing.entity.Car;
import online.carsharing.entity.Rental;
import online.carsharing.entity.User;
import online.carsharing.entity.UserChatId;
import online.carsharing.exception.TelegramUnableToSendMessageException;
import online.carsharing.repository.car.CarRepository;
import online.carsharing.repository.user.UserChatIdRepository;
import online.carsharing.repository.user.UserRepository;
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
            "New car added! \nBrand: %s\nModel: %s\nType: %s\nDaily fee: %s";
    private static final String RENTAL_CREATION_TG_RESPONSE =
            "New rental created! \nRental Date: %s\nReturn Date: %s\nCar: %s\nTaken by: %s";
    private static final String TG_COMMAND_ADD_ME = "/addMe";
    private static final String TG_COMMAND_START = "Start";
    private static final String TG_RESPONSE_UNKNOWN_COMMAND = "Unknown command";
    private static final String TG_RESPONSE_ADDED_SUCCESSFULLY =
            "Registration successful! Welcome aboard!";
    private static final String TG_RESPONSE_EMAIL_NOTFOUND =
            "User does not have an Email in DB Be contact client immediately!";
    private static final String TG_UNABLE_TO_SEND_MESSAGE =
            "Unable to send message, exception: ";

    private final UserChatIdRepository chatRepository;
    private final UserRepository userRepository;
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

            if (command.startsWith(TG_COMMAND_ADD_ME) || command.startsWith(TG_COMMAND_START)) {
                attachUserToChatId(update);
            } else {
                sendMessage(update, TG_RESPONSE_UNKNOWN_COMMAND);
            }
        }
    }

    @Override
    public void attachUserToChatId(Update update) {
        Long userTelegramId = update.getMessage().getFrom().getId();
        Long chatId = update.getMessage().getChatId();
        UserChatId userChatId = chatRepository.findByUserByTelegramId(userTelegramId);
        if (userChatId == null) {
            userChatId = new UserChatId();
            userChatId.setUserTelegramId(userTelegramId);
        }
        userChatId.setChatId(chatId);
        chatRepository.save(userChatId);
        sendMessage(update, TG_RESPONSE_ADDED_SUCCESSFULLY);
    }

    @Override
    public void sendMessage(Update update, String messageText) {
        Long chatId = update.hasMessage()
                ? update.getMessage().getChatId()
                : update.getCallbackQuery().getFrom().getId();
        executeMessage(chatId, messageText);
    }

    @Override
    public void createCarNotification(Car car) {
        String messageText = String.format(CAR_CREATION_TG_RESPONSE,
                car.getBrand(),
                car.getModel(),
                car.getType().toString(),
                car.getDailyFee().toString());
        sendMessageToAllChats(messageText);
    }

    @Override
    public void createRentalNotification(Rental rental) {
        String email = userRepository.findById(rental.getUser().getId())
                .map(User::getEmail)
                .orElse(TG_RESPONSE_EMAIL_NOTFOUND);
        String messageText = String.format(RENTAL_CREATION_TG_RESPONSE,
                rental.getRentalDate(),
                rental.getReturnDate(),
                carRepository.modelCheckById(rental.getCar().getId()),
                email
        );
        sendMessageToAllChats(messageText);
    }

    @Override
    public void sendNotification(String message) {
        sendMessageToAllChats(message);
    }

    private void sendMessageToAllChats(String messageText) {
        List<Long> chatIds = chatRepository.findAllChatIds();
        for (Long chatId : chatIds) {
            if (chatId != null) {
                executeMessage(chatId, messageText);
            }
        }
    }

    private void executeMessage(Long chatId, String messageText) {
        try {
            execute(SendMessage
                    .builder()
                    .chatId(chatId.toString())
                    .text(messageText)
                    .build());
        } catch (TelegramApiException e) {
            throw new TelegramUnableToSendMessageException(
                    TG_UNABLE_TO_SEND_MESSAGE + e);
        }
    }
}
