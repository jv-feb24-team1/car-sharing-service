package online.carsharing.service.impl;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import online.carsharing.repository.RentalRepository;
import online.carsharing.service.NotificationService;
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
public class NotificationServiceImpl extends TelegramLongPollingBot implements NotificationService {
    @Autowired
    private RentalRepository rentalRepository;

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

            if (command.startsWith("/returnStatus")) {
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

    @Override
    public void returnStatus(Update update, Long id) {
        try {
            LocalDate returnDate = rentalRepository.returnDateCheckById(id);
            if (returnDate != null) {
                sendMessage(update, "Return Date: "
                        + returnDate.toString());
            } else {
                sendMessage(update, "No rental found with ID: " + id);
            }
        } catch (Exception e) {
            sendMessage(update, "Error fetching rental info");
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

