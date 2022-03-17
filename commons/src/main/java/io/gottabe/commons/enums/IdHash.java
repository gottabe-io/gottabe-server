package io.gottabe.commons.enums;

import io.gottabe.commons.entities.BaseOwner;
import io.gottabe.commons.entities.User;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

public enum IdHash {

    USER,
    ORGANIZATION;

    public final String hash(Long id) {
        return Hex.encodeHexString(DigestUtils.sha1(this.name() + "_" + id));
    }

    public static final String hash(BaseOwner owner) {
        IdHash type = owner instanceof User ? USER : ORGANIZATION;
        return Hex.encodeHexString(DigestUtils.sha1(type.name() + "_" + owner.getId()));
    }

}
