package app.techify.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private String fullName;
    private String province;
    private String district;
    private String ward;
    private String address;
    private String altAddress;
    private String phone;
    private String altPhone;
    private String email;
    private String role;
}
