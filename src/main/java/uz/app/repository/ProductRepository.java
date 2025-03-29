package uz.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.app.entity.Product;
import uz.app.entity.ProductType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findTop5ByProductTypeOrderByIdDesc(ProductType productType);

    List<Product> findByProductType(ProductType productType);
}