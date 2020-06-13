package ru.kpfu.itis.kevlinsky.citiesbots.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kpfu.itis.kevlinsky.citiesbots.models.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {
}
