package app.techify.service;

import app.techify.dto.OrderDetailDto;
import app.techify.entity.Order;
import app.techify.entity.OrderDetail;
import app.techify.entity.Product;
import app.techify.repository.OrderDetailRepository;
import app.techify.dto.OrderDetailResponse;
import app.techify.repository.OrderRepository;
import app.techify.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderDetailService {

    private final OrderDetailRepository orderDetailRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Transactional
    public void createOrderDetail(List<OrderDetailDto> orderDetailDtos) {
        for (OrderDetailDto dto : orderDetailDtos) {
            Order order = orderRepository.findById(dto.getOrderId())
                    .orElseThrow(() -> new RuntimeException("Order not found"));
            Product product = productRepository.findById(dto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            OrderDetail orderDetail = OrderDetail.builder()
                    .order(order)
                    .product(product)
                    .quantity(dto.getQuantity())
                    .price(dto.getPrice())
                    .color(dto.getColor())
                    .size(dto.getSize())
                    .build();

            orderDetailRepository.save(orderDetail);
        }
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
        response.setSize(orderDetail.getSize());
        response.setPrice(orderDetail.getPrice());
        response.setQuantity(orderDetail.getQuantity());
        response.setTotal(orderDetail.getPrice().multiply(BigDecimal.valueOf(orderDetail.getQuantity())));
        return response;
    }
}
