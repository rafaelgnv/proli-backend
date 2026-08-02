package com.rafael.proli_api.modules.folder;

import com.rafael.proli_api.modules.folder.dto.FolderRequestDTO;
import com.rafael.proli_api.modules.folder.dto.FolderResponseDTO;
import com.rafael.proli_api.modules.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/folders")
@RequiredArgsConstructor
public class FolderController {

    private final FolderService folderService;

    @PostMapping
    public ResponseEntity<FolderResponseDTO> createFolder(
            @Valid @RequestBody FolderRequestDTO dto,
            @AuthenticationPrincipal User loggedUser) {

        FolderResponseDTO response = folderService.createFolder(dto, loggedUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<FolderResponseDTO>> listFolders(
            @RequestParam(required = false) Long parentId,
            @AuthenticationPrincipal User loggedUser) {

        List<FolderResponseDTO> response = folderService.listFolders(parentId, loggedUser);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FolderResponseDTO> renameFolder(
            @PathVariable Long id,
            @RequestBody FolderRequestDTO dto,
            @AuthenticationPrincipal User loggedUser) {

        FolderResponseDTO response = folderService.renameFolder(id, dto.getName(), loggedUser);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFolder(
            @PathVariable Long id,
            @AuthenticationPrincipal User loggedUser) {

        folderService.deleteFolder(id, loggedUser);
        return ResponseEntity.noContent().build();
    }
}