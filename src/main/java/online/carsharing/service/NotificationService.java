package online.carsharing.service;

import online.carsharing.entity.Car;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface NotificationService {
    void sendMessage(Update update, String text);

    void carCreation(Car car);

    void addUserChatId(Update update);
}
