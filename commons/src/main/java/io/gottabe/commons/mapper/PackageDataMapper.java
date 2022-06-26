package io.gottabe.commons.mapper;

import io.gottabe.commons.entities.PackageData;
import io.gottabe.commons.entities.PackageGroup;
import io.gottabe.commons.enums.IdHash;
import io.gottabe.commons.vo.PackageDataVO;
import io.gottabe.commons.vo.PackageGroupVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PackageDataMapper {

    PackageDataMapper INSTANCE = Mappers.getMapper(PackageDataMapper.class);

    @Mapping(target = "releases", ignore = true)
    @Mapping(source = "group", target = "group", qualifiedByName = "groupToVo")
    PackageDataVO packageToVO(PackageData entity);

    @Mapping(source = "group", target = "group", qualifiedByName = "groupToVo")
    PackageDataVO packageToVOWithReleases(PackageData entity);

    @Named("groupToVo")
    default PackageGroupVO groupToVo(PackageGroup group) {
        return PackageGroupMapper.INSTANCE.groupToVO(group);
    }

}
