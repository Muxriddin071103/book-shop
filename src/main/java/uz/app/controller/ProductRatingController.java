package uz.app.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.app.dto.ProductRatingDTO;
import uz.app.entity.ProductRating;
import uz.app.entity.User;
import uz.app.repository.ProductRatingRepository;
import uz.app.repository.ProductRepository;
import uz.app.util.UserUtil;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/product-rating")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Product Rating Management", description = "Every logined User can manage")
public class ProductRatingController {
    private final ProductRatingRepository productRatingRepository;
    private final ProductRepository productRepository;
    private final UserUtil userUtil; // Inject UserUtil

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(productRatingRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<?> addRating(@RequestBody ProductRatingDTO ratingDTO) {
        Optional<User> currentUser = userUtil.getCurrentUser();

        if (currentUser.isEmpty()) {
            return ResponseEntity.status(401).body("User is not authenticated");
        }

        return productRepository
                .findById(ratingDTO
                        .getProductId())
                .map(product -> {
                    ProductRating rating = new ProductRating();
                    rating.setBook(product);
                    rating.setUserId(currentUser.get().getId());
                    rating.setRating(ratingDTO.getRating());
                    rating.setReview(ratingDTO.getReview());

                    return ResponseEntity.ok(productRatingRepository.save(rating));
                })
                .orElseGet(() -> ResponseEntity.badRequest().body(null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductRating> getFromId(@PathVariable UUID id) {
        return productRatingRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody ProductRatingDTO ratingDTO) {
        Optional<User> currentUser = userUtil.getCurrentUser();

        if (currentUser.isEmpty()) {
            return ResponseEntity.status(401).body("User is not authenticated");
        }

        return productRatingRepository.findById(id)
                .map(existingRating -> {
                    if (!existingRating.getUserId().equals(currentUser.get().getId())) {
                        return ResponseEntity.status(403).body("You can only edit your own ratings");
                    }

                    return productRepository.findById(ratingDTO.getProductId())
                            .map(product -> {
                                existingRating.setBook(product);
                                existingRating.setRating(ratingDTO.getRating());
                                existingRating.setReview(ratingDTO.getReview());

                                return ResponseEntity.ok(productRatingRepository.save(existingRating));
                            })
                            .orElseGet(() -> ResponseEntity.badRequest().body(null));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        Optional<User> currentUser = userUtil.getCurrentUser();

        if (currentUser.isEmpty()) {
            return ResponseEntity.status(401).body("User is not authenticated");
        }

        return productRatingRepository.findById(id)
                .map(existingRating -> {
                    if (!existingRating.getUserId().equals(currentUser.get().getId())) {
                        return ResponseEntity.status(403).body("You can only delete your own ratings");
                    }
                    productRatingRepository.deleteById(id);
                    return ResponseEntity.ok().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
