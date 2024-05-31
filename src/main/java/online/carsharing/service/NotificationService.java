package online.carsharing.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface NotificationService {
    void currentRentals(Update update);

    void sendMessage(Update update, String text);
}
