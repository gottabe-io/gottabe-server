package io.gottabe.commons.mapper;

import io.gottabe.commons.entities.Organization;
import io.gottabe.commons.enums.IdHash;
import io.gottabe.commons.vo.OwnerVO;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OrganizationMapper {

    OrganizationMapper INSTANCE = Mappers.getMapper(OrganizationMapper.class);

    @InheritInverseConfiguration
    @Mapping(target = "id", ignore = true)
    Organization voToOrganization(OwnerVO vo);

    @Mapping(source = "id", target = "id", qualifiedByName = "idToHash")
    OwnerVO organizationToVo(Organization entity);

    @Named("idToHash")
    default String idToHash(Long id) {
        return IdHash.ORGANIZATION.hash(id);
    }

}
