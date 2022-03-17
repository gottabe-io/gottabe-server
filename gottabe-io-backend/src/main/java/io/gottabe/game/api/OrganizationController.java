package io.gottabe.game.api;

import io.gottabe.commons.entities.Organization;
import io.gottabe.commons.mapper.AnyUserMapper;
import io.gottabe.commons.mapper.OrgUserMapper;
import io.gottabe.commons.mapper.OrganizationMapper;
import io.gottabe.commons.services.OrganizationService;
import io.gottabe.commons.vo.AnyUserVO;
import io.gottabe.commons.vo.OrgUserVO;
import io.gottabe.commons.vo.OwnerVO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/organization")
public class OrganizationController {

    private OrganizationService organizationService;

    @PreAuthorize("hasAuthority('SCOPE_write')")
    @PostMapping
    @Transactional
    public ResponseEntity<Void> createNew(@Valid @RequestBody OwnerVO orgVo) throws Exception {
        organizationService.save(OrganizationMapper.INSTANCE.voToOrganization(orgVo));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{nickname}")
    public ResponseEntity<OwnerVO> retrieve(@PathVariable("nickname") String nickname) throws Exception {
        OwnerVO ownerVo = OrganizationMapper.INSTANCE.organizationToVo(organizationService.findByNickname(nickname));
        return ResponseEntity.ok(ownerVo);
    }

    @GetMapping("/{nickname}/users")
    @Transactional(readOnly = true)
    public ResponseEntity<List<AnyUserVO>> retrieveUsers(@PathVariable("nickname") String nickname) throws Exception {
        Organization org = organizationService.findByNickname(nickname);
        List<AnyUserVO> users = org.getUsers().stream()
                .map(AnyUserMapper.INSTANCE::userToVoPrivacy)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @PreAuthorize("hasAuthority('SCOPE_write')")
    @PutMapping("/{nickname}/users")
    @Transactional
    public ResponseEntity<Void> addUser(@PathVariable("nickname") String nickname, @Valid @RequestBody AnyUserVO userVo) throws Exception {
        organizationService.addUser(nickname, userVo.getNickname(), userVo.getRole());
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('SCOPE_read')")
    @GetMapping("/mine")
    @Transactional
    public ResponseEntity<List<OrgUserVO>> getMyOrganizations() throws Exception {
        List<OrgUserVO> orgVos = organizationService.findOrgsOfCurrentUser().stream()
                .map(OrgUserMapper.INSTANCE::orgToVo)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orgVos);
    }

}
