package app.techify.service;

import app.techify.dto.TransportVendorDto;
import app.techify.entity.TransportVendor;
import app.techify.repository.TransportVendorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
@Service
@RequiredArgsConstructor
public class TransportVendorService {

    private final TransportVendorRepository transportVendorRepository;

    public List<TransportVendor> getAllTransportVendors() {
        return transportVendorRepository.findAllByStatusTrue();
    }

    public TransportVendor getTransportVendorById(String id) {
        return transportVendorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transport vendor not found with id: " + id));
    }

    public TransportVendor createTransportVendor(TransportVendorDto transportVendorDto) {
        TransportVendor transportVendor = new TransportVendor();
        String uniqueId = generateUniqueId();
        transportVendor.setId(uniqueId);
        transportVendor.setName(transportVendorDto.getName());
        transportVendor.setPhone(transportVendorDto.getPhone());
        transportVendor.setEmail(transportVendorDto.getEmail());
        transportVendor.setBasePrice(transportVendorDto.getBasePrice());

        transportVendor.setStatus(true);

        return transportVendorRepository.save(transportVendor);
    }

    private String generateUniqueId() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String dateString = currentDate.format(formatter);
        String baseId = "TPV-" + dateString;

        int maxSequence = transportVendorRepository.findMaxSequenceForDate(baseId)
                .orElse(0);

        int newSequence = maxSequence + 1;

        String sequenceString = String.format("%03d", newSequence);

        return baseId + sequenceString;
    }

    public TransportVendor updateTransportVendor(TransportVendor transportVendor) {
        if (!transportVendorRepository.existsById(transportVendor.getId())) {
            throw new RuntimeException("Transport vendor not found with id: " + transportVendor.getId());
        }
        return transportVendorRepository.save(transportVendor);
    }

    public void deleteTransportVendor(String id) {
        TransportVendor transportVendor = transportVendorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transport vendor not found with id: " + id));

        transportVendor.setStatus(false);
        transportVendorRepository.save(transportVendor);
    }

} 