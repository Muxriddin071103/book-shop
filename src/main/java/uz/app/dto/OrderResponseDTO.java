package uz.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import uz.app.entity.enums.OrderStatus;

@Data
@AllArgsConstructor
public class OrderResponseDTO {
    private Long orderId;
    private UserDTO user;
    private ProductDTO product;
    private int quantity;
    private double totalPrice;
    private OrderStatus status;
}
