package app.techify.controller;

import app.techify.dto.TopSellingProductResponse;
import app.techify.service.TopSellingProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/top-selling")
@RequiredArgsConstructor
public class TopSellingProductController {

    private final TopSellingProductService topSellingProductService;

    @GetMapping("")
    public ResponseEntity<List<TopSellingProductResponse>> getTopSellingProducts() {
        return ResponseEntity.ok(topSellingProductService.getTopSellingProducts());
    }
} 