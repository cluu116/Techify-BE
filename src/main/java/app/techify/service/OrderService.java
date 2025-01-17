package app.techify.service;

import app.techify.entity.Order;
import app.techify.entity.Voucher;
import app.techify.repository.OrderDetailRepository;
import app.techify.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import app.techify.dto.OrderResponse;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;

    public OrderResponse createOrder(Order order) {
        String orderId = "PO_" + Instant.now().toEpochMilli();
        order.setId(orderId);
        order.setCreatedAt(Instant.now());
        Order savedOrder = orderRepository.save(order);
        return convertToOrderResponse(savedOrder);
    }

    public OrderResponse updateOrder(Order order) {
        Order existingOrder = orderRepository.findById(order.getId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Update only the fields that should change
        existingOrder.setStatus(order.getStatus());
        existingOrder.setCustomer(order.getCustomer());
        existingOrder.setStaff(order.getStaff());
        existingOrder.setPaymentMethod(order.getPaymentMethod());
        existingOrder.setTransportVendor(order.getTransportVendor());
        existingOrder.setVoucher(order.getVoucher());
        existingOrder.setShippingAddress(order.getShippingAddress());

        existingOrder.setUpdatedAt(Instant.now());

        Order updatedOrder = orderRepository.save(existingOrder);
        return convertToOrderResponse(updatedOrder);
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::convertToOrderResponse)
                .collect(Collectors.toList());
    }


    public OrderResponse getOrderById(String id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
        return convertToOrderResponse(order);
    }

    public List<OrderResponse> getAllOrdersWithDetails() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream().map(this::convertToOrderResponse).collect(Collectors.toList());
    }

    private OrderResponse convertToOrderResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        
        // Set IDs
        response.setCustomerId(order.getCustomer() != null ? order.getCustomer().getId() : null);
        response.setStaffId(order.getStaff() != null ? order.getStaff().getId() : null);
        response.setPaymentMethodId(order.getPaymentMethod() != null ? order.getPaymentMethod().getId() : null);
        response.setTransportVendorId(order.getTransportVendor() != null ? order.getTransportVendor().getId() : null);
        response.setVoucherId(order.getVoucher() != null ? order.getVoucher().getId() : null);
        
        // Set names
        response.setCustomerName(order.getCustomer() != null ? order.getCustomer().getFullName() : null);
        response.setStaffName(order.getStaff() != null ? order.getStaff().getFullName() : null);
        response.setPaymentMethodName(order.getPaymentMethod() != null ? order.getPaymentMethod().getName() : null);
        response.setTransportVendorName(order.getTransportVendor() != null ? order.getTransportVendor().getName() : null);

        response.setShippingAddress(order.getShippingAddress());
        response.setStatus(order.getStatus());
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.systemDefault());
        response.setCreatedAt(order.getCreatedAt() != null ? formatter.format(order.getCreatedAt()) : null);
        response.setUpdatedAt(order.getUpdatedAt() != null ? formatter.format(order.getUpdatedAt()) : null);

        BigDecimal orderTotal = calculateOrderTotal(order);

        // Get shipping price
        BigDecimal shippingPrice = order.getTransportVendor() != null ?
                order.getTransportVendor().getBasePrice() : BigDecimal.ZERO;

        // Calculate discount
        BigDecimal discountValue = BigDecimal.ZERO;
        if (order.getVoucher() != null) {
            discountValue = calculateDiscountValue(order.getVoucher(), orderTotal.subtract(shippingPrice));
        }

        BigDecimal finalTotal = orderTotal.subtract(discountValue);

        response.setShipPrice(shippingPrice);
        response.setDisCountValue(discountValue);
        response.setTotal(finalTotal);

        return response;
    }

    private BigDecimal calculateOrderTotal(Order order) {
        BigDecimal tongGiaTriSanPham = orderDetailRepository.findByOrderId(order.getId()).stream()
                .map(detail -> detail.getPrice().multiply(BigDecimal.valueOf(detail.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal phiVanChuyen = order.getTransportVendor() != null ?
                order.getTransportVendor().getBasePrice() : BigDecimal.ZERO;

        return tongGiaTriSanPham.add(phiVanChuyen);
    }

    private BigDecimal calculateDiscountValue(Voucher voucher, BigDecimal total) {
        if (total.compareTo(voucher.getMinOrder()) < 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal discountAmount;
        if (voucher.getDiscountType()) {

            discountAmount = total.multiply(BigDecimal.valueOf(voucher.getDiscountValue()).divide(BigDecimal.valueOf(100)));
        } else {
            discountAmount = BigDecimal.valueOf(voucher.getDiscountValue());
        }

        if (voucher.getMaxDiscount() != null) {
            discountAmount = discountAmount.min(voucher.getMaxDiscount());
        }

        return discountAmount.min(total);
    }

    public OrderResponse updateOrderStatus(String id, Short status) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        existingOrder.setStatus(status);
        existingOrder.setUpdatedAt(Instant.now());

        Order updatedOrder = orderRepository.save(existingOrder);
        return convertToOrderResponse(updatedOrder);
    }

    public List<OrderResponse> getOrdersByCustomerId(String customerId) {
        List<Order> orders = orderRepository.findByCustomerId(customerId);
        return orders.stream()
                .sorted(Comparator.comparing(Order::getCreatedAt).reversed())
                .map(this::convertToOrderResponse)
                .collect(Collectors.toList());
    }
}
