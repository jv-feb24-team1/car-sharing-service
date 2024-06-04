package online.carsharing.repository.user;

import java.util.List;
import online.carsharing.entity.UserChatId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserChatIdRepository extends JpaRepository<UserChatId, Long> {
    @Query("SELECT DISTINCT uc.chatId FROM UserChatId uc")
    List<Long> findAllChatIds();
    
    @Query("SELECT uc FROM UserChatId uc WHERE uc.userTelegramId = :userTelegramId")
    UserChatId findByUserByTelegramId(Long userTelegramId);
}
