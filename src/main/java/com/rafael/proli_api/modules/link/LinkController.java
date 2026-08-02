package com.rafael.proli_api.modules.link;

import com.rafael.proli_api.modules.link.dto.LinkPreviewDTO;
import com.rafael.proli_api.modules.link.dto.LinkRequestDTO;
import com.rafael.proli_api.modules.link.dto.LinkResponseDTO;
import com.rafael.proli_api.modules.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/links")
@RequiredArgsConstructor
public class LinkController {

    private final LinkService linkService;

    // Criar Link
    @PostMapping
    public ResponseEntity<LinkResponseDTO> createLink(
            @Valid @RequestBody LinkRequestDTO dto,
            @AuthenticationPrincipal User loggedUser) {

        LinkResponseDTO response = linkService.createLink(dto, loggedUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Listar Links
    @GetMapping
    public ResponseEntity<List<LinkResponseDTO>> listLinks(
            @RequestParam(required = false) Long folderId,
            @RequestParam(required = false) List<String> tagIds,
            @AuthenticationPrincipal User loggedUser) {

        List<LinkResponseDTO> response = linkService.listLinks(folderId, tagIds, loggedUser);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/preview")
    public ResponseEntity<LinkPreviewDTO> getPreview(@RequestBody LinkRequestDTO request) {
        LinkPreviewDTO preview = new LinkPreviewDTO();
        try {
            String targetUrl = request.getUrl();

            // 1. O Truque para o X (Twitter)
            if (targetUrl.contains("x.com") || targetUrl.contains("twitter.com")) {
                targetUrl = targetUrl.replace("x.com", "vxtwitter.com")
                        .replace("twitter.com", "vxtwitter.com");
            }

            // 2. O Disfarce (User-Agent)
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.connect(targetUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36")
                    .referrer("http://www.google.com")
                    .timeout(5000)
                    .get();

            // 3. Procura pelas tags
            String title = doc.select("meta[property=og:title]").attr("content");
            String image = doc.select("meta[property=og:image]").attr("content");
            String siteName = doc.select("meta[property=og:site_name]").attr("content");

            // 4. Fallback
            if (image.isEmpty()) image = doc.select("meta[name=twitter:image]").attr("content");
            if (title.isEmpty()) title = doc.select("meta[name=twitter:title]").attr("content");

            if (title.isEmpty()) title = doc.title();

            preview.setTitle(title);
            preview.setThumbUrl(image);

            if (siteName.isEmpty() && targetUrl.contains("vxtwitter")) {
                preview.setAuthor("X (Twitter)");
            } else {
                preview.setAuthor(siteName);
            }

            return ResponseEntity.ok(preview);
        } catch (Exception e) {
            return ResponseEntity.ok(preview);
        }
    }

    // Excluir Link
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLink(
            @PathVariable java.util.UUID id,
            @AuthenticationPrincipal User loggedUser) {

        linkService.deleteLink(id, loggedUser);
        return ResponseEntity.noContent().build();
    }

    // Mover Link (Drag & Drop)
    @PatchMapping("/{id}/move")
    public ResponseEntity<LinkResponseDTO> moveLink(
            @PathVariable java.util.UUID id,
            @RequestBody java.util.Map<String, Long> payload,
            @AuthenticationPrincipal User loggedUser) {

        Long folderId = payload.get("folderId");
        LinkResponseDTO response = linkService.moveLink(id, folderId, loggedUser);
        return ResponseEntity.ok(response);
    }

    // Renomear Título do Link
    @PatchMapping("/{id}/title")
    public ResponseEntity<LinkResponseDTO> updateLinkTitle(
            @PathVariable java.util.UUID id,
            @RequestBody java.util.Map<String, String> payload,
            @AuthenticationPrincipal User loggedUser) {

        String newTitle = payload.get("title");
        LinkResponseDTO response = linkService.updateLinkTitle(id, newTitle, loggedUser);
        return ResponseEntity.ok(response);
    }
}