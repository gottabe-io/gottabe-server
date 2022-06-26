package io.gottabe.commons.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserVO {

	@Email
	@NotBlank
	private String email;

	@NotBlank
	private String name;

	@NotBlank
	private String lastName;

	@NotBlank
	private String nickname;
	
	private String password;

	private String confirmPassword;

	private String activationCode;

	private String recoveryCode;

	private String githubAccount;

	private String twitterAccount;

	private String description;

}
