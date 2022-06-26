package io.gottabe.commons.mapper;

import io.gottabe.commons.entities.*;
import io.gottabe.commons.vo.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ReviewMapper {

    ReviewMapper INSTANCE = Mappers.getMapper(ReviewMapper.class);

    @Mapping(source = "user", target = "user", qualifiedByName = "userToVo")
    ReviewVO reviewToVO(PackageReleaseReview entity);

    @Named("userToVo")
    default AnyUserVO ownerToVo(User user) {
        return AnyUserMapper.INSTANCE.userToVoPrivacy(user);
    }

    PackageReleaseReview voToEntity(NewReviewVO reviewVo);

}
