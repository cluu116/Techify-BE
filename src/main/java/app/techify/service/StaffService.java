package app.techify.service;

import app.techify.dto.StaffDto;
import app.techify.entity.Account;
import app.techify.entity.Staff;
import app.techify.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StaffService {
    private final StaffRepository staffRepository;
    private final AccountService accountService;

    public void createStaff(Staff staff) {
        // Create account for staff with both roles
        Account account = Account.builder()
                .email(staff.getAccount().getEmail())
                .passwordHash(staff.getAccount().getPasswordHash())
                .role("STAFF") // Just set as STAFF since they'll have staff privileges
                .build();
        
        Account savedAccount = accountService.createAccount(account);
        
        // Generate staff ID using epoch timestamp
        String epoch = String.valueOf(System.currentTimeMillis());
        staff.setId("S_" + epoch);
        staff.setAccount(savedAccount);
        
        staffRepository.save(staff);
    }

    public List<Staff> getAllStaff() {
        return staffRepository.findAll();
    }

    public Staff getStaffById(String id) {
        return staffRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Staff not found with id: " + id));
    }

    @Transactional
    public Staff updateStaff(String staffId, StaffDto updateDto) {
        Optional<Staff> existingStaffOpt = staffRepository.findById(staffId);

        if (existingStaffOpt.isPresent()) {
            Staff existingStaff = existingStaffOpt.get();

            if (updateDto.getFullName() != null) {
                existingStaff.setFullName(updateDto.getFullName());
            }
            if (updateDto.getPhone() != null) {
                existingStaff.setPhone(updateDto.getPhone());
            }
            if (updateDto.getProvince() != null) {
                existingStaff.setProvince(updateDto.getProvince());
            }
            if (updateDto.getDistrict() != null) {
                existingStaff.setDistrict(updateDto.getDistrict());
            }
            if (updateDto.getWard() != null) {
                existingStaff.setWard(updateDto.getWard());
            }
            if (updateDto.getAddress() != null) {
                existingStaff.setAddress(updateDto.getAddress());
            }
            if (updateDto.getDob() != null) {
                existingStaff.setDob(updateDto.getDob());
            }
            if (updateDto.getGender() != null) {
                existingStaff.setGender(updateDto.getGender());
            }
            if (updateDto.getCitizenId() != null) {
                existingStaff.setCitizenId(updateDto.getCitizenId());
            }

            return staffRepository.save(existingStaff);
        } else {
            throw new RuntimeException("Staff not found with id: " + staffId);
        }
    }

    public void deleteStaff(String id) {
        if (!staffRepository.existsById(id)) {
            throw new RuntimeException("Staff not found with id: " + id);
        }
        staffRepository.deleteById(id);
    }
} 