package online.carsharing.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import online.carsharing.entity.Car;
import online.carsharing.entity.Rental;
import online.carsharing.entity.User;
import online.carsharing.entity.UserChatId;
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
            "New car added! \nModel: *%s*\nBrand: *%s*\nType: *%s*\nDaily fee: *%s*";
    private static final String RENTAL_CREATION_TG_RESPONSE =
            "New rental created! \nRental Date: *%s*\nReturn Date: *%s*\nCar: *%s*\nTaken by: *%s*";
    private static final String TG_COMMAND_ADD_ME = "/addMe";
    private static final String TG_COMMAND_START = "Start";
    private static final String TG_RESPONSE_UNKNOWN_COMMAND = "Unknown command";
    private static final String TG_RESPONSE_ADDED_SUCCESSFULLY = "Unknown command";
    private static final String TG_RESPONSE_EMAIL_NOTFOUND =
            "User does not have an Email in DB Be contact client immediately!";

    private final UserChatIdRepository userChatIdRepository;
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
                addUserChatId(update);
            } else {
                sendMessage(update, TG_RESPONSE_UNKNOWN_COMMAND);
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
        sendMessage(update, TG_RESPONSE_ADDED_SUCCESSFULLY);
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

    @Override
    public void rentalCreation(Rental rental) {
        List<Long> chatIds = userChatIdRepository.findAllChatIds();
        String email = userRepository.findById(rental.getUser().getId())
                .map(User::getEmail)
                .orElse(TG_RESPONSE_EMAIL_NOTFOUND);
        String messageText = String.format(RENTAL_CREATION_TG_RESPONSE,
                rental.getRentalDate(),
                rental.getReturnDate(),
                carRepository.modelCheckById(rental.getCar().getId()),
                email
        );
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
