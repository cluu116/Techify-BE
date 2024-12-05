package app.techify.service;

import app.techify.entity.OrderDetail;
import app.techify.repository.OrderDetailRepository;
import app.techify.dto.OrderDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderDetailService {

    private final OrderDetailRepository orderDetailRepository;

    public void createOrderDetail(List<OrderDetail> orderDetails) {
        orderDetailRepository.saveAll(orderDetails);
    }

    public List<OrderDetailResponse> getOrderDetailsByOrderId(String orderId) {
        List<OrderDetail> orderDetails = orderDetailRepository.findByOrderIdWithProduct(orderId);
        return orderDetails.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private OrderDetailResponse convertToResponse(OrderDetail orderDetail) {
        OrderDetailResponse response = new OrderDetailResponse();
        response.setId(orderDetail.getId());
        response.setProductId(orderDetail.getProduct().getId());
        response.setProductName(orderDetail.getProduct().getName());
        response.setProductThumbnail(orderDetail.getProduct().getThumbnail());
        response.setColor(orderDetail.getColor());
        response.setPrice(orderDetail.getPrice());
        response.setQuantity(orderDetail.getQuantity());
        response.setTotal(orderDetail.getPrice().multiply(BigDecimal.valueOf(orderDetail.getQuantity())));
        return response;
    }
}
