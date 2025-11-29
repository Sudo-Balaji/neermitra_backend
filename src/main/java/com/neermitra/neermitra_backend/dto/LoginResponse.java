package com.neermitra.neermitra_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
	
	private String token;
    private Long userId;
    private String name;
    private String phone;
    private String email;
    private String role;

}
