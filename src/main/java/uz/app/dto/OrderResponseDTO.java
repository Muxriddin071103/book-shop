package uz.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import uz.app.entity.enums.OrderStatus;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class OrderResponseDTO {
    private UUID orderId;
    private String customerFullName;
    private String customerPhoneNumber;
    private ProductDTO product;
    private int quantity;
    private double totalPrice;
    private OrderStatus status;
}
