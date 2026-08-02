package com.rafael.proli_api.modules.folder;

import com.rafael.proli_api.modules.folder.dto.FolderRequestDTO;
import com.rafael.proli_api.modules.folder.dto.FolderResponseDTO;
import com.rafael.proli_api.modules.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FolderService {

    private final FolderRepository folderRepository;

    // Criar pasta
    public FolderResponseDTO createFolder(FolderRequestDTO dto, User user) {
        Folder folder = new Folder();
        folder.setName(dto.getName());
        folder.setUser(user);

        // Verifica se tem parentId (ou seja, se está criando uma subpasta)
        if (dto.getParentId() != null) {
            Folder parent = folderRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new RuntimeException("Pasta pai não encontrada."));

            // Regra de segurança: A pasta pai deve pertencer ao mesmo usuário
            if (!parent.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("Acesso negado à pasta pai.");
            }
            folder.setParent(parent);
        }

        Folder saved = folderRepository.save(folder);
        return toDTO(saved);
    }

    // Listar pastas dinamicamente (raiz ou subpastas)
    public List<FolderResponseDTO> listFolders(Long parentId, User user) {
        List<Folder> folders;
        if (parentId != null) {
            folders = folderRepository.findByUserAndParentId(user, parentId);
        } else {
            folders = folderRepository.findByUserAndParentIsNull(user);
        }
        return folders.stream().map(this::toDTO).collect(Collectors.toList());
    }

    // Converter para DTO
    private FolderResponseDTO toDTO(Folder folder) {
        return FolderResponseDTO.builder()
                .id(folder.getId())
                .name(folder.getName())
                .parentId(folder.getParent() != null ? folder.getParent().getId() : null)
                .build();
    }

    // Renomear pasta
    public FolderResponseDTO renameFolder(Long id, String newName, User user) {
        Folder folder = folderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pasta não encontrada."));

        if (!folder.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Acesso negado para editar esta pasta.");
        }

        folder.setName(newName);
        Folder updated = folderRepository.save(folder);
        return toDTO(updated);
    }

    // Excluir pasta
    public void deleteFolder(Long id, User user) {
        Folder folder = folderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pasta não encontrada."));

        if (!folder.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Acesso negado para excluir esta pasta.");
        }

        folderRepository.delete(folder);
    }
}