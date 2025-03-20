package grupo11.bcf_store.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import grupo11.bcf_store.model.User;
import grupo11.bcf_store.repository.UserRepository;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;

    /**
     * Returns always the same user for simplicity before adding authentication
     * @return User
     */
    public User getLoggedUser() {
        return userRepository.findAll().get(0);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

}

