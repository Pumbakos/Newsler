package pl.palubiak.dawid.newsler.businesclinet.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import pl.palubiak.dawid.newsler.user.model.User;
import pl.palubiak.dawid.newsler.utils.DBModel;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "CLIENTS")
public class BusinessClient extends DBModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", columnDefinition = "NUMBER(10)")
    @ToString.Exclude
    private Long id;

    @NotBlank
    @Column(name = "NAME", nullable = false, columnDefinition = "VARCHAR(255)")
    private String name;

    @Column(name = "LAST_NAME", columnDefinition = "VARCHAR(255)")
    private String lastName;

    @NotBlank
    @Email(regexp = "[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}")
    @Column(name = "EMAIL", nullable = false, unique = true, columnDefinition = "VARCHAR(255)")
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "EMAIL_TYPE", columnDefinition = "VARCHAR(20)")
    private EmailType emailType;

    @ManyToOne
    @JsonIgnore
    private User user;
}
