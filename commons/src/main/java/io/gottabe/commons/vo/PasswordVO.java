package io.gottabe.commons.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordVO {

    @NotEmpty
    private String password;

    @NotEmpty
    private String passwordConfirmation;
}
