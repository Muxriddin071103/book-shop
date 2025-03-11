package uz.app.dto;

import lombok.Data;

@Data
public class BookPageDTO {
    private Long bookId;
    private Integer pageNumber;
    private String content;
}
