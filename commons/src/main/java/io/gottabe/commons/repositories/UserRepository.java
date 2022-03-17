package io.gottabe.commons.repositories;

import io.gottabe.commons.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByActivationCode(String activationCode);

    Optional<User> findByRecoveryCode(String recoveryCode);

    Optional<User> findByNickname(String nickname);

    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByNicknameAndIdNot(String nickname, Long id);
}
