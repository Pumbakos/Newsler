package pl.newsler.components.user.event;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;

@Entity
@Getter
@Setter
@ToString
public class NLUserEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_nluser_event")
    @SequenceGenerator(name = "seq_nluser_event", sequenceName = "seq_nluser_event")
    private Long id;

    @Lob
    private byte[] content;
}
