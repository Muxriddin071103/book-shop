package uz.app.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.app.dto.UserDTO;
import uz.app.entity.User;
import uz.app.util.UserUtil;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class DashboardController {

    private final UserUtil userUtil;

    @GetMapping("/dashboard")
    public ResponseEntity<UserDTO> dashboard() {
        Optional<User> currentUser = userUtil.getCurrentUser();

        return currentUser.map(user -> ResponseEntity.ok(
                        new UserDTO(user.getFirstName(), user.getLastName(), user.getBirthYear(), user.getPhoneNumber())
                ))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
