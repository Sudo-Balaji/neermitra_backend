package com.neermitra.neermitra_backend.controller;

import com.neermitra.neermitra_backend.dto.ApiResponse;
import com.neermitra.neermitra_backend.dto.CustomerSignupRequest;
import com.neermitra.neermitra_backend.entity.User;
import com.neermitra.neermitra_backend.security.JwtUtil;
import com.neermitra.neermitra_backend.service.CustomerSignupService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neermitra.neermitra_backend.dto.LoginRequest;
import com.neermitra.neermitra_backend.dto.LoginResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;



@Slf4j
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

	
    private final CustomerSignupService customerSignupService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    

    public AuthController(CustomerSignupService customerSignupService, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.customerSignupService = customerSignupService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/customer/signup")
    public ResponseEntity<ApiResponse<Long>> customerSignup(@Valid @RequestBody CustomerSignupRequest request) {
   
        log.trace("Entering customerSignup endpoint");
        log.debug("Signup request received for district {} with lat {} and lng {}", request.getDistrict(),
                request.getLatitude(), request.getLongitude());
        log.info("Customer signup attempt for phone {}", maskPhone(request.getPhone()));
        try {
            User user = customerSignupService.signup(request);
            ApiResponse<Long> response = new ApiResponse<>(true, "Signup successful, please login!", user.getId());
            log.info("Customer signup successful for phone {}", maskPhone(request.getPhone()));
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            log.warn("Customer signup validation failed for phone {}: {}", maskPhone(request.getPhone()), ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error during signup for phone {}", maskPhone(request.getPhone()), ex);
            throw ex;
        } finally {
            log.trace("Exiting customerSignup endpoint");
        }
    }
    
    @PostMapping("/customer/login")
    public ResponseEntity<ApiResponse<LoginResponse>> customerLogin(@Valid @RequestBody LoginRequest request) {

        log.trace("Entering customerLogin endpoint");
        log.info("Customer login attempt for phone {}", maskPhone(request.getPhone()));

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getPhone(),
                            request.getPassword()
                    )
            );

            // If we reach here, authentication is successful
            org.springframework.security.core.userdetails.User principal =
                    (org.springframework.security.core.userdetails.User) authentication.getPrincipal();

            // Generate JWT using the phone (subject)
            String token = jwtUtil.generateToken(principal.getUsername());

            // Optionally load full User entity to send basic info
            User user = customerSignupService.findByPhone(principal.getUsername());

            LoginResponse loginResponse = new LoginResponse(
                    token,
                    user.getId(),
                    user.getName(),
                    user.getPhone(),
                    user.getEmail(),
                    user.getRole().name()
            );

            ApiResponse<LoginResponse> response =
                    new ApiResponse<>(true, "Login successful", loginResponse);

            log.info("Customer login successful for phone {}", maskPhone(request.getPhone()));
            log.trace("Exiting customerLogin endpoint");
            return ResponseEntity.ok(response);

        } catch (BadCredentialsException ex) {
            log.warn("Invalid credentials for phone {}", maskPhone(request.getPhone()));
            ApiResponse<LoginResponse> response =
                    new ApiResponse<>(false, "Invalid phone or password", null);
            return ResponseEntity.status(401).body(response);
        }
    }



    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 4) {
            return "****";
        }
        return "******" + phone.substring(phone.length() - 4);
    }
}
