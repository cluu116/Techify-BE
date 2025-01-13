package app.techify.service;

import app.techify.entity.StatisticsResult;
import app.techify.repository.StatisticsRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatisticsService {

    private final StatisticsRepository statisticsRepository;

    public StatisticsService(StatisticsRepository statisticsRepository) {
        this.statisticsRepository = statisticsRepository;
    }

    public StatisticsResult getOrderStatistics() {
        return statisticsRepository.getOrderStatistics();
    }

    public StatisticsResult getRevenueStatistics() {
        return statisticsRepository.getRevenueStatistics();
    }

    public StatisticsResult getProductStatistics() {
        return statisticsRepository.getProductStatistics();
    }

    public StatisticsResult getCustomerStatistics() {
        return statisticsRepository.getCustomerStatistics();
    }

    public List<Map<String, Object>> getRevenueLastSixMonths() {
        return statisticsRepository.getRevenueLastSixMonths();
    }
}