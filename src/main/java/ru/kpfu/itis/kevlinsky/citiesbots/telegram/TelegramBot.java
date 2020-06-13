package ru.kpfu.itis.kevlinsky.citiesbots.telegram;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.kpfu.itis.kevlinsky.citiesbots.services.MessageService;

public class TelegramBot extends TelegramLongPollingBot {
    private MessageService messageService;

    public TelegramBot(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        messageService.receivedFromTelegram(update);
    }

    @Override
    public String getBotUsername() {
        return "WordGameBot";
    }

    @Override
    public String getBotToken() {
        return "1281569481:AAGKyhUnit3-7kkfCA6Qp7d6oR4JAlVPCug";
    }
}
