package uz.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import uz.app.entity.enums.OrderStatus;

import java.util.UUID;

@Data
@AllArgsConstructor
public class OrderResponseDTO {
    private UUID orderId;
    private UserDTO user;
    private ProductDTO product;
    private int quantity;
    private double totalPrice;
    private OrderStatus status;
}
