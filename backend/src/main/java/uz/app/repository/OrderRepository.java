package uz.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.app.entity.Order;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

}
