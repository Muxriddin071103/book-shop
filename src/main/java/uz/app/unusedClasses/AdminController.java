/*
package uz.app.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.app.dto.*;
import uz.app.entity.*;
import uz.app.repository.*;
import uz.app.util.UserUtil;
import uz.app.entity.enums.Role;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserUtil userUtil;
    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductTypeRepository productTypeRepository;
    private final ProductRatingRepository productRatingRepository;
    private final BookPageRepository bookPageRepository;

    // Ensure only admins can access this controller
    private boolean isAdmin() {
        return userUtil.getCurrentUser()
                .map(user -> user.getRole() == Role.ADMIN)
                .orElse(false);
    }

    */
/** ===================== PRODUCT MANAGEMENT ===================== *//*


    @GetMapping("/products")
    public ResponseEntity<?> getAllProducts() {
        if (!isAdmin()) return ResponseEntity.status(403).body("Access denied");
        List<Product> products = productRepository.findAll();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        if (!isAdmin()) return ResponseEntity.status(403).body("Access denied");
        return productRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().body(null));
    }

    @PostMapping("/products")
    public ResponseEntity<?> addProduct(@RequestBody ProductDTO productDTO) {
        if (!isAdmin()) return ResponseEntity.status(403).body("Access denied");

        Product product = new Product();
        product.setName(productDTO.getName());
        product.setPrice(productDTO.getPrice());
        product.setSalePrice(productDTO.getSalePrice());
        product.setQuantity(productDTO.getQuantity());
        product.setDescription(productDTO.getDescription());
        product.setAbout(productDTO.getAbout());
        product.setProductCategory(productCategoryRepository.findById(productDTO.getProductCategoryId()).orElse(null));
        product.setProductType(productTypeRepository.findById(productDTO.getProductTypeId()).orElse(null));

        return ResponseEntity.ok(productRepository.save(product));
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody ProductDTO productDTO) {
        if (!isAdmin()) return ResponseEntity.status(403).body("Access denied");

        return productRepository.findById(id).map(product -> {
            product.setName(productDTO.getName());
            product.setPrice(productDTO.getPrice());
            product.setSalePrice(productDTO.getSalePrice());
            product.setQuantity(productDTO.getQuantity());
            product.setDescription(productDTO.getDescription());
            product.setAbout(productDTO.getAbout());
            product.setProductCategory(productCategoryRepository.findById(productDTO.getProductCategoryId()).orElse(null));
            product.setProductType(productTypeRepository.findById(productDTO.getProductTypeId()).orElse(null));
            return ResponseEntity.ok(productRepository.save(product));
        }).orElse(ResponseEntity.badRequest().body(null));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        if (!isAdmin()) return ResponseEntity.status(403).body("Access denied");

        return productRepository.findById(id).map(product -> {
            productRepository.delete(product);
            return ResponseEntity.ok("Product deleted successfully");
        }).orElse(ResponseEntity.badRequest().body("Product not found"));
    }

    */
/** ===================== PRODUCT CATEGORY MANAGEMENT ===================== *//*


    @GetMapping("/product-categories")
    public ResponseEntity<?> getAllProductCategories() {
        if (!isAdmin()) return ResponseEntity.status(403).body("Access denied");
        List<ProductCategory> categories = productCategoryRepository.findAll();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/product-categories/{id}")
    public ResponseEntity<?> getProductCategoryById(@PathVariable Long id) {
        if (!isAdmin()) return ResponseEntity.status(403).body("Access denied");

        return productCategoryRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().body(null));
    }

    @PostMapping("/product-categories")
    public ResponseEntity<?> addProductCategory(@RequestBody NameDTO nameDTO) {
        if (!isAdmin()) return ResponseEntity.status(403).body("Access denied");

        ProductCategory category = new ProductCategory();
        category.setName(nameDTO.getName());
        return ResponseEntity.ok(productCategoryRepository.save(category));
    }

    @PutMapping("/product-categories/{id}")
    public ResponseEntity<?> updateProductCategory(@PathVariable Long id, @RequestBody NameDTO nameDTO) {
        if (!isAdmin()) return ResponseEntity.status(403).body("Access denied");

        return productCategoryRepository.findById(id).map(category -> {
            category.setName(nameDTO.getName());
            return ResponseEntity.ok(productCategoryRepository.save(category));
        }).orElse(ResponseEntity.badRequest().body(null));
    }

    @DeleteMapping("/product-categories/{id}")
    public ResponseEntity<?> deleteProductCategory(@PathVariable Long id) {
        if (!isAdmin()) return ResponseEntity.status(403).body("Access denied");

        return productCategoryRepository.findById(id).map(category -> {
            productCategoryRepository.delete(category);
            return ResponseEntity.ok("Category deleted successfully");
        }).orElse(ResponseEntity.badRequest().body("Category not found"));
    }

    */
