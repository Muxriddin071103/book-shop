package uz.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.app.entity.ProductRating;

import java.util.List;

@Repository
public interface ProductRatingRepository extends JpaRepository<ProductRating,Long> {
    List<ProductRating> findByBook_Id(Long productId);
}
