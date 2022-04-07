package io.gottabe.commons.mapper;

import io.gottabe.commons.entities.PackageData;
import io.gottabe.commons.vo.PackageDataVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PackageDataMapper {

    PackageDataMapper INSTANCE = Mappers.getMapper(PackageDataMapper.class);

    @Mapping(target = "releases", ignore = true)
    PackageDataVO packageToVO(PackageData entity);

}
