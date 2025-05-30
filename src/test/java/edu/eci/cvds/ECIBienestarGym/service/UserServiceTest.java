package edu.eci.cvds.ECIBienestarGym.service;

import edu.eci.cvds.ECIBienestarGym.dto.UserDTO;
import edu.eci.cvds.ECIBienestarGym.enums.Role;
import edu.eci.cvds.ECIBienestarGym.exceptions.GYMException;
import edu.eci.cvds.ECIBienestarGym.model.User;
import edu.eci.cvds.ECIBienestarGym.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnSavedUserWhenDataIsValid() throws GYMException {
        UserDTO dto = new UserDTO();
        dto.setId("user1");
        dto.setName("Ana");
        dto.setEmail("ana@example.com");
        dto.setRole(Role.STUDENT);

        User expectedUser = new User();
        expectedUser.setId(dto.getId());
        expectedUser.setName(dto.getName());
        expectedUser.setEmail(dto.getEmail());
        expectedUser.setRole(dto.getRole());

        when(userRepository.save(any(User.class))).thenReturn(expectedUser);

        User result = userService.createUser(dto);

        assertEquals(dto.getId(), result.getId());
        assertEquals(dto.getName(), result.getName());
        assertEquals(dto.getEmail(), result.getEmail());
        assertEquals(dto.getRole(), result.getRole());
        verify(userRepository, times(1)).save(any(User.class));
    }



    @Test
    void shouldThrowExceptionWhenNameIsNullOrEmpty() {
        UserDTO dto = new UserDTO();
        dto.setId("user1");
        dto.setName("");
        dto.setEmail("test@example.com");
        dto.setRole(Role.STUDENT);
        GYMException exception = assertThrows(GYMException.class, () -> userService.createUser(dto));
        assertEquals(GYMException.USER_NOT_NULL, exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenEmailIsNullOrEmpty() {
        UserDTO dto = new UserDTO();
        dto.setId("user1");
        dto.setName("Test");
        dto.setEmail(null);
        dto.setRole(Role.STUDENT);
        GYMException exception = assertThrows(GYMException.class, () -> userService.createUser(dto));
        assertEquals(GYMException.USER_NOT_NULL, exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenRoleIsNull() {
        UserDTO dto = new UserDTO();
        dto.setId("user1");
        dto.setName("Test");
        dto.setEmail("test@example.com");
        dto.setRole(null);
        GYMException exception = assertThrows(GYMException.class, () -> userService.createUser(dto));
        assertEquals(GYMException.USER_NOT_NULL, exception.getMessage());
    }

    @Test
    void shouldReturnSavedUserWithValidData() throws GYMException {
        UserDTO userDTO = new UserDTO();
        userDTO.setId("user123");
        userDTO.setName("John Doe");
        userDTO.setEmail("johndoe@example.com");
        userDTO.setRole(Role.STUDENT);

        User mockUser = new User();
        mockUser.setId("user123");
        mockUser.setName("John Doe");
        mockUser.setEmail("johndoe@example.com");
        mockUser.setRole(Role.STUDENT);

        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        User createdUser = userService.createUser(userDTO);

        assertEquals(mockUser.getId(), createdUser.getId());
        assertEquals(mockUser.getName(), createdUser.getName());
        assertEquals(mockUser.getEmail(), createdUser.getEmail());
        assertEquals(mockUser.getRole(), createdUser.getRole());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void shouldReturnAllUsers() {
        List<User> mockUsers = Arrays.asList(new User(), new User());
        when(userRepository.findAll()).thenReturn(mockUsers);

        List<User> users = userService.getAllUsers();

        assertEquals(2, users.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void shouldReturnUserWhenIdExists() {
        String id = "user123";
        User mockUser = new User();
        when(userRepository.findById(id)).thenReturn(Optional.of(mockUser));

        User user = userService.getUsersById(id);

        assertEquals(mockUser, user);
        verify(userRepository, times(1)).findById(id);
    }

    @Test
    void shouldReturnUsersWithGivenRole() {
        Role role = Role.TRAINER;
        List<User> mockUsers = Arrays.asList(new User(), new User());
        when(userRepository.findByRole(role)).thenReturn(mockUsers);

        List<User> users = userService.getUsersByRole(role);

        assertEquals(2, users.size());
        verify(userRepository, times(1)).findByRole(role);
    }

    @Test
    void shouldReturnMatchingUsersByName() {
        String name = "Carlos";
        List<User> mockUsers = Arrays.asList(new User(), new User());
        when(userRepository.findByName(name)).thenReturn(mockUsers);
        List<User> users = userService.getUsersByName(name);

        assertEquals(2, users.size());
        verify(userRepository, times(1)).findByName(name);
    }

    @Test
    void shouldReturnUserByEmailIfExists() {
        String email = "test@example.com";
        User mockUser = new User();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));
        Optional<User> user = userService.getUsersByEmail(email);

        assertEquals(Optional.of(mockUser), user);
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void shouldReturnEmptyIfUserNotFoundByEmail() {
        String email = "notfound@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        Optional<User> user = userService.getUsersByEmail(email);

        assertTrue(user.isEmpty());
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void shouldReturnUsersByRegistrationDate() {
        LocalDate date = LocalDate.of(2024, 3, 15);
        List<User> mockUsers = Arrays.asList(new User(), new User());
        when(userRepository.findByRegistrationDate(date)).thenReturn(mockUsers);
        List<User> users = userService.getUsersByRegistrationDate(date);

        assertEquals(2, users.size());
        verify(userRepository, times(1)).findByRegistrationDate(date);
    }

    @Test
    void shouldUpdateUserWhenExists() throws GYMException {
        String id = "user123";
        UserDTO userDTO = new UserDTO();
        User mockUser = new User();
        when(userRepository.findById(id)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        User updatedUser = userService.updateUser(id, userDTO);

        assertEquals(mockUser, updatedUser);
        verify(userRepository, times(1)).findById(id);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void shouldDeleteUserWhenIdIsValid() throws GYMException {
        String id = "user123";
        User mockUser = new User();
        mockUser.setId(id);

        when(userRepository.findById(id)).thenReturn(Optional.of(mockUser));
        doNothing().when(userRepository).deleteById(id);

        userService.deleteUser(id);

        verify(userRepository, times(1)).findById(id);
        verify(userRepository, times(1)).deleteById(id);
    }

    @Test
    void shouldDeleteUserWithValidId() throws GYMException {
        String id = "user123";
        User mockUser = new User();
        mockUser.setId(id);

        when(userRepository.findById(id)).thenReturn(Optional.of(mockUser));
        doNothing().when(userRepository).deleteById(id);

        userService.deleteUser(id);

        verify(userRepository, times(1)).findById(id);
        verify(userRepository, times(1)).deleteById(id);
    }

    @Test
    void shouldThrowExceptionWhenIdIsNullForDeletion() {
        GYMException exception = assertThrows(GYMException.class, () -> userService.deleteUser(null));
        assertEquals(GYMException.USER_NOT_NULL, exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenIdIsEmptyForDeletion() {
        GYMException exception = assertThrows(GYMException.class, () -> userService.deleteUser(""));
        assertEquals(GYMException.USER_NOT_NULL, exception.getMessage());
    }

    @Test
    void shouldReturnNullWhenUserIdDoesNotExist() {
        String id = "nonexistent";
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        User result = userService.getUsersById(id);

        assertNull(result);
        verify(userRepository, times(1)).findById(id);
    }

    @Test
    void shouldNotCheckDuplicateIfEmailIsSameAsCurrent() throws GYMException {
        String id = "user123";
        String sameEmail = "same@example.com";

        User existingUser = new User();
        existingUser.setId(id);
        existingUser.setEmail(sameEmail);

        UserDTO dto = new UserDTO();
        dto.setEmail(sameEmail);

        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.updateUser(id, dto);

        verify(userRepository, never()).findByEmail(anyString());
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundOnUpdate() {
        String id = "nonexistent";
        UserDTO dto = new UserDTO();

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        GYMException exception = assertThrows(GYMException.class, () -> userService.updateUser(id, dto));
        assertEquals(GYMException.USER_NOT_FOUND, exception.getMessage());

        verify(userRepository, times(1)).findById(id);
    }
    @Test
    void shouldThrowExceptionWhenUserNotFoundOnDelete() {
        String id = "nonexistent";

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        GYMException exception = assertThrows(GYMException.class, () -> userService.deleteUser(id));
        assertEquals(GYMException.USER_NOT_FOUND, exception.getMessage());

        verify(userRepository, times(1)).findById(id);
    }
    @Test
    void shouldThrowExceptionWhenNameIsEmpty() {
        UserDTO dto = new UserDTO();
        dto.setId("user1");
        dto.setName("");  // cadena vacía
        dto.setEmail("test@example.com");
        dto.setRole(Role.STUDENT);
        GYMException exception = assertThrows(GYMException.class, () -> userService.createUser(dto));
        assertEquals(GYMException.USER_NOT_NULL, exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenEmailIsEmpty() {
        UserDTO dto = new UserDTO();
        dto.setId("user1");
        dto.setName("Test");
        dto.setEmail("");  // cadena vacía
        dto.setRole(Role.STUDENT);
        GYMException exception = assertThrows(GYMException.class, () -> userService.createUser(dto));
        assertEquals(GYMException.USER_NOT_NULL, exception.getMessage());
    }

    @Test
    void shouldReturnCustomMessageFromToString() {
        GYMException exception = new GYMException(GYMException.RESERVE_NOT_FOUND);
        assertEquals(GYMException.RESERVE_NOT_FOUND, exception.toString());
    }

}