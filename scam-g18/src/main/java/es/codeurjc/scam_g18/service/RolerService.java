package es.codeurjc.scam_g18.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.scam_g18.model.Role;
import es.codeurjc.scam_g18.repository.RoleRepository;

@Service
public class RolerService {

    @Autowired
    private RoleRepository roleRepository;

    // Busca un rol por nombre.
    public Optional<Role> findByName(String name) {
        return roleRepository.findByName(name);
    }

    // Guarda un rol en base de datos.
    public void save(Role role) {
        roleRepository.save(role);
    }
}
