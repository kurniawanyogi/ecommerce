package com.ecommerce.auth_service.controller;

import com.ecommerce.auth_service.entity.User;
import com.ecommerce.auth_service.model.request.RegistrationUserRequest;
import com.ecommerce.auth_service.model.request.UpdateUserRequest;
import com.ecommerce.auth_service.model.response.BaseResponse;
import com.ecommerce.auth_service.model.response.PaginationInfo;
import com.ecommerce.auth_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
    public ResponseEntity<BaseResponse> findUsers(@RequestParam(defaultValue = "1") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        int pageIndex = (page > 0) ? page - 1 : 0;
        Page<User> usersPage = userService.findAll(pageIndex, size);

        return ResponseEntity.ok(new BaseResponse(
                HttpStatus.OK.toString(),
                "Users data",
                usersPage.getContent(),
                null,
                new PaginationInfo(usersPage.getNumber(), usersPage.getSize(), usersPage.getTotalElements(), usersPage.getTotalPages())
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse> inactivateUser(@PathVariable("id") Long id) {
        userService.deactivate(id);
        return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.toString(), "User inactivate successfully", null, null));
    }

    @PostMapping("/{id}")
    public ResponseEntity<BaseResponse> requestReactivation(@PathVariable("id") Long id) {
        userService.requestUserReactivation(id);
        return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.toString(), "request user reactive successfully", null, null));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BaseResponse> updateUser(@PathVariable("id") Long id, @RequestBody UpdateUserRequest request) {
        userService.updateUser(id, request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new BaseResponse(HttpStatus.OK.toString(), "User updated successfully", null, null));
    }
}
