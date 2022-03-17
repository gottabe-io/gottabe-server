package io.gottabe.commons.services;

import io.gottabe.commons.entities.Organization;
import io.gottabe.commons.entities.OrganizationUser;
import io.gottabe.commons.entities.User;
import io.gottabe.commons.enums.OrgUserRole;
import io.gottabe.commons.exceptions.AccessDeniedException;
import io.gottabe.commons.exceptions.InvalidRequestException;
import io.gottabe.commons.exceptions.ResourceNotFoundException;
import io.gottabe.commons.mapper.UserMapper;
import io.gottabe.commons.repositories.OrganizationRepository;
import io.gottabe.commons.repositories.OrganizationUserRepository;
import io.gottabe.commons.util.Messages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

@Service
public class OrganizationService extends AbstractCrudService<Organization, Long> {

    private UserService userService;
    private OrganizationUserRepository organizationUserRepository;
    private EmailService emailService;
    private Messages messages;

    @Autowired
    public OrganizationService(OrganizationRepository repository, UserService userService, OrganizationUserRepository organizationUserRepository, EmailService emailService, Messages messages) {
        super(repository);
        this.userService = userService;
        this.organizationUserRepository = organizationUserRepository;
        this.emailService = emailService;
        this.messages = messages;
    }

    protected OrganizationRepository getRepository() {
        return (OrganizationRepository) this.repository;
    }

    public Organization findByNickname(String nickname) {
        return getRepository().findByNickname(nickname).orElseThrow(ResourceNotFoundException::new);
    }

    public void addUser(String orgNickname, String userNickname, OrgUserRole role) throws Exception {
        Organization org = findByNickname(orgNickname);
        User currentUser = userService.currentUser();
        if (!organizationUserRepository.existsByUserAndOrganizationAndRoleIn(currentUser, org, Arrays.asList(OrgUserRole.OWNER, OrgUserRole.ADMIN)))
                throw new AccessDeniedException();
        if (role == OrgUserRole.OWNER) {
            throw new InvalidRequestException("organization.user.invalid.role");
        }
        User user = userService.findByNickname(userNickname);
        OrganizationUser orgUser = organizationUserRepository.findByUserAndOrganization(user, org)
                .map(ou -> checkUser(ou, role))
                .orElseGet(() -> newOrgUser(user, org, role));
        organizationUserRepository.save(orgUser);
        emailService.sendMailUser(UserMapper.INSTANCE.userToVO(user),
                messages.getString("user.invite.subject"), "invite");
    }

    private OrganizationUser newOrgUser(User user, Organization org, OrgUserRole role) {
        return OrganizationUser.builder()
                .user(user)
                .organization(org)
                .role(role)
                .active(false)
                .build();
    }

    OrganizationUser checkUser(OrganizationUser orgUser, OrgUserRole role) {
        if (orgUser.isActive()) {
            throw new InvalidRequestException("organization.user.already.exists");
        } else if (orgUser.getInviteDate().plus(7, ChronoUnit.DAYS).isAfter(LocalDateTime.now())) {
            throw new InvalidRequestException("organization.user.invite.active");
        }
        orgUser.setRole(role);
        orgUser.setInviteDate(LocalDateTime.now());
        return orgUser;
    }

    public List<OrganizationUser> findOrgsOfCurrentUser() {
        User user = userService.currentUser();
        return organizationUserRepository.findByUser(user);
    }
}
