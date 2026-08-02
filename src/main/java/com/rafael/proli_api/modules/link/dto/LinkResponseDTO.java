package com.rafael.proli_api.modules.link.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class LinkResponseDTO {
    private UUID id;
    private String url;
    private String title;
    private String thumbUrl;
    private String author;
    private Long folderId;
    private List<String> tags; // Retorna os nomes das tags para renderizar no LinkCard
    private LocalDateTime createdAt;
}