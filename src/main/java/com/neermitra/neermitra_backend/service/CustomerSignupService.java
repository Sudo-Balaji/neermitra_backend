package com.neermitra.neermitra_backend.service;

import com.neermitra.neermitra_backend.dto.CustomerSignupRequest;
import com.neermitra.neermitra_backend.entity.User;
import com.neermitra.neermitra_backend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class CustomerSignupService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerSignupService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User signup(CustomerSignupRequest request) {
        log.trace("CustomerSignupService.signup invoked");
        log.debug("Checking uniqueness for phone {} and email {}", request.getPhone(), request.getEmail());

        if (userRepository.existsByPhone(request.getPhone())) {
            log.warn("Duplicate phone signup attempt detected for {}", request.getPhone());
            throw new IllegalArgumentException("Phone number already registered");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Duplicate email signup attempt detected for {}", request.getEmail());
            throw new IllegalArgumentException("Email already registered");
        }

        User user = new User();
        user.setName(request.getName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setAddressLineOne(request.getAddressLineOne());
        user.setAddressLineTwo(request.getAddressLineTwo());
        user.setDistrict(request.getDistrict());
        user.setPincode(request.getPincode());
        user.setLocationLat(request.getLatitude());
        user.setLocationLng(request.getLongitude());
        user.setRole(User.Role.CUSTOMER);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);
        log.info("Customer profile created with id {} for phone {}", savedUser.getId(), request.getPhone());
        log.trace("CustomerSignupService.signup completed");
        return savedUser;
    }
    
    
    
	/*
	 * BELOW IS Login Transacation for login service
	 * Why NO separate LoginService? (Smart design choice)
	 * Reason: Authentication is NOT business logic
	 */

    @Transactional(readOnly = true)
    public User findByPhone(String phone) {
        return userRepository.findByPhone(phone)
                .orElseThrow(() -> new IllegalArgumentException("User not found with phone: " + phone));
    }

    
    
    
    
    
    
    
}
