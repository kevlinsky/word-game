package ru.kpfu.itis.kevlinsky.citiesbots.services;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.kpfu.itis.kevlinsky.citiesbots.discord.DiscordBot;
import ru.kpfu.itis.kevlinsky.citiesbots.models.DiscordChat;
import ru.kpfu.itis.kevlinsky.citiesbots.models.ExecuteClass;
import ru.kpfu.itis.kevlinsky.citiesbots.models.Message;
import ru.kpfu.itis.kevlinsky.citiesbots.models.TelegramChat;
import ru.kpfu.itis.kevlinsky.citiesbots.repositories.DiscordChatRepository;
import ru.kpfu.itis.kevlinsky.citiesbots.repositories.MessageRepository;
import ru.kpfu.itis.kevlinsky.citiesbots.repositories.TelegramChatRepository;

@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private TelegramChatRepository telegramChatRepository;

    @Autowired
    private DiscordChatRepository discordChatRepository;

    @Autowired
    private ExecuteClass executeClass;

    public void receivedFromDiscord(GuildMessageReceivedEvent event) throws TelegramApiException {
        if (!event.getAuthor().getName().equals("kevlinsky-bot")){
            String message = event.getMessage().getContentRaw().toLowerCase();
            if (message.equals("!start")){
                String chatId = event.getChannel().getId();
                boolean exist = false;
                if (this.discordChatRepository.findByChat(chatId) != null){
                    exist = true;
                }
                if(!exist){
                    this.discordChatRepository.save(DiscordChat.builder().chat(chatId).build());
                    String text = "Welcome to the game!\n" +
                            "Let's start with the word - " + this.messageRepository.findAll().get(0).getWord() + ".\n" +
                            "You should type in the word which starts with the last letter of this word.\n" +
                            "Begin!";
                    event.getChannel().sendMessage(text).queue();
                } else {
                    event.getChannel().sendMessage("You are already in the game").queue();
                }
            } else if(message.equals("!exit")) {
                String chatId = event.getChannel().getId();
                boolean exist = false;
                DiscordChat discordChat = this.discordChatRepository.findByChat(chatId);
                if (discordChat != null){
                    exist = true;
                }
                if (exist){
                    this.discordChatRepository.delete(discordChat);
                    event.getChannel().sendMessage("You are exit from the game").queue();
                } else {
                    event.getChannel().sendMessage("You should \"!start\" the game at first").queue();
                }
            } else if(this.discordChatRepository.findByChat(event.getChannel().getId()) != null) {
                if (message.startsWith("!new ")) {
                    String newWord = message.replace("!new ", "");
                    this.messageRepository.delete(this.messageRepository.findAll().get(0));
                    this.messageRepository.save(Message.builder().word(newWord).build());
                    for (TelegramChat chat : this.telegramChatRepository.findAll()) {
                        String text = event.getAuthor().getName() + " from Discord set \"" + newWord + "\" as a new word for the game";
                        SendMessage sendMessage = new SendMessage(chat.getChat(), text);
                        executeClass.getTelegramBot().execute(sendMessage);

                    }
                    for (Guild guild : DiscordBot.jda.getSelfUser().getMutualGuilds()) {
                        for (TextChannel textChannel : guild.getTextChannels()) {
                            if (this.discordChatRepository.findByChat(textChannel.getId()) != null) {
                                String text = event.getAuthor().getName() + " set \"" + newWord + "\" as a new word for the game";
                                textChannel.sendMessage(text).queue();
                            }
                        }
                    }
                } else {
                    String word = this.messageRepository.findAll().get(0).getWord().toLowerCase();
                    String lastLetter = word.split("")[word.length() - 1];
                    if (message.startsWith(lastLetter) && notContainsSpecialSymbols(message) && notEndsWithSpecialLetters(message)){
                        this.messageRepository.delete(this.messageRepository.findAll().get(0));
                        this.messageRepository.save(Message.builder().word(message).build());
                        for (TelegramChat chat : this.telegramChatRepository.findAll()) {
                            String text = "(Discord) " + event.getAuthor().getName() + ": " + message + "\n" +
                                    "Next word should starts with - " + message.split("")[message.length() - 1];
                            SendMessage sendMessage = new SendMessage(chat.getChat(), text);
                            executeClass.getTelegramBot().execute(sendMessage);

                        }
                        for (Guild guild : DiscordBot.jda.getSelfUser().getMutualGuilds()) {
                            for (TextChannel textChannel : guild.getTextChannels()) {
                                if (this.discordChatRepository.findByChat(textChannel.getId()) != null) {
                                    if (textChannel.getId().equals(event.getChannel().getId())){
                                        event.getChannel().sendMessage("Next word should starts with - " + message.split("")[message.length() - 1]).queue();
                                    } else {
                                        String text = "(Discord) " + event.getAuthor().getName() + ": " + message + "\n" +
                                                      "Next word should starts with - " + message.split("")[message.length() - 1];
                                        textChannel.sendMessage(text).queue();
                                    }
                                }
                            }
                        }
                    } else {
                        event.getChannel().sendMessage("Incorrect word").queue();
                    }
                }
            } else {
                event.getChannel().sendMessage("Please \"!start\" the game at first").queue();
            }
        }
    }

    public void receivedFromTelegram(Update update){
        String message = update.getMessage().getText().toLowerCase();
        try {
            if (message.equals("/start")) {
                long chadId = update.getMessage().getChatId();
                boolean exist = false;
                if (this.telegramChatRepository.findByChat(String.valueOf(chadId)) != null) {
                    exist = true;
                }
                if (!exist) {
                    this.telegramChatRepository.save(TelegramChat.builder().chat(String.valueOf(chadId)).build());
                    String text = "Welcome to the game!\n" +
                                  "Let's start with the word - " + this.messageRepository.findAll().get(0).getWord() + ".\n" +
                                  "You should type in the word which starts with the last letter of this word.\n" +
                                  "Begin!";
                    this.executeClass.getTelegramBot().execute(new SendMessage(chadId, text));
                } else {
                    this.executeClass.getTelegramBot().execute(new SendMessage(chadId, "You are already in the game"));
                }
            } else if(message.equals("/exit")){
                long chadId = update.getMessage().getChatId();
                boolean exist = false;
                TelegramChat telegramChat = this.telegramChatRepository.findByChat(String.valueOf(chadId));
                if (telegramChat != null) {
                    exist = true;
                }
                if (exist) {
                    this.telegramChatRepository.delete(telegramChat);
                    String text = "You are exit from the game";
                    this.executeClass.getTelegramBot().execute(new SendMessage(chadId, text));
                } else {
                    String text = "You should \"/start\" the game at first";
                    this.executeClass.getTelegramBot().execute(new SendMessage(chadId, text));
                }
            } else {
                if (this.telegramChatRepository.findByChat(String.valueOf(update.getMessage().getChatId())) != null){
                    if (message.startsWith("/new ")){
                        String newWord = message.replace("/new ", "");
                        this.messageRepository.delete(this.messageRepository.findAll().get(0));
                        this.messageRepository.save(Message.builder().word(newWord).build());
                        for (TelegramChat chat : this.telegramChatRepository.findAll()) {
                            String text = update.getMessage().getFrom().getUserName() + " set \"" + newWord + "\" as a new word for the game";
                            SendMessage sendMessage = new SendMessage(chat.getChat(), text);
                            executeClass.getTelegramBot().execute(sendMessage);

                        }
                        for (Guild guild : DiscordBot.jda.getSelfUser().getMutualGuilds()) {
                            String text = update.getMessage().getFrom().getUserName() + " from Telegram set \"" + newWord + "\" as a new word for the game";
                            for (TextChannel textChannel : guild.getTextChannels()) {
                                if (this.discordChatRepository.findByChat(textChannel.getId()) != null) {
                                    textChannel.sendMessage(text).queue();
                                }
                            }
                        }
                    } else {
                        String word = this.messageRepository.findAll().get(0).getWord().toLowerCase();
                        String lastLetter = word.split("")[word.length() - 1];
                        if (message.startsWith(lastLetter) && notContainsSpecialSymbols(message) && notEndsWithSpecialLetters(message)){
                            this.messageRepository.delete(this.messageRepository.findAll().get(0));
                            this.messageRepository.save(Message.builder().word(message).build());
                            for (TelegramChat chat : this.telegramChatRepository.findAll()) {
                                if (chat.getChat().equals(String.valueOf(update.getMessage().getChatId()))){
                                    String text = "Next word should starts with - " + message.split("")[message.length() - 1];
                                    SendMessage sendMessage = new SendMessage(update.getMessage().getChatId(), text);
                                    executeClass.getTelegramBot().execute(sendMessage);
                                } else {
                                    String text = "(Telegram) " + update.getMessage().getFrom().getUserName() + ": " + message + "\n" +
                                            "Next word should starts with - " + message.split("")[message.length() - 1];
                                    SendMessage sendMessage = new SendMessage(chat.getChat(), text);
                                    executeClass.getTelegramBot().execute(sendMessage);
                                }
                            }
                            for (Guild guild : DiscordBot.jda.getSelfUser().getMutualGuilds()) {
                                for (TextChannel textChannel : guild.getTextChannels()) {
                                    if (this.discordChatRepository.findByChat(textChannel.getId()) != null) {
                                        String text = "(Telegram) " + update.getMessage().getFrom().getUserName() + ": " + message + "\n" +
                                                      "Next word should starts with - " + message.split("")[message.length() - 1];
                                        textChannel.sendMessage(text).queue();
                                    }
                                }
                            }
                        } else {
                            SendMessage sendMessage = new SendMessage(update.getMessage().getChatId(), "Incorrect word");
                            this.executeClass.getTelegramBot().execute(sendMessage);
                        }
                    }
                } else {
                    SendMessage sendMessage = new SendMessage(update.getMessage().getChatId(), "Please \"/start\" the game at first");
                    this.executeClass.getTelegramBot().execute(sendMessage);
                }
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private boolean notContainsSpecialSymbols(String message){
        String specialSymbols = "!@#$%^&*()_+\"№;:?-=\\|{}[],.'`~";
        boolean result = false;
        for(char letter: specialSymbols.toCharArray()){
            if(message.contains(String.valueOf(letter))){
                result = true;
                break;
            }
        }
        return !result;
    }

    private boolean notEndsWithSpecialLetters(String message){
        String specialLetters = "ъьы";
        boolean result = false;
        for(char letter: specialLetters.toCharArray()){
            if(message.contains(String.valueOf(letter))){
                result = true;
                break;
            }
        }
        return !result;
    }
}
