package com.rafael.proli_api.modules.link.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;

@Data
public class LinkRequestDTO {
    @NotBlank(message = "A URL é obrigatória")
    private String url;

    private String thumbUrl;

    private Long folderId;

    private String title;

    private String author;

    // Lista de nomes de tags que o frontend envia
    private List<String> tagIds;
}