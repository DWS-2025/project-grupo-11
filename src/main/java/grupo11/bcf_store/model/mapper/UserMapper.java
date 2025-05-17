package grupo11.bcf_store.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import grupo11.bcf_store.model.User;
import grupo11.bcf_store.model.dto.UserDTO;

import java.util.List;

@Mapper(componentModel = "spring", uses = {OrderMapper.class, CartMapper.class})
public interface UserMapper {

    @Mapping(source = "cart", target = "cart")
    @Mapping(source = "orders", target = "orders")
    UserDTO toDTO(User user);

    List<UserDTO> toDTOs(List<User> users);

    @Mapping(target = "cart", ignore = true)
    @Mapping(target = "orders", ignore = true)
    User toDomain(UserDTO userDTO);
}
