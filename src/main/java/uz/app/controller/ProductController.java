package uz.app.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.app.dto.ProductDTO;
import uz.app.entity.*;
import uz.app.repository.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private final AttachmentRepository attachmentRepository;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productRepository.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Product> getFromId(@PathVariable Long id) {
        return productRepository
                .findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/by-type")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, List<Product>>> getProductsByType() {
        List<ProductType> productTypes = productTypeRepository.findAll();
        Map<String, List<Product>> result = new HashMap<>();

        for (ProductType type : productTypes) {
            List<Product> products = productRepository.findTop5ByProductTypeOrderByIdDesc(type);
            result.put(type.getName(), products);
        }

        return ResponseEntity.ok(result);
    }

    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<?> addProduct(
            @RequestParam("name") String name,
            @RequestParam("productTypeId") Long productTypeId,
            @RequestParam("productCategoryId") Long productCategoryId,
            @RequestParam(value = "authorId", required = false) Long authorId,
            @RequestParam("price") Double price,
            @RequestParam("salePrice") Double salePrice,
            @RequestParam("quantity") Integer quantity,
            @RequestParam("description") String description,
            @RequestParam("about") String about,
            @RequestPart(required = false) MultipartFile photo) throws IOException {

        Optional<ProductType> productType = productTypeRepository.findById(productTypeId);
        Optional<ProductCategory> productCategory = productCategoryRepository.findById(productCategoryId);
        Optional<Author> author = authorId != null ? authorRepository.findById(authorId) : Optional.empty();

        if (productType.isEmpty() || productCategory.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid ProductType or ProductCategory ID");
        }

        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setProductType(productType.get());
        product.setProductCategory(productCategory.get());
        product.setAuthor(author.orElse(null));
        product.setSalePrice(salePrice);
        product.setQuantity(quantity);
        product.setDescription(description);
        product.setAbout(about);

        if (photo != null && !photo.isEmpty()) {
            Attachment attachment = Attachment.createAttachment(photo, "photos");
            if (attachment != null) {
                attachmentRepository.save(attachment);
                product.setPhoto(attachment);
            }
        }

        productRepository.save(product);
        return ResponseEntity.ok("Product added successfully");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestPart ProductDTO productDTO,
            @RequestPart(required = false) MultipartFile photo) throws IOException {

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

        if (photo != null && !photo.isEmpty()) {
            if (updatedProduct.getPhoto() != null) {
                attachmentRepository.delete(updatedProduct.getPhoto());
            }

            Attachment attachment = Attachment.createAttachment(photo, "/products");
            updatedProduct.setPhoto(attachment);
        }

        return ResponseEntity.ok(productRepository.save(updatedProduct));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Product product = productOptional.get();
        productRepository.delete(product);
        return ResponseEntity.ok().build();
    }
}