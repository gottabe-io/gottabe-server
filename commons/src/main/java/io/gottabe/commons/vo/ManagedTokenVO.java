package io.gottabe.commons.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ManagedTokenVO {

    private String token;

    private Date expireDate;

    private Date creationDate;

}
