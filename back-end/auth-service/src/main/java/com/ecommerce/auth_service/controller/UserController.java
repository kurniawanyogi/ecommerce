package com.ecommerce.auth_service.controller;

import com.ecommerce.auth_service.entity.User;
import com.ecommerce.auth_service.model.request.RegistrationUserRequest;
import com.ecommerce.auth_service.model.response.BaseResponse;
import com.ecommerce.auth_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping(value = "/register", consumes = "application/json", produces = "application/json")
    public ResponseEntity<BaseResponse> registerUser(@Valid @RequestBody RegistrationUserRequest request) {
        userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new BaseResponse(HttpStatus.CREATED.toString(), "User registered successfully", null, null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse> getUserById(@PathVariable("id") Long id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.toString(), "User detail", user, null));
    }

    @GetMapping()
    public ResponseEntity<BaseResponse> findUsers() {
        List<User> users = userService.findAll();
        return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.toString(), "Users data", users, null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse> deleteUserById(@PathVariable("id") Long id) {
        userService.deactivate(id);
        return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.toString(), "User deactivated successfully", users, null));
    }
}
