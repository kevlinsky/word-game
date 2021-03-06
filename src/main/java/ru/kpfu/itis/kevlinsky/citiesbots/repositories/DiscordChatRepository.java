package ru.kpfu.itis.kevlinsky.citiesbots.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kpfu.itis.kevlinsky.citiesbots.models.DiscordChat;

import java.util.Optional;

@Repository
public interface DiscordChatRepository extends JpaRepository<DiscordChat, Integer> {
    DiscordChat findByChat(String chat);
}
