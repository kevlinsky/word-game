package ru.kpfu.itis.kevlinsky.citiesbots;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.kpfu.itis.kevlinsky.citiesbots.discord.DiscordBot;
import ru.kpfu.itis.kevlinsky.citiesbots.discord.DiscordListener;
import ru.kpfu.itis.kevlinsky.citiesbots.models.ExecuteClass;
import ru.kpfu.itis.kevlinsky.citiesbots.models.Message;
import ru.kpfu.itis.kevlinsky.citiesbots.services.MessageService;
import ru.kpfu.itis.kevlinsky.citiesbots.telegram.TelegramBot;

@SpringBootApplication
public class CitiesBotsApplication implements CommandLineRunner {

    @Autowired
    public DiscordListener discordListener;

    @Autowired
    public MessageService messageService;

    @Autowired
    public ExecuteClass executeClass;

    public static void main(String[] args) {
        SpringApplication.run(CitiesBotsApplication.class, args);

    }

    @Override
    public void run(String... args) throws Exception {
        DiscordBot.run(discordListener);

        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            TelegramBot telegramBot = new TelegramBot(messageService);
            telegramBotsApi.registerBot(telegramBot);
            executeClass.setTelegramBot(telegramBot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
