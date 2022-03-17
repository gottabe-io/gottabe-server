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
public class PackageReleaseVO {

    private String version;

    private Date releaseDate;

    private String description;

    private String sourceUrl;

    private String issuesUrl;

    private String documentationUrl;

}
