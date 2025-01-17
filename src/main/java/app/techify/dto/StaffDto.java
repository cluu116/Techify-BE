package app.techify.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StaffDto {

    private String fullName;

    private String phone;

    private String province;

    private String district;

    private String ward;

    private String address;

    private LocalDate dob;

    private String gender;

    private String citizenId;
}
