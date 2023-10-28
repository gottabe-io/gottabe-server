package io.gottabe.commons.services;

import io.gottabe.commons.entities.BaseOwner;
import io.gottabe.commons.entities.User;
import io.gottabe.commons.entities.UserPrivacyOptions;
import io.gottabe.commons.enums.IdHash;
import io.gottabe.commons.exceptions.InvalidRequestException;
import io.gottabe.commons.exceptions.ResourceNotFoundException;
import io.gottabe.commons.mapper.CurrentUserMapper;
import io.gottabe.commons.mapper.UserMapper;
import io.gottabe.commons.repositories.UserRepository;
import io.gottabe.commons.store.FileStore;
import io.gottabe.commons.store.ObjectSummary;
import io.gottabe.commons.util.IOUtils;
import io.gottabe.commons.util.Messages;
import io.gottabe.commons.vo.CurrentUserVO;
import io.gottabe.commons.vo.PasswordVO;
import io.gottabe.commons.vo.UserVO;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService extends AbstractCrudService<User, Long> {

    @Value("${gottabeio.config.salty.activation}")
    private String activationSalty;

    @Value("${gottabeio.config.salty.recover}")
    private String recoverSalty;

    @Value("${gottabeio.config.autoActivate}")
    private Boolean autoActivateUser;

    @Autowired
    private EmailService emailService;

    @Autowired
    private Messages messages;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder2;

    @Autowired
    private FileStore fileStore;

    @Autowired
    public UserService(UserRepository repository) {
        super(repository);
    }

    protected UserRepository getRepository() {
        return (UserRepository) this.repository;
    }

    public Optional<String> currentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.isAuthenticated()) {
            return Optional.of(auth.getName());
        }
        return Optional.empty();
    }

    public CurrentUserVO currentUserVo() {
        var user = currentUsername()
                .map(id -> getRepository().findByEmail(id).orElseThrow(ResourceNotFoundException::new)).orElse(null);
        var vo = CurrentUserMapper.INSTANCE.userToVO(user);
        Optional<ObjectSummary> avatar = getAvatar();
        avatar.ifPresent(avt -> vo.setImage("/api/user/" + IdHash.USER.hash(user.getId()) + "/avatar"));
        return vo;
    }

    public User currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            return getRepository().findByEmail(auth.getName())
                    .orElseThrow(ResourceNotFoundException::new);
        }
        return null;
    }

    public void createNew(UserVO userVo) throws Exception {
        User user = UserMapper.INSTANCE.voToUser(userVo);
        if (!StringUtils.hasText(userVo.getPassword())
                || !hasMinimumRequirements(userVo.getPassword()))
            throw new InvalidRequestException("user.password.requirements");
        if (!Objects.equals(userVo.getPassword(), userVo.getConfirmPassword()))
            throw new InvalidRequestException("user.password.dont_match");
        user.setPassword(passwordEncoder2.encode(userVo.getPassword()));
        user.setPrivacyOptions(new UserPrivacyOptions());
        if (autoActivateUser) {
            user.setActivationCode(null);
            user.setActivationExpires(new Date());
        } else {
            generateActivationCode(user);
        }
        user = this.save(user);
        if (!autoActivateUser) {
            emailService.sendMailUser(UserMapper.INSTANCE.userToVO(user), messages.getString("user.activation.subject"), "activation");
        }
    }

    private boolean hasMinimumRequirements(String password) {
        boolean hasUppercase = false;
        boolean hasLowercase = false;
        boolean hasNumber = false;
        boolean hasSpecial = false;
        for (int i = 0; i < password.length(); i++) {
            char ch = password.charAt(i);
            hasUppercase = hasUppercase || Character.isUpperCase(ch);
            hasLowercase = hasLowercase || Character.isLowerCase(ch);
            hasNumber = hasNumber || Character.isDigit(ch);
            hasSpecial = hasSpecial || (!Character.isAlphabetic(ch) && !Character.isDigit(ch));
        }
        return hasUppercase && hasLowercase && hasNumber && hasSpecial && password.length() >= 8;
    }

    private void generateActivationCode(User user) {
        String code = generateCode(user, activationSalty);
        user.setActivationExpires(Date.from(Instant.now().plus(7, ChronoUnit.DAYS)));
        user.setActivationCode(code);
    }

    private String generateCode(User user, String salty) {
        MessageDigest sha256Digest = DigestUtils.getSha256Digest();
        sha256Digest.update(user.getEmail().getBytes());
        sha256Digest.update(salty.getBytes());
        sha256Digest.update(IOUtils.longToBytes(System.currentTimeMillis()));
        byte[] bs = sha256Digest.digest();
        return UUID.nameUUIDFromBytes(bs).toString();
    }

    public void resendActivationCode(String email) throws Exception {
        User user = getRepository().findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("E-Mail not found in the database."));
        generateActivationCode(user);
        repository.save(user);
        emailService.sendMailUser(UserMapper.INSTANCE.userToVO(user), messages.getString("user.activation.subject"), "activation");
    }

    public void activate(String activationCode) throws Exception {
        User user = getRepository().findByActivationCode(activationCode).orElseThrow(() -> new ResourceNotFoundException("Activation code not found."));
        if (user.getActivationExpires().before(Date.from(Instant.now()))) {
            throw new InvalidRequestException("user.activation.expired");
        }
        user.setActivationCode(null);
        repository.save(user);
        emailService.sendMailUser(UserMapper.INSTANCE.userToVO(user), messages.getString("user.activation_success.subject"), "activation_success");
    }

    private void generateRecoverCode(User user) {
        String code = generateCode(user, recoverSalty);
        user.setRecoveryExpires(Date.from(Instant.now().plus(7, ChronoUnit.DAYS)));
        user.setRecoveryCode(code);
    }

    public void generateRecover(String email) throws Exception {
        User user = getRepository().findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("E-Mail not found in the database."));
        generateRecoverCode(user);
        save(user);
        emailService.sendMailUser(UserMapper.INSTANCE.userToVO(user), messages.getString("user.recovery.subject"), "recovery");
    }

    public void recover(String recoverCode, String password, String passwordConfirmation) throws Exception {
        User user = getRepository().findByRecoveryCode(recoverCode).orElseThrow(() -> new ResourceNotFoundException("Recovery code not found."));
        if (!password.equals(passwordConfirmation)) {
            throw new InvalidRequestException("user.password.dont_match");
        }
        if (user.getRecoveryExpires().before(Date.from(Instant.now()))) {
            throw new InvalidRequestException("user.recover.expired");
        }
        user.setRecoveryCode(null);
        user.setPassword(passwordEncoder2.encode(password));
        save(user);
        emailService.sendMailUser(UserMapper.INSTANCE.userToVO(user), messages.getString("user.recovery_success.subject"), "recovery_success");
    }

    public void saveAvatar(InputStream inputStream) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        inputStream.transferTo(bout);

        BufferedImage image = ImageIO.read(new ByteArrayInputStream(bout.toByteArray()));

        if (image.getWidth() > image.getHeight() * 1.5 ||
                image.getWidth() * 1.5 < image.getHeight())
            throw new InvalidRequestException(messages.getString("user.photo.invalidSize"));

        User user = currentUser();

        bout = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", bout);

        String id = IdHash.USER.hash(user.getId());

        fileStore.putFile("user"+id, "avatar_" + id + ".png", new ByteArrayInputStream(bout.toByteArray()));
    }

    public Optional<ObjectSummary> getAvatar() {
        User user = currentUser();
        String id = IdHash.USER.hash(user.getId());
        return fileStore.getObject("user"+id, "avatar_" + id + ".png");
    }

    public Optional<ObjectSummary> getAvatar(String id) {
        return fileStore.getObject("user"+id, "avatar_" + id + ".png");
    }

    public User findByNickname(String nickname) {
        return getRepository().findByNickname(nickname).orElseThrow(ResourceNotFoundException::new);
    }

    public void updatePrivacy(User user, UserPrivacyOptions privacyOptionsNew) {
        UserPrivacyOptions privacyOptions = user.getPrivacyOptions();
        if (privacyOptions == null) {
            privacyOptions = new UserPrivacyOptions();
            privacyOptions.setUser(user);
            user.setPrivacyOptions(privacyOptions);
        }
        privacyOptions.setShowEmail(privacyOptionsNew.isShowEmail());
        privacyOptions.setShowTwitter(privacyOptionsNew.isShowTwitter());
        privacyOptions.setShowGithub(privacyOptionsNew.isShowGithub());
        privacyOptions.setShowName(privacyOptionsNew.isShowName());
        save(user);
    }

    public void updateData(User user, UserVO userVo) {
        if (getRepository().existsByEmailAndIdNot(userVo.getEmail(), user.getId()))
            throw new InvalidRequestException("user.email.already_exists");
        if (getRepository().existsByNicknameAndIdNot(userVo.getNickname(), user.getId()))
            throw new InvalidRequestException("user.nickname.already_exists");
        user.setEmail(userVo.getEmail());
        user.setName(userVo.getName());
        user.setLastName(userVo.getLastName());
        user.setNickname(userVo.getNickname());
        user.setGithubAccount(userVo.getGithubAccount());
        user.setTwitterAccount(userVo.getTwitterAccount());
        user.setDescription(userVo.getDescription());
        save(user);
    }

    public void updatePassword(User user, PasswordVO passwordVo) throws Exception {
        if (!passwordVo.getPassword().equals(passwordVo.getPasswordConfirmation())) {
            throw new InvalidRequestException("user.password.dont_match");
        }
        user.setPassword(passwordEncoder2.encode(passwordVo.getPassword()));
        save(user);
        emailService.sendMailUser(UserMapper.INSTANCE.userToVO(user), messages.getString("user.recovery_success.subject"), "recovery_success");
    }

    public Optional<User> findByEmail(String email) {
        return getRepository().findByEmail(email);
    }

    public BaseOwner findOwnerByNickname(String nickname) {
        return getRepository().findOwnerByNickname(nickname).orElseThrow(ResourceNotFoundException::new);
    }
}
