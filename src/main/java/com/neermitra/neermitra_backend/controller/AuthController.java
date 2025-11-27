package com.neermitra.neermitra_backend.controller;

import com.neermitra.neermitra_backend.dto.ApiResponse;
import com.neermitra.neermitra_backend.dto.CustomerSignupRequest;
import com.neermitra.neermitra_backend.entity.User;
import com.neermitra.neermitra_backend.service.CustomerSignupService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

	
    private final CustomerSignupService customerSignupService;
    

    public AuthController(CustomerSignupService customerSignupService) {
        this.customerSignupService = customerSignupService;
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

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 4) {
            return "****";
        }
        return "******" + phone.substring(phone.length() - 4);
    }
}
