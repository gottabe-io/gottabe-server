package io.gottabe.commons.entities;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "tbs_user")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class User extends BaseOwner {

    @Column(length = 120, nullable = false)
    private String lastName;

    @Column(length = 200, nullable = false)
    private String password;

    @Column(length = 200, nullable = true)
    private String activationCode;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = true)
    private Date activationExpires;

    @Column(length = 200, nullable = true)
    private String recoveryCode;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = true)
    private Date recoveryExpires;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dtLocked;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    private UserPrivacyOptions privacyOptions;

}
