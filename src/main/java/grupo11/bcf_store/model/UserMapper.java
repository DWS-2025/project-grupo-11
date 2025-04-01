package grupo11.bcf_store.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

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
