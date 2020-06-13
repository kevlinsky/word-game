package ru.kpfu.itis.kevlinsky.citiesbots.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.kpfu.itis.kevlinsky.citiesbots.telegram.TelegramBot;

@Component
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ExecuteClass {
    private TelegramBot telegramBot;
}
