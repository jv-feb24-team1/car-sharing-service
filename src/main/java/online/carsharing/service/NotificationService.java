package online.carsharing.service;

import online.carsharing.entity.Car;
import online.carsharing.entity.Rental;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface NotificationService {
    void sendMessage(Update update, String text);

    void createCar(Car car);

    void createRental(Rental rental);

    void addUserChatId(Update update);
}
