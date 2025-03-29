package uz.app.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class OrderDTO {
    private UUID productId;
    private Integer quantity;
}
