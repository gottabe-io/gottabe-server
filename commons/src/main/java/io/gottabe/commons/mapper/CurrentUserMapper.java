package io.gottabe.commons.mapper;

import io.gottabe.commons.entities.User;
import io.gottabe.commons.vo.CurrentUserVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CurrentUserMapper {

    CurrentUserMapper INSTANCE = Mappers.getMapper(CurrentUserMapper.class);

    CurrentUserVO userToVO(User entity);

}
