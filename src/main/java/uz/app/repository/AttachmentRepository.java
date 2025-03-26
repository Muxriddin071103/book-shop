package uz.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.app.entity.Attachment;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
}
