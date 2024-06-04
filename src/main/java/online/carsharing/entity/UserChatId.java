package online.carsharing.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users_chat_id")
@Getter
@Setter
public class UserChatId {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_telegram_id", nullable = false)
    private Long userTelegramId;

    @Column(name = "chat_id", nullable = false)
    private Long chatId;
}
