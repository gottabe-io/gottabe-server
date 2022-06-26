package io.gottabe.commons.mapper;

import io.gottabe.commons.entities.BaseOwner;
import io.gottabe.commons.entities.OrganizationUser;
import io.gottabe.commons.entities.User;
import io.gottabe.commons.entities.UserPrivacyOptions;
import io.gottabe.commons.enums.IdHash;
import io.gottabe.commons.vo.AnyUserVO;
import io.gottabe.commons.vo.OwnerVO;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AnyUserMapper {

    AnyUserMapper INSTANCE = Mappers.getMapper(AnyUserMapper.class);

    @InheritInverseConfiguration
    @Mapping(target = "id", ignore = true)
    User voToUser(AnyUserVO vo);

    @Mapping(source = "id", target = "id", qualifiedByName = "idToHash")
    AnyUserVO userToVo(User entity);

    @Mapping(source = "id", target = "id", qualifiedByName = "idToHash")
    OwnerVO ownerToVo(BaseOwner owner);

    @Named("idToHash")
    default String idToHash(Long id) {
        return IdHash.USER.hash(id);
    }

    default AnyUserVO userToVoPrivacy(User entity) {
        AnyUserVO vo = userToVo(entity);
        UserPrivacyOptions privacyOptions = entity.getPrivacyOptions();
        if (privacyOptions != null) {
            if (!privacyOptions.isShowEmail()) vo.setEmail(null);
            if (!privacyOptions.isShowGithub()) vo.setGithubAccount(null);
            if (!privacyOptions.isShowTwitter()) vo.setTwitterAccount(null);
            if (!privacyOptions.isShowName()) {
                vo.setName(null);
                vo.setLastName(null);
            }
        }
        return vo;
    }

    default AnyUserVO userToVoPrivacy(OrganizationUser entity) {
        AnyUserVO vo = userToVoPrivacy(entity.getUser());
        vo.setRole(entity.getRole());
        return vo;
    }

}
