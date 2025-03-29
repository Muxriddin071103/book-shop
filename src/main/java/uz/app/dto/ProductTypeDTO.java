package uz.app.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProductTypeDTO {
    private String typeName;
    private List<ProductDTO> products;
}