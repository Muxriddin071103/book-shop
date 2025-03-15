package uz.app.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.app.dto.BookPageDTO;
import uz.app.entity.BookPage;
import uz.app.entity.Product;
import uz.app.repository.BookPageRepository;
import uz.app.repository.ProductRepository;

import java.util.List;

@RestController
@RequestMapping("/book-pages")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
@Tag(name = "Book Page Management", description = "Only Admin & Super Admin can manage")
public class BookPageController {
    private final BookPageRepository bookPageRepository;
    private final ProductRepository productRepository;

    @GetMapping
    public ResponseEntity<List<BookPage>> getAll() {
        return ResponseEntity.ok(bookPageRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<?> addPage(@RequestBody BookPageDTO pageDTO) {
        System.out.println("Book ID: " + pageDTO.getBookId());

        Product book = productRepository.findById(pageDTO.getBookId())
                .orElse(null);

        if (book == null) {
            return ResponseEntity.badRequest().body("Book not found");
        }

        System.out.println("Book found: " + book.getName());

        BookPage page = new BookPage();
        page.setBook(book);
        page.setPageNumber(pageDTO.getPageNumber());
        page.setContent(pageDTO.getContent());

        return ResponseEntity.ok(bookPageRepository.save(page));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookPage> getFromId(@PathVariable Long id) {
        return bookPageRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookPage> update(@PathVariable Long id, @RequestBody BookPageDTO pageDTO) {
        return bookPageRepository.findById(id)
                .map(existingPage -> {
                    existingPage.setPageNumber(pageDTO.getPageNumber());
                    existingPage.setContent(pageDTO.getContent());
                    return ResponseEntity.ok(bookPageRepository.save(existingPage));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (bookPageRepository.existsById(id)) {
            bookPageRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
