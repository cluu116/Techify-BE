package app.techify.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDto {

    private String fullName;

    private String phone;

    private String altPhone;

    private String province;

    private String district;

    private String ward;

    private String address;

    private String altAddress;
}