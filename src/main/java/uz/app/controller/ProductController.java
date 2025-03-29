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
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "Product Management")
@CrossOrigin(origins = "*")
public class ProductController {
    private final ProductRepository productRepository;
    private final ProductTypeRepository productTypeRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final AuthorRepository authorRepository;
    private final AttachmentRepository attachmentRepository;

    @GetMapping
    public ResponseEntity<?> getAllProducts() {
        return ResponseEntity.ok(productRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFromId(@PathVariable UUID id) {
        return productRepository
                .findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/by-type")
    public ResponseEntity<?> getProductsByType() {
        List<Map<String, Object>> result = productTypeRepository.findAll().stream()
                .map(type -> {
                    Map<String, Object> typeMap = new HashMap<>();
                    typeMap.put("typeName", type.getName());

                    List<Map<String, Object>> products = productRepository
                            .findTop5ByProductTypeOrderByIdDesc(type)
                            .stream()
                            .map(product -> {
                                Map<String, Object> productMap = new HashMap<>();
                                productMap.put("name", product.getName());
                                productMap.put("productTypeId", product.getProductType().getId());
                                productMap.put("productCategoryId", product.getProductCategory().getId());
                                productMap.put("authorId", product.getAuthor() != null ? product.getAuthor().getId() : null);
                                productMap.put("price", product.getPrice());
                                productMap.put("salePrice", product.getSalePrice());
                                productMap.put("quantity", product.getQuantity());
                                productMap.put("description", product.getDescription());
                                productMap.put("about", product.getAbout());

                                if (product.getPhoto() != null) {
                                    Map<String, Object> photoMap = new HashMap<>();
                                    photoMap.put("name", product.getPhoto().getName());
                                    photoMap.put("prefix", product.getPhoto().getPrefix());
                                    productMap.put("photo", photoMap);
                                }

                                return productMap;
                            })
                            .collect(Collectors.toList());

                    typeMap.put("products", products);
                    return typeMap;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    @GetMapping("/by-type/{typeName}")
    public ResponseEntity<?> getProductsByTypeName(@PathVariable String typeName) {
        Optional<ProductType> productTypeOptional = productTypeRepository.findByName(typeName);

        if (productTypeOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid ProductType name");
        }

        ProductType productType = productTypeOptional.get();

        List<Map<String, Object>> products = productRepository.findByProductType(productType)
                .stream()
                .map(product -> {
                    Map<String, Object> productMap = new HashMap<>();
                    productMap.put("name", product.getName());
                    productMap.put("productTypeId", product.getProductType().getId());
                    productMap.put("productCategoryId", product.getProductCategory().getId());
                    productMap.put("authorId", product.getAuthor() != null ? product.getAuthor().getId() : null);
                    productMap.put("price", product.getPrice());
                    productMap.put("salePrice", product.getSalePrice());
                    productMap.put("quantity", product.getQuantity());
                    productMap.put("description", product.getDescription());
                    productMap.put("about", product.getAbout());

                    if (product.getPhoto() != null) {
                        Map<String, Object> photoMap = new HashMap<>();
                        photoMap.put("name", product.getPhoto().getName());
                        photoMap.put("prefix", product.getPhoto().getPrefix());
                        productMap.put("photo", photoMap);
                    }

                    return productMap;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(products);
    }

    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<?> addProduct(
            @RequestParam("name") String name,
            @RequestParam("productTypeId") UUID productTypeId,
            @RequestParam("productCategoryId") UUID productCategoryId,
            @RequestParam(value = "authorId", required = false) UUID authorId,
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
            @PathVariable UUID id,
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
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Product product = productOptional.get();
        productRepository.delete(product);
        return ResponseEntity.ok().build();
    }
}