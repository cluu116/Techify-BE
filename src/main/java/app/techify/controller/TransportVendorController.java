package app.techify.controller;

import app.techify.entity.TransportVendor;
import app.techify.service.TransportVendorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/transport-vendor")
@RequiredArgsConstructor
public class TransportVendorController {

    private final TransportVendorService transportVendorService;

    @GetMapping("")
    public ResponseEntity<List<TransportVendor>> getAllTransportVendors() {
        return ResponseEntity.ok(transportVendorService.getAllTransportVendors());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransportVendor> getTransportVendorById(@PathVariable String id) {
        return ResponseEntity.ok(transportVendorService.getTransportVendorById(id));
    }

    @PostMapping("")
    public ResponseEntity<TransportVendor> createTransportVendor(@Valid @RequestBody TransportVendor transportVendor) {
        return ResponseEntity.ok(transportVendorService.createTransportVendor(transportVendor));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransportVendor> updateTransportVendor(
            @PathVariable String id,
            @Valid @RequestBody TransportVendor transportVendor
    ) {
        transportVendor.setId(id);
        return ResponseEntity.ok(transportVendorService.updateTransportVendor(transportVendor));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransportVendor(@PathVariable String id) {
        transportVendorService.deleteTransportVendor(id);
        return ResponseEntity.ok().build();
    }
} 