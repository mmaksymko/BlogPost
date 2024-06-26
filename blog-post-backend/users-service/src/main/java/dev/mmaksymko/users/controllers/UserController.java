package dev.mmaksymko.users.controllers;

import dev.mmaksymko.users.dto.UserRequest;
import dev.mmaksymko.users.dto.UserResponse;
import dev.mmaksymko.users.dto.UserUpdateRequest;
import dev.mmaksymko.users.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users/")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("{id}/")
    public UserResponse getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping("email/{email}/")
    public UserResponse getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email);
    }

    @PostMapping
    public UserResponse createUser(@RequestBody UserRequest user) {
        return userService.saveUser(user);
    }

    @PutMapping("{id}/")
    public UserResponse updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest user) {
        return userService.updateUser(id, user);
    }

    @PatchMapping("{id}/pfp/")
    public UserResponse updateUserPfp(@PathVariable Long id, @RequestPart("file") MultipartFile pfpFile) {
        return userService.updateUserPfp(id, pfpFile);
    }

    @PatchMapping("/appoint-admin/{id}/")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public UserResponse appointAdmin(@PathVariable Long id) {
        return userService.appointAdmin(id);
    }

    @PatchMapping("/fire-admin/{id}/")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public UserResponse fireAdmin(@PathVariable Long id) {
        return userService.fireAdmin(id);
    }

    @DeleteMapping("{id}/")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
    }
}
