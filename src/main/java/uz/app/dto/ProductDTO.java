package uz.app.dto;

import lombok.Data;

@Data
public class ProductDTO {
    private String name;
    private Long productTypeId;
    private Long productCategoryId;
    private Double price;
    private Double salePrice;
    private Integer quantity;
    private String description;
    private String about;
}
