package pl.palubiak.dawid.newsler.businesclinet.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import pl.palubiak.dawid.newsler.user.model.User;
import pl.palubiak.dawid.newsler.utils.DBModel;

import javax.persistence.*;
import javax.validation.Constraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.executable.ValidateOnExecution;

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
    private long id;

    @NotBlank
    @Column(name = "NAME", nullable = false, columnDefinition = "VARCHAR(255)")
    private String name;

    @NotBlank
    @Column(name = "LAST_NAME", nullable = false, columnDefinition = "VARCHAR(255)")
    private String lastName;

    @NotBlank
    @Email(regexp = "[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}")
    @Column(nullable = false, name = "EMAIL", unique = true, columnDefinition = "VARCHAR(255)")
    private String email;

    @Column(name = "IS_ACTIVE", columnDefinition = "BIT")
    private boolean isActive;

    @Column(name = "ACTIVE_NEWSLETTERS", columnDefinition = "BIT")
    private boolean activeNewsLetters;

    @Column(name = "ACTIVE_PARTNERSHIP_OFFERS", columnDefinition = "BIT")
    private boolean activePartnershipOffers;

    @ManyToOne
    @JsonIgnore
    private User user;
}
