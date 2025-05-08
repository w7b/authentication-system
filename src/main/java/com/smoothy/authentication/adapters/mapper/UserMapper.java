package com.smoothy.authentication.adapters.mapper;

import com.smoothy.authentication.adapters.inbound.dtos.in.RequestRegisterDTO;
import com.smoothy.authentication.adapters.inbound.dtos.in.UserUpdateRequestDTO;
import com.smoothy.authentication.adapters.inbound.dtos.out.UserResponseDto;
import com.smoothy.authentication.adapters.outbound.entities.UserEntity;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "uuid", target = "id")
    UserResponseDto fromEntityToResponseDTO(UserEntity userEntity);

    UserEntity fromEntityToRequest (RequestRegisterDTO requestRegisterDTO);
    RequestRegisterDTO fromRequestToEntity (UserEntity entity);


    void userUpdate(UserUpdateRequestDTO request, @MappingTarget UserEntity entity);

}

//tipoDoRetorno nomeDoMetodo ( params1, params2 )