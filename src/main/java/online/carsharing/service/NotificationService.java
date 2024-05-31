package online.carsharing.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface NotificationService {
    void returnStatus(Update update, Long id);

    void sendMessage(Update update, String text);
}
