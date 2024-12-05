package app.techify.service;

import app.techify.entity.TransportVendor;
import app.techify.repository.TransportVendorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransportVendorService {

    private final TransportVendorRepository transportVendorRepository;

    public List<TransportVendor> getAllTransportVendors() {
        return transportVendorRepository.findAll();
    }

    public TransportVendor getTransportVendorById(String id) {
        return transportVendorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transport vendor not found with id: " + id));
    }

    public TransportVendor createTransportVendor(TransportVendor transportVendor) {
        if (transportVendor.getStatus() == null) {
            transportVendor.setStatus(true); // Set default status to active
        }
        return transportVendorRepository.save(transportVendor);
    }

    public TransportVendor updateTransportVendor(TransportVendor transportVendor) {
        if (!transportVendorRepository.existsById(transportVendor.getId())) {
            throw new RuntimeException("Transport vendor not found with id: " + transportVendor.getId());
        }
        return transportVendorRepository.save(transportVendor);
    }

    public void deleteTransportVendor(String id) {
        if (!transportVendorRepository.existsById(id)) {
            throw new RuntimeException("Transport vendor not found with id: " + id);
        }
        transportVendorRepository.deleteById(id);
    }
} 