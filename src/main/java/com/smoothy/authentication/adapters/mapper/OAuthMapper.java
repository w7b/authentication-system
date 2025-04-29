package com.smoothy.authentication.adapters.mapper;

import com.smoothy.authentication.adapters.inbound.dtos.out.ResponseOAuthUser;
import com.smoothy.authentication.infrastructure.security.oauth2.repository.iOAuthRepository;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OAuthMapper {

    OAuthMapper INSTANCE = Mappers.getMapper(OAuthMapper.class);

//    @Mapping(target = "provider", ignore = false)
    ResponseOAuthUser toResponse(iOAuthRepository user, String provider);

}
