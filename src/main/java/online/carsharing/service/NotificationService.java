package online.carsharing.service;

import online.carsharing.entity.Car;
import online.carsharing.entity.Rental;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface NotificationService {
    void sendMessage(Update update, String text);

    void createCarNotification(Car car);

    void createRentalNotification(Rental rental);

    void attachUserToChatId(Update update);

    void sendNotification(String message);
}
