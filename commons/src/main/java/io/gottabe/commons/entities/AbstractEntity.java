package io.gottabe.commons.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractEntity implements Serializable {

    @Temporal(TemporalType.TIMESTAMP)
    protected Date createTime;

    @Temporal(TemporalType.TIMESTAMP)
    protected Date lastUpdated;

    @Temporal(TemporalType.TIMESTAMP)
    protected Date deleteTime;

}
