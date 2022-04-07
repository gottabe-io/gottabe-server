package io.gottabe.commons.entities;

import io.gottabe.commons.enums.OrgUserRole;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "tbs_user_privacy_ops", indexes = @Index(unique = true, name = "uk_privacy_user", columnList = "user_id"))
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class UserPrivacyOptions extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private User user;

    private boolean showEmail;

    private boolean showTwitter;

    private boolean showGithub;

    private boolean showName;

}