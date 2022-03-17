package io.gottabe.commons.mapper;

import io.gottabe.commons.entities.PackageGroup;
import io.gottabe.commons.vo.PackageGroupVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PackageGroupMapper {

    PackageGroupMapper INSTANCE = Mappers.getMapper(PackageGroupMapper.class);

    PackageGroupVO groupToVO(PackageGroup entity);

}
