package io.gottabe.commons.mapper;

import io.gottabe.commons.entities.User;
import io.gottabe.commons.vo.UserVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User voToUser(UserVO vo);

    UserVO userToVO(User entity);

}
