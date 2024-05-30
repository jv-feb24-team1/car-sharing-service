package online.carsharing.config;

import online.carsharing.service.impl.BotService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
@ComponentScan("online.carsharing")
public class AppConfig {

    @Bean
    public BotService registration() {
        BotService bot = new BotService();
        try {
            new TelegramBotsApi(DefaultBotSession.class).registerBot(bot);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bot;
    }
}
