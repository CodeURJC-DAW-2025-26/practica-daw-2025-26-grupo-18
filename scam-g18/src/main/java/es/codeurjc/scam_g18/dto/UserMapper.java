package es.codeurjc.scam_g18.dto;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;

import es.codeurjc.scam_g18.model.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface UserMapper {
    UserDTO toDTO(User user);
    User toDomain(UserDTO userDTO);
    List<UserDTO> toDTOs(Collection<User> users);
}
