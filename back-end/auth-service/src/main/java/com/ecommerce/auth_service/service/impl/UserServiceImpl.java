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
import com.ecommerce.auth_service.repository.UserRepository;
import com.ecommerce.auth_service.repository.VerificationCodeRepository;
import com.ecommerce.auth_service.service.RoleService;
import com.ecommerce.auth_service.service.UserRoleService;
import com.ecommerce.auth_service.service.UserService;
import com.ecommerce.auth_service.service.VerificationCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import sun.applet.Main;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final VerificationCodeService verificationCodeService;
    private final RoleService roleService;
    private final UserRoleService userRoleService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final VerificationCodeRepository verificationCodeRepository;

    @Override
    @Transactional
    public void register(RegistrationUserRequest request) {
        validateUniqueEmail(request.getEmail());
        validateUniquePhone(request.getPhoneNumber());

        User user = buildUserFromRequest(request);
        userRepository.save(user);

        assignDefaultUserRole(user);

        String code = verificationCodeService.generateCode(VerificationCodeType.REGISTRATION);

        // TODO: Send Notification to user
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new MainException(
                        GeneralError.NOT_FOUND.getCode(),
                        "user not found"
                ));
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
        Role userRole = roleService.findRoleByName(RoleEnum.USER.name());
        if (userRole == null) {
            throw new MainException(GeneralError.VALIDATION_ERROR.getCode(), "Default role 'USER' is not configured in the system");
        }
        userRoleService.saveUserRole(user, userRole);
    }
}
