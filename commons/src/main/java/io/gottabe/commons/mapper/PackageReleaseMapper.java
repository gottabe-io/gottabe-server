package io.gottabe.commons.mapper;

import io.gottabe.commons.entities.PackageRelease;
import io.gottabe.commons.vo.PackageReleaseVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PackageReleaseMapper {

    PackageReleaseMapper INSTANCE = Mappers.getMapper(PackageReleaseMapper.class);

    PackageReleaseVO releaseToVO(PackageRelease entity);

}
