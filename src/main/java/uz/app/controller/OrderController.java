package uz.app.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import uz.app.dto.OrderDTO;
import uz.app.dto.OrderResponseDTO;
import uz.app.dto.ProductDTO;
import uz.app.entity.Order;
import uz.app.entity.Product;
import uz.app.entity.User;
import uz.app.entity.enums.OrderStatus;
import uz.app.repository.OrderRepository;
import uz.app.repository.ProductRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(name = "Order Management")
public class OrderController {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @PostMapping
    public ResponseEntity<?> addOrder(@RequestBody OrderDTO orderDTO, @AuthenticationPrincipal User user) {
        Optional<Product> productOptional = productRepository.findById(orderDTO.getProductId());

        if (productOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Product not found");
        }

        Product product = productOptional.get();
        if (orderDTO.getQuantity() <= 0) {
            return ResponseEntity.badRequest().body("Quantity must be greater than 0");
        }
        if (product.getQuantity() < orderDTO.getQuantity()) {
            return ResponseEntity.badRequest().body("Not enough stock available");
        }

        double totalPrice = product.getPrice() * orderDTO.getQuantity();

        Order newOrder = new Order();
        newOrder.setUser(user);
        newOrder.setCustomerFullName(orderDTO.getCustomerFullName());
        newOrder.setCustomerPhoneNumber(orderDTO.getCustomerPhoneNumber());
        newOrder.setProduct(product);
        newOrder.setQuantity(orderDTO.getQuantity());
        newOrder.setTotalPrice(totalPrice);
        newOrder.setStatus(OrderStatus.IN_PROGRESS);

        product.setQuantity(product.getQuantity() - orderDTO.getQuantity());
        productRepository.save(product);
        orderRepository.save(newOrder);

        return ResponseEntity.ok(convertToOrderResponseDTO(newOrder));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder(@PathVariable UUID id, @RequestBody OrderDTO orderDTO) {
        Optional<Order> existingOrderOptional = orderRepository.findById(id);
        if (existingOrderOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Order existingOrder = existingOrderOptional.get();
        Product product = existingOrder.getProduct();
        int oldQuantity = existingOrder.getQuantity();
        int newQuantity = orderDTO.getQuantity();

        if (newQuantity <= 0) {
            return ResponseEntity.badRequest().body("Quantity must be greater than 0");
        }
        if (newQuantity > product.getQuantity() + oldQuantity) {
            return ResponseEntity.badRequest().body("Not enough stock available");
        }

        product.setQuantity(product.getQuantity() + oldQuantity - newQuantity);
        productRepository.save(product);

        existingOrder.setCustomerFullName(orderDTO.getCustomerFullName());
        existingOrder.setCustomerPhoneNumber(orderDTO.getCustomerPhoneNumber());
        existingOrder.setQuantity(newQuantity);
        existingOrder.setTotalPrice(product.getPrice() * newQuantity);
        orderRepository.save(existingOrder);

        return ResponseEntity.ok(convertToOrderResponseDTO(existingOrder));
    }

    @GetMapping
    public ResponseEntity<?> getAllOrders() {
        List<OrderResponseDTO> orderDTOs = orderRepository
                .findAll()
                .stream()
                .map(this::convertToOrderResponseDTO)
                .toList();

        return ResponseEntity.ok(orderDTOs);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        return ResponseEntity.ok(convertToOrderResponseDTO(order));
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<?> deleteOrder(@PathVariable UUID orderId) {
        if (!orderRepository.existsById(orderId)) {
            return ResponseEntity.notFound().build();
        }
        orderRepository.deleteById(orderId);
        return ResponseEntity.ok("Order deleted successfully");
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN','OPERATOR')")
    @Tag(name = "Change order's status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable UUID id, @RequestParam OrderStatus status) {
        Optional<Order> existingOrderOptional = orderRepository.findById(id);
        if (existingOrderOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Order order = existingOrderOptional.get();
        Product product = order.getProduct();

        if (status == OrderStatus.CANCELLED) {
            product.setQuantity(product.getQuantity() + order.getQuantity());
            productRepository.save(product);
        }

        order.setStatus(status);
        orderRepository.save(order);

        return ResponseEntity.ok(convertToOrderResponseDTO(order));
    }

    private OrderResponseDTO convertToOrderResponseDTO(Order order) {
        Product product = order.getProduct();
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName(product.getName());
        productDTO.setProductTypeId(product.getProductType().getId());
        productDTO.setProductCategoryId(product.getProductCategory().getId());
        productDTO.setAuthorId(product.getAuthor().getId());
        productDTO.setPrice(product.getPrice());
        productDTO.setSalePrice(product.getSalePrice());
        productDTO.setQuantity(product.getQuantity());
        productDTO.setDescription(product.getDescription());
        productDTO.setAbout(product.getAbout());

        return new OrderResponseDTO(
                order.getId(),
                order.getCustomerFullName(),
                order.getCustomerPhoneNumber(),
                productDTO,
                order.getQuantity(),
                order.getTotalPrice(),
                order.getStatus()
        );
    }
}
