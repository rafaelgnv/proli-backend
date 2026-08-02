package com.rafael.proli_api.modules.link;

import com.rafael.proli_api.modules.folder.Folder;
import com.rafael.proli_api.modules.folder.FolderRepository;
import com.rafael.proli_api.modules.link.dto.LinkRequestDTO;
import com.rafael.proli_api.modules.link.dto.LinkResponseDTO;
import com.rafael.proli_api.modules.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LinkService {

    private final LinkRepository linkRepository;
    private final FolderRepository folderRepository;
    private final TagRepository tagRepository;

    // 1. Inserção de Links
    public LinkResponseDTO createLink(LinkRequestDTO dto, User user) {
        Link link = new Link();
        link.setUrl(dto.getUrl());
        link.setThumbUrl(dto.getThumbUrl());
        link.setTitle(dto.getTitle());   // Captura o título extraído
        link.setAuthor(dto.getAuthor()); // Captura o autor/site extraído
        link.setUser(user);

        // Associa à pasta, se existir
        if (dto.getFolderId() != null) {
            Folder folder = folderRepository.findById(dto.getFolderId())
                    .orElseThrow(() -> new RuntimeException("Pasta não encontrada."));
            link.setFolder(folder);
        }

        // Processa as Tags
        Set<Tag> tagsToSave = new HashSet<>();
        if (dto.getTagIds() != null && !dto.getTagIds().isEmpty()) {
            for (String tagName : dto.getTagIds()) {
                String normalizedName = tagName.toLowerCase().trim();
                // Busca a tag no banco; se não existir, cria uma nova
                Tag tag = tagRepository.findByName(normalizedName)
                        .orElseGet(() -> tagRepository.save(new Tag(null, normalizedName)));
                tagsToSave.add(tag);
            }
        }
        link.setTags(tagsToSave);

        Link savedLink = linkRepository.save(link);
        return toResponseDTO(savedLink);
    }

    // 2. Leitura de Links
    public List<LinkResponseDTO> listLinks(Long folderId, List<String> tagIds, User user) {
        List<Link> links;

        // Se tem folderId, busca dentro da pasta. Se não, busca na raiz.
        if (folderId != null) {
            links = linkRepository.findByUserAndFolderId(user, folderId);
        } else {
            links = linkRepository.findByUserAndFolderIsNull(user);
        }

        // Filtro em memória pelas tags (caso o usuário clique nos filtros da tela)
        if (tagIds != null && !tagIds.isEmpty()) {
            links = links.stream()
                    .filter(link -> link.getTags().stream()
                            .map(Tag::getName)
                            .anyMatch(tagIds::contains))
                    .collect(Collectors.toList());
        }

        return links.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    // Método utilitário de conversão
    private LinkResponseDTO toResponseDTO(Link link) {
        return LinkResponseDTO.builder()
                .id(link.getId())
                .url(link.getUrl())
                .title(link.getTitle()) // Pode ser nulo se o front não enviar
                .thumbUrl(link.getThumbUrl())
                .author(link.getAuthor())
                .folderId(link.getFolder() != null ? link.getFolder().getId() : null)
                .tags(link.getTags().stream().map(Tag::getName).collect(Collectors.toList()))
                .createdAt(link.getCreatedAt())
                .build();
    }

    // 3. Exclusão de Links
    public void deleteLink(java.util.UUID linkId, User user) {
        Link link = linkRepository.findById(linkId)
                .orElseThrow(() -> new RuntimeException("Link não encontrado."));

        // Garante que o usuário logado é o dono do link
        if (!link.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Acesso negado. Você não pode excluir este link.");
        }

        linkRepository.delete(link);
    }

    // 4. Mover Link (Drag and Drop)
    public LinkResponseDTO moveLink(java.util.UUID linkId, Long folderId, User user) {
        Link link = linkRepository.findById(linkId)
                .orElseThrow(() -> new RuntimeException("Link não encontrado."));

        if (!link.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Acesso negado.");
        }

        if (folderId != null) {
            Folder folder = folderRepository.findById(folderId)
                    .orElseThrow(() -> new RuntimeException("Pasta destino não encontrada."));

            if (!folder.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("Acesso negado à pasta destino.");
            }
            link.setFolder(folder);
        } else {
            link.setFolder(null); // Caso o frontend envie nulo, move para a raiz
        }

        Link savedLink = linkRepository.save(link);
        return toResponseDTO(savedLink);
    }

    // 5. Editar Título do Link
    public LinkResponseDTO updateLinkTitle(java.util.UUID linkId, String newTitle, User user) {
        Link link = linkRepository.findById(linkId)
                .orElseThrow(() -> new RuntimeException("Link não encontrado."));

        if (!link.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Acesso negado.");
        }

        link.setTitle(newTitle);
        Link savedLink = linkRepository.save(link);
        return toResponseDTO(savedLink);
    }
}