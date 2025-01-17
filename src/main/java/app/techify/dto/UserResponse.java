package app.techify.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class UserResponse {
    private String id;
    private String fullName;
    private String province;
    private LocalDate dob;
    private String gender;
    private String citizenId;
    private String district;
    private String ward;
    private String address;
    private String phone;
    private String email;
    private String role;
    private String avatar;
}
