package io.gottabe.userservices.api;

import io.gottabe.commons.entities.User;
import io.gottabe.commons.entities.UserPrivacyOptions;
import io.gottabe.commons.exceptions.AccessDeniedException;
import io.gottabe.commons.exceptions.InvalidRequestException;
import io.gottabe.commons.exceptions.ResourceNotFoundException;
import io.gottabe.commons.mapper.UserMapper;
import io.gottabe.commons.mapper.UserPrivacyMapper;
import io.gottabe.commons.services.UserService;
import io.gottabe.userservices.services.ManagedTokenService;
import io.gottabe.commons.store.ObjectSummary;
import io.gottabe.commons.util.Messages;
import io.gottabe.commons.vo.*;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("api/user")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private Messages messages;
	
	@Autowired
	private LocaleResolver localeResolver;

	@Autowired
	private ManagedTokenService managedTokenService;

	@PreAuthorize("!hasAuthority('ROLE_USER')")
	@PostMapping
	public ResponseEntity<Void> createNew(@Valid @RequestBody UserVO user) throws Exception {
		userService.createNew(user);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PreAuthorize("!hasAuthority('ROLE_USER')")
	@PostMapping("resend-activation")
	public ResponseEntity<Void> resendActivationCode(@RequestParam(value = "email", required = true) String email, HttpServletRequest request) throws Exception {
		userService.resendActivationCode(email);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@PreAuthorize("hasAuthority('ROLE_USER')")
	@GetMapping("/current")
	public ResponseEntity<CurrentUserVO> currentUser() {
		CurrentUserVO vo = userService.currentUserVo();
		return ResponseEntity.ok(vo);
	}

	@GetMapping("/{id}/avatar")
	public ResponseEntity<Resource> avatar(@PathVariable("id") String id) {
		return getAvatar(userService.getAvatar(id));
	}

	@PreAuthorize("!hasAuthority('ROLE_USER')")
	@PostMapping("/activate/{activationCode}")
	public ResponseEntity<Void> activate(@PathVariable("activationCode") String activationCode) throws Exception {
		userService.activate(activationCode);
		return ResponseEntity.ok().build();
	}

	@PreAuthorize("!hasAuthority('ROLE_USER')")
	@PostMapping("/recover/{email}")
	public ResponseEntity<Void> generateRecover(@PathVariable("email") String email) throws Exception {
		userService.generateRecover(email);
		return ResponseEntity.ok().build();
	}

	@PreAuthorize("!hasAuthority('ROLE_USER')")
	@PostMapping("/recover")
	public ResponseEntity<Void> recover(
			@RequestParam("recoverCode") String recoverCode,
			@RequestParam("password") String password,
			@RequestParam("passwordConfirmation") String passwordConfirmation) throws Exception {
		userService.recover(recoverCode, password, passwordConfirmation);
		return ResponseEntity.ok().build();
	}

	@PreAuthorize("hasAuthority('ROLE_USER')")
	@RequestMapping(method = RequestMethod.POST, value = "/current/avatar")
	public ResponseEntity<Void> updatePhoto(@RequestParam("file") MultipartFile file) throws IOException {
		if (file == null) {
			throw new InvalidRequestException("You must select the a file for uploading");
		}

		InputStream inputStream = file.getInputStream();
		long size = file.getSize();
		if (size > 5242880)
			throw new InvalidRequestException(messages.format("user.photo.maxSize", size));

		userService.saveAvatar(inputStream);

		return ResponseEntity.ok().build();
	}

	@Autowired
	private WebRequest request;

	@PreAuthorize("hasAuthority('ROLE_USER')")
	@RequestMapping(path = "/current/avatar", method = RequestMethod.GET)
	public ResponseEntity<Resource> getAvatar() throws IOException {

		Optional<ObjectSummary> opAvatar = userService.getAvatar();

		return getAvatar(opAvatar);
	}

	private ResponseEntity<Resource> getAvatar(Optional<ObjectSummary> opAvatar) {
		ObjectSummary avatar = opAvatar.orElseThrow(ResourceNotFoundException::new);

		if (request.checkNotModified(avatar.getLastModified().getTime()) || request.checkNotModified(avatar.getETag())) {
			return ResponseEntity.status(HttpStatus.NOT_MODIFIED)
					.cacheControl(CacheControl.maxAge(0, TimeUnit.SECONDS).cachePrivate().mustRevalidate())
					.build();
		}

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + avatar.getFilename());

		return ResponseEntity.ok()
				.headers(headers)
				.contentLength(avatar.getSize())
				.contentType(MediaType.IMAGE_PNG)
				.cacheControl(CacheControl.maxAge(0, TimeUnit.SECONDS).cachePrivate().mustRevalidate())
				.lastModified(avatar.getLastModified().getTime())
				.eTag(avatar.getETag())
				.body(avatar.getResource());
	}

	@PreAuthorize("hasAuthority('ROLE_USER')")
	@GetMapping("/current/data")
	public ResponseEntity<UserVO> currentUserProfile() {
		User user = userService.findByEmail(userService.currentUsername().orElseThrow(AccessDeniedException::new)).orElseThrow(ResourceNotFoundException::new);
		UserVO vo = UserMapper.INSTANCE.userToVO(user);
		vo.setPassword(null);
		vo.setActivationCode(null);
		vo.setRecoveryCode(null);
		return ResponseEntity.ok(vo);
	}

	@PreAuthorize("hasAuthority('ROLE_USER')")
	@GetMapping("/current/privacy")
	public ResponseEntity<UserPrivacyVO> currentUserPrivacy() {
		User user = userService.currentUser();
		UserPrivacyVO vo = UserPrivacyMapper.INSTANCE.entityToVO(user.getPrivacyOptions());
		return ResponseEntity.ok(vo);
	}

	@PreAuthorize("hasAuthority('ROLE_USER')")
	@PatchMapping("/current/data")
	public ResponseEntity<Void> updateCurrentUserProfile(@Valid @RequestBody UserVO userVo) {
		User user = userService.currentUser();
		userService.updateData(user, userVo);
		return ResponseEntity.ok().build();
	}

	@PreAuthorize("hasAuthority('ROLE_USER')")
	@PatchMapping("/current/privacy")
	@Transactional
	public ResponseEntity<Void> updateCurrentUserPrivacy(@Valid @RequestBody UserPrivacyVO userPrivacyVo) {
		User user = userService.currentUser();
		UserPrivacyOptions privacyOptions = UserPrivacyMapper.INSTANCE.voToUser(userPrivacyVo);
		userService.updatePrivacy(user, privacyOptions);
		return ResponseEntity.ok().build();
	}

	@PreAuthorize("hasAuthority('ROLE_USER')")
	@PatchMapping("/current/password")
	public ResponseEntity<Void> updatePassword(@Valid @RequestBody PasswordVO passwordVo) throws Exception {
		User user = userService.currentUser();
		userService.updatePassword(user, passwordVo);
		return ResponseEntity.ok().build();
	}

	@PreAuthorize("hasAuthority('ROLE_USER')")
	@PostMapping("/current/tokens")
	public ResponseEntity<Void> createToken() throws IOException {
		User user = userService.currentUser();
		managedTokenService.createToken(user);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PreAuthorize("hasAuthority('ROLE_USER')")
	@GetMapping("/current/tokens")
	public ResponseEntity<List<ManagedTokenVO>> listTokens() {
		User user = userService.currentUser();
		return ResponseEntity.ok(managedTokenService.findByUser(user));
	}

	@PreAuthorize("hasAuthority('ROLE_USER')")
	@DeleteMapping("/current/tokens")
	public ResponseEntity<Void> removeToken(@RequestParam(value = "tokenId", required = true) String tokenId) {
		User user = userService.currentUser();
		managedTokenService.findByUser(user).stream()
				.filter(token -> token.getToken().equals(tokenId))
				.findAny()
				.orElseThrow(() -> new InvalidRequestException("user.token.not_found"));
		managedTokenService.revokeToken(tokenId);
		return ResponseEntity.ok().build();
	}

}
