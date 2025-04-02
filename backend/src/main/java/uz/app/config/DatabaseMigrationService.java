package uz.app.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class DatabaseMigrationService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void removeProductIdColumn() {
        try {
            String sql = "ALTER TABLE orders DROP COLUMN IF EXISTS product_id";
            jdbcTemplate.execute(sql);
            System.out.println(" 'product_id' column removed from orders table (if it existed).");
        } catch (Exception e) {
            System.err.println(" Error during database migration: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

