package io.gottabe.commons.vo;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CurrentUserVO {

	private String name;

	private String lastName;

	private String email;

	private String image;

}
