package io.gottabe.commons.mapper;

import io.gottabe.commons.entities.PackageData;
import io.gottabe.commons.vo.PackageDataVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PackageDataMapper {

    PackageDataMapper INSTANCE = Mappers.getMapper(PackageDataMapper.class);

    PackageDataVO packageToVO(PackageData entity);

}
