package com.rafael.proli_api.modules.folder.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FolderRequestDTO {
    @NotBlank(message = "O nome da pasta é obrigatório")
    private String name;

    // Opcional: nulo indica que é uma pasta raiz
    private Long parentId;
}