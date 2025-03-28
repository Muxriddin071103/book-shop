package uz.app.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.app.dto.UserDTO;
import uz.app.entity.User;
import uz.app.entity.enums.Role;
import uz.app.repository.UserRepository;

import java.util.List;
import java.util.Optional;

/*  PHONE-NUMBER: 111,  PASSWORD: super111.   For logining to system     */
@RestController
@RequestMapping("/super-admin")
@RequiredArgsConstructor
@Tag(name = "Super Admin Controller",description = "Only Super Admin can manage")
public class SuperAdminController {
    private final UserRepository userRepository;

    @GetMapping("/users")
    public ResponseEntity<?> getUsers() {
        List<UserDTO> admins = userRepository
                .findAll()
                .stream()
                .filter(user -> user.getRole() == Role.USER)
                .map(user -> new UserDTO(
                        user.getFirstName(),
                        user.getLastName(),
                        user.getBirthYear(),
                        user.getPhoneNumber(),
                        user.getRole()
                ))
                .toList();

        return ResponseEntity.ok(admins);
    }

    @GetMapping("/admins")
    public ResponseEntity<List<UserDTO>> getAdmins() {
        List<UserDTO> admins = userRepository
                .findAll()
                .stream()
                .filter(user -> user.getRole() == Role.ADMIN)
                .map(user -> new UserDTO(
                        user.getFirstName(),
                        user.getLastName(),
                        user.getBirthYear(),
                        user.getPhoneNumber(),
                        user.getRole()
                ))
                .toList();

        return ResponseEntity.ok(admins);
    }

    @GetMapping("/operators")
    public ResponseEntity<List<UserDTO>> getOperators() {
        List<UserDTO> operators = userRepository
                .findAll()
                .stream()
                .filter(user -> user.getRole() == Role.OPERATOR)
                .map(user -> new UserDTO(
                        user.getFirstName(),
                        user.getLastName(),
                        user.getBirthYear(),
                        user.getPhoneNumber(),
                        user.getRole()
                ))
                .toList();

        return ResponseEntity.ok(operators);
    }

    @GetMapping("/admin/{id}")
    public ResponseEntity<?> getAdminById(@PathVariable Long id) {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Admin not found");
        }

        User user = userOptional.get();

        if (user.getRole() != Role.ADMIN) {
            return ResponseEntity.badRequest().body("User is not an admin");
        }

        UserDTO userDTO = new UserDTO(
                user.getFirstName(),
                user.getLastName(),
                user.getBirthYear(),
                user.getPhoneNumber(),
                user.getRole()
        );

        return ResponseEntity.ok(userDTO);
    }

    @GetMapping("/operator/{id}")
    public ResponseEntity<?> getOperatorById(@PathVariable Long id) {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Operator not found");
        }

        User user = userOptional.get();

        if (user.getRole() != Role.OPERATOR) {
            return ResponseEntity.badRequest().body("User is not an operator");
        }

        UserDTO userDTO = new UserDTO(
                user.getFirstName(),
                user.getLastName(),
                user.getBirthYear(),
                user.getPhoneNumber(),
                user.getRole()
        );

        return ResponseEntity.ok(userDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        User user = userOptional.get();

        if (user.getRole() == Role.USER || user.getRole() == Role.ADMIN || user.getRole() == Role.OPERATOR) {
            return ResponseEntity.badRequest().body("I cannot show it, because it is not a super admin");
        }

        UserDTO userDTO = new UserDTO(
                user.getFirstName(),
                user.getLastName(),
                user.getBirthYear(),
                user.getPhoneNumber(),
                user.getRole()
        );

        return ResponseEntity.ok(userDTO);
    }

    @PostMapping("/add-admin")
    public ResponseEntity<UserDTO> addAdmin(@RequestBody UserDTO userDTO) {
        User user = new User();
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setBirthYear(userDTO.getBirthYear());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setRole(Role.ADMIN);

        User savedUser = userRepository.save(user);

        UserDTO savedUserDTO = new UserDTO(
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getBirthYear(),
                savedUser.getPhoneNumber(),
                savedUser.getRole()
        );

        return ResponseEntity.ok(savedUserDTO);
    }

    @PostMapping("/add-operator")
    public ResponseEntity<?> addOperator(@RequestBody UserDTO userDTO) {
        User user = new User();
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setBirthYear(userDTO.getBirthYear());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setRole(Role.OPERATOR);

        User savedUser = userRepository.save(user);

        UserDTO savedUserDTO = new UserDTO(
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getBirthYear(),
                savedUser.getPhoneNumber(),
                savedUser.getRole()
        );

        return ResponseEntity.ok(savedUserDTO);
    }

    @PutMapping("/edit-user/{id}")
    public ResponseEntity<?> editUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        User existingUser = optionalUser.get();

        if (existingUser.getRole() == Role.SUPER_ADMIN) {
            return ResponseEntity.status(403).body("Cannot edit a super admin");
        }

        existingUser.setFirstName(userDTO.getFirstName());
        existingUser.setLastName(userDTO.getLastName());
        existingUser.setBirthYear(userDTO.getBirthYear());
        existingUser.setPhoneNumber(userDTO.getPhoneNumber());
        existingUser.setRole(existingUser.getRole());

        User updatedUser = userRepository.save(existingUser);

        UserDTO updatedUserDTO = new UserDTO(
                updatedUser.getFirstName(),
                updatedUser.getLastName(),
                updatedUser.getBirthYear(),
                updatedUser.getPhoneNumber(),
                updatedUser.getRole()
        );

        return ResponseEntity.ok(updatedUserDTO);
    }

    @DeleteMapping("/delete-admin/{id}")
    public ResponseEntity<?> deleteAdmin(@PathVariable Long id) {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Admin not found");
        }

        User user = userOptional.get();

        if (user.getRole() != Role.ADMIN) {
            return ResponseEntity.badRequest().body("User is not an admin");
        }

        userRepository.deleteById(id);
        return ResponseEntity.ok().body("Admin deleted successfully");
    }

    @DeleteMapping("/delete-operator/{id}")
    public ResponseEntity<?> deleteOperator(@PathVariable Long id) {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Operator not found");
        }

        User user = userOptional.get();

        if (user.getRole() != Role.OPERATOR) {
            return ResponseEntity.badRequest().body("User is not an operator");
        }

        userRepository.deleteById(id);
        return ResponseEntity.ok().body("Operator deleted successfully");
    }

    @DeleteMapping("/delete-user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);

        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (user.get().getRole() == Role.SUPER_ADMIN) {
            return ResponseEntity.status(403).body("Cannot delete a super admin");
        }

        userRepository.deleteById(id);
        return ResponseEntity.ok().body("User deleted successfully");
    }
}