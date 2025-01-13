package app.techify.controller;

import app.techify.entity.StatisticsResult;
import app.techify.service.StatisticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/orders")
    public StatisticsResult getOrderStatistics() {
        return statisticsService.getOrderStatistics();
    }

    @GetMapping("/revenue")
    public StatisticsResult getRevenueStatistics() {
        return statisticsService.getRevenueStatistics();
    }

    @GetMapping("/products")
    public StatisticsResult getProductStatistics() {
        return statisticsService.getProductStatistics();
    }

    @GetMapping("/customers")
    public StatisticsResult getCustomerStatistics() {
        return statisticsService.getCustomerStatistics();
    }

    @GetMapping("/revenue/last-six-months")
    public List<Map<String, Object>> getRevenueLastSixMonths() {
        return statisticsService.getRevenueLastSixMonths();
    }
}