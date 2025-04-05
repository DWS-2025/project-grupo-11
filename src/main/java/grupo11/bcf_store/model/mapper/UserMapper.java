package grupo11.bcf_store.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import grupo11.bcf_store.model.User;
import grupo11.bcf_store.model.dto.UserDTO;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "cart", ignore = true)
    @Mapping(target = "orders", ignore = true)
    UserDTO toDTO(User user);

    List<UserDTO> toDTOs(List<User> users);

    @Mapping(target = "cart", ignore = true)
    @Mapping(target = "orders", ignore = true)
    User toDomain(UserDTO userDTO);
}
