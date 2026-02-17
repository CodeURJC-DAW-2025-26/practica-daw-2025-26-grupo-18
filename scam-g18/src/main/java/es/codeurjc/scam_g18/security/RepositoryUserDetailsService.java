package es.codeurjc.scam_g18.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import es.codeurjc.scam_g18.model.Role;

import es.codeurjc.scam_g18.model.User;
import es.codeurjc.scam_g18.repository.UserRepository;

@Service
public class RepositoryUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<GrantedAuthority> roles = new ArrayList<>();
        for (Role role : user.getRoles()) { // <--- Ahora iteramos Role, no String
            roles.add(new SimpleGrantedAuthority("ROLE_" + role.getName())); // Asumimos role.getName()
        }

        return new org.springframework.security.core.userdetails.User(user.getUsername(),
                user.getPassword(), roles);
    }
}