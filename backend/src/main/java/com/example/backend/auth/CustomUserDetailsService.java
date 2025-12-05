package com.example.backend.auth;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service // ¡IMPORTANTE! Esto lo registra como un Bean
public class CustomUserDetailsService implements UserDetailsService {

    // Asume que aquí tienes inyectado el repositorio para acceder a tu base de datos
    // @Autowired
    // private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // **PASO 1: Buscar al usuario en la base de datos**
        // Usuario usuarioEncontrado = userRepository.findByUsername(username);

        // if (usuarioEncontrado == null) {
        //     throw new UsernameNotFoundException("Usuario no encontrado: " + username);
        // }

        // **PASO 2: Construir el objeto UserDetails de Spring Security**
        // Si usas una entidad propia, debes mapear sus datos a UserDetails.

        // Ejemplo simple (Si usas un repositorio):
        // return new User(
        //     usuarioEncontrado.getUsername(),
        //     usuarioEncontrado.getPassword(),
        //     usuarioEncontrado.getAuthorities() // Roles y Permisos
        // );

        // **Ejemplo de Retorno temporal para probar (en memoria):**
        if ("testuser".equals(username)) {
            return User.withUsername("testuser")
                    .password("{noop}password") // {noop} significa sin codificación de contraseña
                    .roles("USER")
                    .build();
        } else {
            throw new UsernameNotFoundException("Usuario no encontrado con el nombre: " + username);
        }
    }
}