package app.techify.service;

import app.techify.entity.Account;
import app.techify.entity.Staff;
import app.techify.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public Staff updateStaff(Staff staff) {
        if (!staffRepository.existsById(staff.getId())) {
            throw new RuntimeException("Staff not found with id: " + staff.getId());
        }
        return staffRepository.save(staff);
    }

    public void deleteStaff(String id) {
        if (!staffRepository.existsById(id)) {
            throw new RuntimeException("Staff not found with id: " + id);
        }
        staffRepository.deleteById(id);
    }
} 