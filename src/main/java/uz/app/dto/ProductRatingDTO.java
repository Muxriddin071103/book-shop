package uz.app.dto;

import lombok.Data;

@Data
public class ProductRatingDTO {
    private Long productId;
    private Integer rating; // 1-5
    private String review; // Can be null
}
