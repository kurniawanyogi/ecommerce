package com.ecommerce.auth_service.service.impl;

import com.ecommerce.auth_service.common.constant.GeneralError;
import com.ecommerce.auth_service.common.constant.GeneralStatus;
import com.ecommerce.auth_service.common.constant.RoleEnum;
import com.ecommerce.auth_service.common.constant.VerificationCodeType;
import com.ecommerce.auth_service.common.exception.MainException;
import com.ecommerce.auth_service.entity.Role;
import com.ecommerce.auth_service.entity.VerificationCode;
import com.ecommerce.auth_service.model.request.AdminRegistrationRequest;
import com.ecommerce.auth_service.model.request.RegistrationUserRequest;
import com.ecommerce.auth_service.model.request.SellerRegistrationRequest;
import com.ecommerce.auth_service.entity.User;
import com.ecommerce.auth_service.model.request.UpdateUserRequest;
import com.ecommerce.auth_service.repository.UserRepository;
import com.ecommerce.auth_service.repository.VerificationCodeRepository;
import com.ecommerce.auth_service.security.AuthUtil;
import com.ecommerce.auth_service.service.RoleService;
import com.ecommerce.auth_service.service.UserRoleService;
import com.ecommerce.auth_service.service.UserService;
import com.ecommerce.auth_service.service.VerificationCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import sun.applet.Main;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final VerificationCodeService verificationCodeService;
    private final RoleService roleService;
    private final UserRoleService userRoleService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void register(RegistrationUserRequest request) {
        validateUniqueEmail(request.getEmail());
        validateUniquePhone(request.getPhoneNumber());

        User user = buildUserFromRequest(request);
        User savedUser = userRepository.save(user);

        assignDefaultUserRole(user);

        String code = verificationCodeService.generateCode(VerificationCodeType.REGISTRATION, savedUser.getId());

        // TODO: Send Notification to user
    }

    @Override
    public void confirmVerification(VerificationCode verificationCode) {
        VerificationCode verifiedCode = verificationCodeService.verifyCode(verificationCode.getCode(), verificationCode.getType());
        if (verifiedCode == null) {
            throw new MainException(GeneralError.VALIDATION_ERROR.getCode(), "invalid code or the code has already expired");
        }
        User existingUser = getUserOrThrow(verifiedCode.getUserId());

        updateUserStatus(existingUser, GeneralStatus.ACTIVE.getValue(), null);
    }

    @Override
    public User findById(Long id) {
        return getUserOrThrow(id);
    }

    @Override
    public Page<User> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAll(pageable);
    }

    @Override
    public void deactivate(Long id) {
        Long currentUserId = getCurrentUserIdOrThrow();
        User user = getUserOrThrow(id);
        if (user.getStatus().equals(GeneralStatus.INACTIVE.getValue())) {
            throw new MainException(GeneralError.VALIDATION_ERROR.getCode(), "inactive user");
        }
        updateUserStatus(user, GeneralStatus.INACTIVE.getValue(), currentUserId);
    }

    @Override
    @Transactional
    public void requestUserReactivation(Long id) {
        Long currentUserId = getCurrentUserIdOrThrow();

        User user = getUserOrThrow(id);

        if (GeneralStatus.ACTIVE.getValue().equals(user.getStatus())) {
            throw new MainException(GeneralError.VALIDATION_ERROR.getCode(), "User is in ACTIVE status");
        }

        updateUserStatus(user, GeneralStatus.PENDING.getValue(), currentUserId);
        String code = verificationCodeService.generateCode(VerificationCodeType.REGISTRATION, user.getId());

        // TODO: Send Notification to user
    }

    @Override
    public void updateUser(Long id, UpdateUserRequest request) {
        Long currentUserId = getCurrentUserIdOrThrow();

        User user = getUserOrThrow(id);
        if (GeneralStatus.INACTIVE.getValue().equals(user.getStatus())) {
            throw new MainException(GeneralError.VALIDATION_ERROR.getCode(), "inactive user");
        }
        applyUpdateRequestToUser(user, request);
        user.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
        user.setUpdatedBy(currentUserId);
        userRepository.save(user);
    }

    private void applyUpdateRequestToUser(User user, UpdateUserRequest request) {
        updateIfPresent(request.getFirstName(), user::setFirstName);
        updateIfPresent(request.getLastName(), user::setLastName);
        updateIfPresent(request.getPhoneNumber(), user::setPhoneNumber);
        updateIfPresent(request.getGender(), user::setGender);
        updateIfPresent(request.getIdNumber(), user::setIdNumber);
        updateIfPresent(request.getIdType(), user::setIdType);

        if (request.getBirthDate() != null) {
            user.setBirthDate(parseBirthDate(request.getBirthDate()));
        }
    }

    private <T> void updateIfPresent(T value, Consumer<T> setter) {
        if (value != null) {
            setter.accept(value);
        }
    }

    private static Long getCurrentUserIdOrThrow() {
        Long currentUserId = AuthUtil.getCurrentUserId();
        if (currentUserId == null) {
            throw new MainException(GeneralError.UNAUTHORIZED.getCode(), "User is not authenticated");
        }
        return currentUserId;
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new MainException(GeneralError.NOT_FOUND.getCode(), "user not found"));
    }

    private void updateUserStatus(User user, String newStatus, Long currentUserId) {
        user.setStatus(newStatus);
        user.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
        user.setUpdatedBy(currentUserId);
        userRepository.save(user);
    }

    private void validateUniqueEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new MainException(GeneralError.VALIDATION_ERROR.getCode(), "email already registered");
        }
    }

    private void validateUniquePhone(String phoneNumber) {
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            if (userRepository.existsByPhoneNumber(phoneNumber)) {
                throw new MainException(GeneralError.VALIDATION_ERROR.getCode(), "phone number already registered");
            }
        }
    }

    private User buildUserFromRequest(RegistrationUserRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setGender(request.getGender());
        user.setStatus(GeneralStatus.PENDING.getValue());

        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isEmpty()) {
            user.setPhoneNumber(request.getPhoneNumber());
        }

        if (request.getBirthDate() != null && !request.getBirthDate().isEmpty()) {
            user.setBirthDate(parseBirthDate(request.getBirthDate()));
        }

        Timestamp now = new Timestamp(System.currentTimeMillis());
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        return user;
    }

    private Date parseBirthDate(String birthDateStr) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateFormat.setLenient(false);
            return dateFormat.parse(birthDateStr);
        } catch (ParseException e) {
            throw new MainException(GeneralError.VALIDATION_ERROR.getCode(), "Invalid birth date format, expected yyyy-MM-dd");
        }
    }

    private void assignDefaultUserRole(User user) {
        Role userRole = roleService.findRoleByName(RoleEnum.USER.getName());
        if (userRole == null) {
            throw new MainException(GeneralError.VALIDATION_ERROR.getCode(), "Default role 'USER' is not configured in the system");
        }
        userRoleService.saveUserRole(user, userRole);
    }
}
