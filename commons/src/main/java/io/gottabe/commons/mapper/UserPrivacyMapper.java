package io.gottabe.commons.mapper;

import io.gottabe.commons.entities.UserPrivacyOptions;
import io.gottabe.commons.vo.UserPrivacyVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserPrivacyMapper {

    UserPrivacyMapper INSTANCE = Mappers.getMapper(UserPrivacyMapper.class);

    UserPrivacyOptions voToUser(UserPrivacyVO vo);

    UserPrivacyVO entityToVO(UserPrivacyOptions entity);

}
