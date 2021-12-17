package pl.palubiak.dawid.newsler.businesclinet.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pl.palubiak.dawid.newsler.user.model.User;
import pl.palubiak.dawid.newsler.utils.DBModel;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Entity
@Table(name = "CLIENTS")
public class BusinessClient extends DBModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false, unique = true, updatable = false, columnDefinition = "NUMBER(10)")
    @ToString.Exclude
    private Long id;

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

    @Column(name = "IS_ACTIVE", columnDefinition = "NUMBER(1)")
    private boolean isActive;

    @Column(name = "ACTIVE_NEWSLETTERS", columnDefinition = "NUMBER(1)")
    private boolean activeNewsLetters;

    @Column(name = "ACTIVE_PARTNERSHIP_OFFERS", columnDefinition = "NUMBER(1)")
    private boolean activePartnershipOffers;

    @ManyToOne
    @JsonIgnore
    private User user;
}
