package com.rafael.proli_api.modules.link;

import com.rafael.proli_api.modules.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LinkRepository extends JpaRepository<Link, UUID> {
    // Links na raiz (sem pasta)
    List<Link> findByUserAndFolderIsNull(User user);

    // Links dentro de uma pasta específica
    List<Link> findByUserAndFolderId(User user, Long folderId);
}
