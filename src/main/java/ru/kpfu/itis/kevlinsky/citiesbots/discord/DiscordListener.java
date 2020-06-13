package ru.kpfu.itis.kevlinsky.citiesbots.discord;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.kpfu.itis.kevlinsky.citiesbots.repositories.MessageRepository;
import ru.kpfu.itis.kevlinsky.citiesbots.services.MessageService;

import javax.annotation.Nonnull;

@Component
public class DiscordListener extends ListenerAdapter {
    @Autowired
    private MessageService messageService;

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        try {
            messageService.receivedFromDiscord(event);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
