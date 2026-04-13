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

    // Finds a role by name.
    public Optional<Role> findByName(String name) {
        return roleRepository.findByName(name);
    }

    // Saves a role in the database.
    public void save(Role role) {
        roleRepository.save(role);
    }
}
