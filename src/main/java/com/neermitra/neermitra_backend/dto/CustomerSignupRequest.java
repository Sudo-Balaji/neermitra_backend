package com.neermitra.neermitra_backend.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CustomerSignupRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^[6-9][0-9]{9}$", message = "Phone must be 10 digits and Must be Valid")
    private String phone;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email address")
    private String email;

    @NotBlank(message = "Address first line is required")
    private String addressLineOne;

    @NotBlank(message = "Address second line is required")
    private String addressLineTwo;

    @NotBlank(message = "District is required")
    private String district;

    @Size(min = 6, max = 6, message = "Pincode must be 6 digits")
    @NotBlank(message = "Pincode is required")
    private String pincode;

    @NotNull(message = "Latitude is required")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    private Double longitude;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;

    @AssertTrue(message = "Password and confirm password must match")
    public boolean isPasswordMatching() {
        if (password == null || confirmPassword == null) {
            return false;
        }
        return password.equals(confirmPassword);
    }

    @AssertTrue(message = "Latitude and longitude cannot be the same values")
    public boolean isDifferentCoordinates() {
        if (latitude == null || longitude == null) {
            return true;
        }
        return Double.compare(latitude, longitude) != 0;
    }
}


