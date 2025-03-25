package uz.app.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.app.dto.ProductDTO;
import uz.app.entity.*;
import uz.app.repository.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "Product Management")
public class ProductController {
    private final ProductRepository productRepository;
    private final ProductTypeRepository productTypeRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final AuthorRepository authorRepository;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productRepository.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Product> getFromId(@PathVariable Long id) {
        return productRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<?> addProduct(@RequestBody ProductDTO productDTO) {
        Optional<ProductType> productType = productTypeRepository.findById(productDTO.getProductTypeId());
        Optional<ProductCategory> productCategory = productCategoryRepository.findById(productDTO.getProductCategoryId());
        Optional<Author> author = productDTO.getAuthorId() != null ?
                authorRepository.findById(productDTO.getAuthorId()) : Optional.empty();

        if (productType.isEmpty() || productCategory.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid ProductType or ProductCategory ID");
        }

        Product product = new Product();
        product.setName(productDTO.getName());
        product.setProductType(productType.get());
        product.setProductCategory(productCategory.get());
        product.setPrice(productDTO.getPrice());
        product.setSalePrice(productDTO.getSalePrice());
        product.setQuantity(productDTO.getQuantity());
        product.setDescription(productDTO.getDescription());
        product.setAbout(productDTO.getAbout());
        author.ifPresent(product::setAuthor);

        return ResponseEntity.ok(productRepository.save(product));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ProductDTO productDTO) {
        Optional<Product> existingProduct = productRepository.findById(id);
        Optional<ProductType> productType = productTypeRepository.findById(productDTO.getProductTypeId());
        Optional<ProductCategory> productCategory = productCategoryRepository.findById(productDTO.getProductCategoryId());
        Optional<Author> author = productDTO.getAuthorId() != null ?
                authorRepository.findById(productDTO.getAuthorId()) : Optional.empty();

        if (existingProduct.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if (productType.isEmpty() || productCategory.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid ProductType or ProductCategory ID");
        }

        Product updatedProduct = existingProduct.get();
        updatedProduct.setName(productDTO.getName());
        updatedProduct.setProductType(productType.get());
        updatedProduct.setProductCategory(productCategory.get());
        updatedProduct.setPrice(productDTO.getPrice());
        updatedProduct.setSalePrice(productDTO.getSalePrice());
        updatedProduct.setQuantity(productDTO.getQuantity());
        updatedProduct.setDescription(productDTO.getDescription());
        updatedProduct.setAbout(productDTO.getAbout());
        author.ifPresent(updatedProduct::setAuthor);

        return ResponseEntity.ok(productRepository.save(updatedProduct));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}