package io.gottabe.commons.mapper;

import io.gottabe.commons.entities.OrganizationUser;
import io.gottabe.commons.entities.User;
import io.gottabe.commons.vo.AnyUserVO;
import io.gottabe.commons.vo.OrgUserVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OrgUserMapper {

    OrgUserMapper INSTANCE = Mappers.getMapper(OrgUserMapper.class);

    OrgUserVO orgToVo(OrganizationUser entity);

}
