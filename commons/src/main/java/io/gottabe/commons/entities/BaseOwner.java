package io.gottabe.commons.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "tbs_base_owner", indexes = {
        @Index(unique = true, columnList = "email", name = "uk_org_mail"),
        @Index(unique = true, columnList = "nickname", name = "uk_org_nick")
})
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseOwner extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(length = 60, nullable = false)
    private String name;

    @Column(length = 60, nullable = false)
    private String nickname;

    @Column(length = 100, nullable = false)
    private String email;

    @Column(length = 100)
    private String githubAccount;

    @Column(length = 100)
    private String twitterAccount;

    @Column(length = 1000)
    private String description;

}
