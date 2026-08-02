package com.rafael.proli_api.modules.folder;

import com.rafael.proli_api.modules.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {

    // Busca pastas na raiz (sem pasta pai) de um usuário específico
    List<Folder> findByUserAndParentIsNull(User user);

    // Busca subpastas dentro de uma pasta específica do usuário
    List<Folder> findByUserAndParentId(User user, Long parentId);
}