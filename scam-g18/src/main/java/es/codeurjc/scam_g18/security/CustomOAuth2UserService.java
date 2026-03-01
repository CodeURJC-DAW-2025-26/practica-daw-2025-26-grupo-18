package es.codeurjc.scam_g18.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import es.codeurjc.scam_g18.model.Role;
import es.codeurjc.scam_g18.model.User;
import es.codeurjc.scam_g18.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. Request basic user data from Google
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 2. Extraemos el email
        String email = oAuth2User.getAttribute("email");

        // 3. Buscamos en NUESTRA base de datos si existe alguien con ese email
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            // IF THERE IS NO ACCOUNT: return a user pending registration
            // con ROLE_PENDING para poder redirigirle al formulario de completar datos
            List<GrantedAuthority> pendingAuthorities = new ArrayList<>();
            pendingAuthorities.add(new SimpleGrantedAuthority("ROLE_PENDING"));
            return new DefaultOAuth2User(pendingAuthorities, oAuth2User.getAttributes(), "email");
        }

        User dbUser = userOptional.get();

        // 4. Check whether the user is active
        if (!dbUser.getIsActive()) {
            throw new org.springframework.security.oauth2.core.OAuth2AuthenticationException(
                    new org.springframework.security.oauth2.core.OAuth2Error("account_disabled"),
                    "El usuario está baneado");
        }

        // 5. Cargamos los roles de NUESTRA base de datos
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Role role : dbUser.getRoles()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
        }

        // 5. Return the authenticated user (merging Google data with our DB)
        // Le decimos a Spring que el identificador principal será el email
        return new DefaultOAuth2User(authorities, oAuth2User.getAttributes(), "email");
    }
}