/** ===================== PRODUCT TYPE MANAGEMENT ===================== *//*


    @GetMapping("/product-types")
    public ResponseEntity<?> getAllProductTypes() {
        if (!isAdmin()) return ResponseEntity.status(403).body("Access denied");
        List<ProductType> types = productTypeRepository.findAll();
        return ResponseEntity.ok(types);
    }

    @GetMapping("/product-types/{id}")
    public ResponseEntity<?> getProductTypeById(@PathVariable Long id) {
        if (!isAdmin()) return ResponseEntity.status(403).body("Access denied");

        return productTypeRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().body(null));
    }

    @PostMapping("/product-types")
    public ResponseEntity<?> addProductType(@RequestBody NameDTO nameDTO) {
        if (!isAdmin()) return ResponseEntity.status(403).body("Access denied");

        ProductType type = new ProductType();
        type.setName(nameDTO.getName());
        return ResponseEntity.ok(productTypeRepository.save(type));
    }

    @PutMapping("/product-types/{id}")
    public ResponseEntity<?> updateProductType(@PathVariable Long id, @RequestBody NameDTO nameDTO) {
        if (!isAdmin()) return ResponseEntity.status(403).body("Access denied");

        return productTypeRepository.findById(id).map(type -> {
            type.setName(nameDTO.getName());
            return ResponseEntity.ok(productTypeRepository.save(type));
        }).orElse(ResponseEntity.badRequest().body(null));
    }

    @DeleteMapping("/product-types/{id}")
    public ResponseEntity<?> deleteProductType(@PathVariable Long id) {
        if (!isAdmin()) return ResponseEntity.status(403).body("Access denied");

        return productTypeRepository.findById(id).map(type -> {
            productTypeRepository.delete(type);
            return ResponseEntity.ok("Type deleted successfully");
        }).orElse(ResponseEntity.badRequest().body("Type not found"));
    }

    */
/** ===================== PRODUCT RATING MANAGEMENT ===================== *//*


    @GetMapping("/product-ratings")
    public ResponseEntity<?> getAllProductRatings() {
        if (!isAdmin()) return ResponseEntity.status(403).body("Access denied");
        List<ProductRating> ratings = productRatingRepository.findAll();
        return ResponseEntity.ok(ratings);
    }

    @GetMapping("/product-ratings/{productId}")
    public ResponseEntity<?> getProductRatingsByProductId(@PathVariable Long productId) {
        if (!isAdmin()) return ResponseEntity.status(403).body("Access denied");

        List<ProductRating> ratings = productRatingRepository.findByBook_Id(productId);
        if (ratings.isEmpty()) return ResponseEntity.badRequest().body("No ratings found for this product");
        return ResponseEntity.ok(ratings);
    }

    @PostMapping("/product-ratings")
    public ResponseEntity<?> addProductRating(@RequestBody ProductRatingDTO ratingDTO) {
        if (!isAdmin()) return ResponseEntity.status(403).body("Access denied");

        ProductRating rating = new ProductRating();
        rating.setBook(productRepository.findById(ratingDTO.getProductId()).orElse(null));
        rating.setRating(ratingDTO.getRating());
        rating.setReview(ratingDTO.getReview());
        return ResponseEntity.ok(productRatingRepository.save(rating));
    }

    */
/** ===================== BOOK PAGE MANAGEMENT ===================== *//*


    @GetMapping("/book-pages")
    public ResponseEntity<?> getAllBookPages() {
        if (!isAdmin()) return ResponseEntity.status(403).body("Access denied");
        List<BookPage> pages = bookPageRepository.findAll();
        return ResponseEntity.ok(pages);
    }

    @GetMapping("/book-pages/{id}")
    public ResponseEntity<?> getBookPageById(@PathVariable Long id) {
        if (!isAdmin()) return ResponseEntity.status(403).body("Access denied");

        return bookPageRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().body(null));
    }

    @PostMapping("/book-pages")
    public ResponseEntity<?> addBookPage(@RequestBody BookPageDTO pageDTO) {
        if (!isAdmin()) return ResponseEntity.status(403).body("Access denied");

        BookPage page = new BookPage();
        page.setPageNumber(pageDTO.getPageNumber());
        page.setContent(pageDTO.getContent());
        return ResponseEntity.ok(bookPageRepository.save(page));
    }

    @DeleteMapping("/book-pages/{id}")
    public ResponseEntity<?> deleteBookPage(@PathVariable Long id) {
        if (!isAdmin()) return ResponseEntity.status(403).body("Access denied");

        return bookPageRepository.findById(id).map(page -> {
            bookPageRepository.delete(page);
            return ResponseEntity.ok("Book page deleted successfully");
        }).orElse(ResponseEntity.badRequest().body("Book page not found"));
    }
}
*/
