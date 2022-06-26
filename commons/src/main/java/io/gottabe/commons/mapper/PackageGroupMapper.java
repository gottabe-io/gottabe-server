package io.gottabe.commons.mapper;

import io.gottabe.commons.entities.BaseOwner;
import io.gottabe.commons.entities.PackageGroup;
import io.gottabe.commons.enums.IdHash;
import io.gottabe.commons.vo.AnyUserVO;
import io.gottabe.commons.vo.OwnerVO;
import io.gottabe.commons.vo.PackageGroupVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PackageGroupMapper {

    PackageGroupMapper INSTANCE = Mappers.getMapper(PackageGroupMapper.class);

    @Mapping(source = "owner", target = "owner", qualifiedByName = "ownerToVo")
    PackageGroupVO groupToVO(PackageGroup entity);

    @Named("ownerToVo")
    default OwnerVO ownerToVo(BaseOwner owner) {
        return AnyUserMapper.INSTANCE.ownerToVo(owner);
    }

}
