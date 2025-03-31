package uz.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.UUID;

@Data
public class OrderDTO {
    @Schema(example = "string")
    private UUID productId;
    private Integer quantity;
    private String customerPhoneNumber;
    private String customerFullName;
}
