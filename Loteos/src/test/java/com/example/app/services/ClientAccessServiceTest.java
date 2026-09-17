package com.example.app.services;

import com.example.app.models.Loteo;
import com.example.app.models.Usuario;
import com.example.app.repositories.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ClientAccessServiceTest {

    private final UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
    private final ClientAccessService service = new ClientAccessService(usuarioRepository);
    private final Authentication authentication = mock(Authentication.class);

    @Test
    void allowsClientToAccessAssignedLoteo() {
        Loteo loteo = new Loteo("Loma Alta");
        loteo.setIdLoteo(7);
        Usuario usuario = new Usuario("cliente", "encoded", "CLIENTE");
        usuario.setLoteoAsignado(loteo);

        when(authentication.getName()).thenReturn("cliente");
        when(usuarioRepository.findByUsername("cliente")).thenReturn(Optional.of(usuario));

        assertThat(service.canAccessLoteo(authentication, 7)).isTrue();
    }

    @Test
    void rejectsClientAccessToAnotherLoteo() {
        Loteo loteo = new Loteo("Loma Alta");
        loteo.setIdLoteo(7);
        Usuario usuario = new Usuario("cliente", "encoded", "CLIENTE");
        usuario.setLoteoAsignado(loteo);

        when(authentication.getName()).thenReturn("cliente");
        when(usuarioRepository.findByUsername("cliente")).thenReturn(Optional.of(usuario));

        assertThat(service.canAccessLoteo(authentication, 8)).isFalse();
    }

    @Test
    void rejectsAccessWithoutAuthenticationOrAssignment() {
        when(authentication.getName()).thenReturn("cliente");
        when(usuarioRepository.findByUsername("cliente")).thenReturn(Optional.of(new Usuario()));

        assertThat(service.canAccessLoteo(null, 7)).isFalse();
        assertThat(service.canAccessLoteo(authentication, 7)).isFalse();
    }
}
