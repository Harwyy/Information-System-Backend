package is.is_backend.service;

import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void notifyAllSubscribers() {
        messagingTemplate.convertAndSend("/topic/all", "Some table (created/updated/deleted)");
    }
}
