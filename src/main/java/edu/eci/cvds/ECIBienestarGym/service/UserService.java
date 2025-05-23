package edu.eci.cvds.ECIBienestarGym.service;

import edu.eci.cvds.ECIBienestarGym.dto.UserDTO;
import edu.eci.cvds.ECIBienestarGym.enums.Role;
import edu.eci.cvds.ECIBienestarGym.model.User;
import edu.eci.cvds.ECIBienestarGym.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getUsersByName(String name) {
        return userRepository.findByName(name);
    }

    public Optional<User> getUsersByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> getUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }

    public List<User> getUsersByRegistrationDate(LocalDate registrationDate) {return userRepository.findByRegistrationDate(registrationDate);}

    public User getUsersById(String id) {return userRepository.findById(id).orElse(null);}

    public User createUser(UserDTO userDTO) {
        if (userDTO.getId() == null || userDTO.getId().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        if (userDTO.getName() == null || userDTO.getName().isEmpty()) {
            throw new IllegalArgumentException("User name cannot be null or empty");
        }
        if (userDTO.getEmail() == null || userDTO.getEmail().isEmpty()) {
            throw new IllegalArgumentException("User email cannot be null or empty");
        }
        if (userDTO.getRole() == null) {
            throw new IllegalArgumentException("User role cannot be null");
        }

        User user = new User();
        user.setId(userDTO.getId());
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setRole(userDTO.getRole());

        return userRepository.save(user);
    }

    public User updateUser(String id, UserDTO userDTO) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("No se encontró el usuario"));
        if(userDTO.getName() != null) user.setName(userDTO.getName());
        if(userDTO.getEmail() != null && !user.getEmail().equals(userDTO.getEmail())) {
            if(userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
                throw new IllegalArgumentException("El usuario ya tiene ese email");
            }
            user.setEmail(userDTO.getEmail());
        }
        if(userDTO.getPassword() != null) user.setPassword(userDTO.getPassword());

        return userRepository.save(user);
    }

    public void deleteUser(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("El Id no puede ser nulo o vacío");
        }

        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con el id: " + id));

        userRepository.deleteById(id);
    }
}

