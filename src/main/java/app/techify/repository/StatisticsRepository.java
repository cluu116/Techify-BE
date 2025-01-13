package app.techify.repository;

import app.techify.entity.StatisticsResult;
import org.springframework.data.jpa.repository.Query;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class StatisticsRepository {

    private final JdbcTemplate jdbcTemplate;

    public StatisticsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public StatisticsResult getOrderStatistics() {
        return jdbcTemplate.queryForObject(
                "EXEC sp_GetOrderStatistics",
                (rs, rowNum) -> {
                    StatisticsResult result = new StatisticsResult();
                    result.setTotal(rs.getInt("TotalOrders"));
                    result.setPercentageChange(rs.getDouble("PercentageChange"));
                    return result;
                }
        );
    }

    public StatisticsResult getRevenueStatistics() {
        return jdbcTemplate.queryForObject(
                "EXEC sp_GetRevenueStatistics",
                (rs, rowNum) -> {
                    StatisticsResult result = new StatisticsResult();
                    result.setTotal(rs.getInt("TotalRevenue"));
                    result.setPercentageChange(rs.getDouble("PercentageChange"));
                    return result;
                }
        );
    }

    public StatisticsResult getProductStatistics() {
        return jdbcTemplate.queryForObject(
                "EXEC sp_GetProductStatistics",
                (rs, rowNum) -> {
                    StatisticsResult result = new StatisticsResult();
                    result.setTotal(rs.getInt("TotalProducts"));
                    result.setPercentageChange(rs.getDouble("PercentageChange"));
                    return result;
                }
        );
    }

    public StatisticsResult getCustomerStatistics() {
        return jdbcTemplate.queryForObject(
                "EXEC sp_GetCustomerStatistics",
                (rs, rowNum) -> {
                    StatisticsResult result = new StatisticsResult();
                    result.setTotal(rs.getInt("TotalCustomers"));
                    result.setPercentageChange(rs.getDouble("PercentageChange"));
                    return result;
                }
        );
    }

    public List<Map<String, Object>> getRevenueLastSixMonths() {
        return jdbcTemplate.queryForList("EXEC sp_GetRevenueLastSixMonths");
    }
}