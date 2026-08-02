package com.rafael.proli_api.modules.folder.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class FolderResponseDTO {
    private Long id;
    private String name;
    private Long parentId;
    private int itemCount; // Quantidade de links/subpastas dentro dela
    private LocalDateTime createdAt;
}