package vacancy_tracker.model.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "sessions")
@Getter
@Setter
@NoArgsConstructor
public class SessionEntity {

    @Id
    @Column(name = "chat_id")
    private Long chatId;

    @Column(name = "interceptor_command_key")
    private String interceptorCommandKey;

    public SessionEntity(long chatId) {
        this.chatId = chatId;
    }
}
