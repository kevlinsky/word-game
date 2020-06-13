package ru.kpfu.itis.kevlinsky.citiesbots.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kpfu.itis.kevlinsky.citiesbots.models.TelegramChat;

@Repository
public interface TelegramChatRepository extends JpaRepository<TelegramChat, Integer> {
    TelegramChat findByChat(String chat);
}
