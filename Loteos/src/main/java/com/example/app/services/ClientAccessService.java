package com.example.app.services;

import com.example.app.models.Lote;
import com.example.app.models.Usuario;
import com.example.app.repositories.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class ClientAccessService {

    private final UsuarioRepository usuarioRepository;

    public ClientAccessService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public boolean canAccessLoteo(Authentication authentication, Integer loteoId) {
        if (authentication == null || loteoId == null) {
            return false;
        }

        return usuarioRepository.findByUsername(authentication.getName())
                .map(Usuario::getLoteoAsignado)
                .map(loteo -> loteoId.equals(loteo.getIdLoteo()))
                .orElse(false);
    }

    public boolean canAccessLote(Authentication authentication, Lote lote) {
        return lote != null
                && lote.getLoteo() != null
                && canAccessLoteo(authentication, lote.getLoteo().getIdLoteo());
    }
}
