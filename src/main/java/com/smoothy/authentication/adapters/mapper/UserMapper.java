package com.smoothy.authentication.adapters.mapper;

import com.smoothy.authentication.adapters.inbound.dtos.out.ResponseRegisterDTO;
import com.smoothy.authentication.adapters.outbound.entities.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserEntity EntityToResponse (ResponseRegisterDTO registerDTO);
    ResponseRegisterDTO EntityToResponse (UserEntity userEntity);

}
