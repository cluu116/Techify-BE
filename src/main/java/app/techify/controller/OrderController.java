package app.techify.controller;

import app.techify.dto.OrderResponse;
import app.techify.entity.Account;
import app.techify.entity.Customer;
import app.techify.entity.Order;
import app.techify.entity.OrderDetail;
import app.techify.repository.AccountRepository;
import app.techify.repository.CustomerRepository;
import app.techify.service.EmailService;
import app.techify.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/order")

@RequiredArgsConstructor
public class OrderController {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private CustomerRepository customerRepository;

    private final OrderService orderService;
    private final EmailService emailService;

    @GetMapping("")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        List<OrderResponse> orders = orderService.getAllOrdersWithDetails();
        return ResponseEntity.ok(orders);
    }

    @PostMapping("")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody Order order) {
        return ResponseEntity.ok(orderService.createOrder(order));
    }

    @PutMapping("")
    public ResponseEntity<OrderResponse> updateOrder(@Valid @RequestBody Order order) {
        return ResponseEntity.ok(orderService.updateOrder(order));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable String id) {
        OrderResponse order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{id}/status/{status}")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable String id,
            @PathVariable Short status

    ) {
        OrderResponse updatedOrder = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(updatedOrder);
    }


    @GetMapping("/generateInvoice/{orderId}")
    public ResponseEntity<Map<String, Object>> generateInvoiceData(
            @PathVariable String orderId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Optional<Order> optionalOrder = orderService.findById(orderId);
        if (optionalOrder.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Order order = optionalOrder.get();
        List<Map<String, Object>> details = order.getOrderDetails().stream().map(detail -> {
            Map<String, Object> detailMap = new HashMap<>();
            detailMap.put("productName", detail.getProduct().getName());
            detailMap.put("price", detail.getPrice());
            detailMap.put("quantity", detail.getQuantity());
            return detailMap;
        }).toList();

        Map<String, Object> response = new HashMap<>();
        response.put("id", order.getId());
        response.put("shippingAddress", order.getShippingAddress());
        response.put("customerName", order.getCustomer().getFullName());
        response.put("customerPhone", order.getCustomer().getPhone());
        response.put("customerEmail", order.getCustomer().getAccount().getEmail());
        response.put("details", details);

        String sellerEmail = userDetails.getUsername();

        Customer sellerCustomer = customerRepository.findByAccount_Email(sellerEmail);
        if (sellerCustomer != null) {
            response.put("sellerName", sellerCustomer.getFullName());
        } else {
            response.put("sellerName", "Không xác định");
        }

        return ResponseEntity.ok(response);
    }



    @PostMapping("/sendInvoice")
    public ResponseEntity<String> sendInvoice(
            @RequestParam("file") MultipartFile file,
            @RequestParam("orderId") String orderId
    ) {

        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("Tệp bị thiếu");
        }
        if (orderId == null || orderId.isEmpty()) {
            return ResponseEntity.badRequest().body("Mã đơn hàng bị thiếu");
        }

        Optional<Order> optionalOrder = orderService.findById(orderId);
        if (optionalOrder.isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy đơn hàng");
        }

        Order order = optionalOrder.get();
        String email = order.getCustomer() != null && order.getCustomer().getAccount() != null
                ? order.getCustomer().getAccount().getEmail()
                : null;
        if (email == null) {
            return ResponseEntity.badRequest().body("Không tìm thấy email");
        }

        try {
            emailService.sendEmailWithAttachment(
                    email,
                    "Hóa đơn đặt hàng",
                    "Cảm ơn bạn đã tin tưởng và đặt hàng tại Techify. Dưới đây là tệp hóa đơn đính kèm",
                    file.getBytes(),
                    file.getOriginalFilename()
            );
            return ResponseEntity.ok("Gửi hóa đơn thành công");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Gửi hóa đơn thất bại: " + e.getMessage());
        }
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByCustomerId (@PathVariable String customerId){
        List<OrderResponse> orders = orderService.getOrdersByCustomerId(customerId);
        return ResponseEntity.ok(orders);

    }
}


