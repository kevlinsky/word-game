package ru.kpfu.itis.kevlinsky.citiesbots.discord;

import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.util.List;

public class DiscordBot {
    public static JDA jda;

    public static void run(ListenerAdapter eventListener) throws LoginException {
        JDABuilder builder = new JDABuilder(AccountType.BOT);
        builder.setToken("NzA5MzA3NjgxODI2OTMwNzI5.XrkApg.uikC_p3u0dmjxpW66NJIqY3VP4c");
        builder.addEventListeners(eventListener);
        jda = builder.build();
    }
}
