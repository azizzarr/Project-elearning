package tn.siga.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import tn.siga.Interfaces.IRoleService;
import tn.siga.entities.User;
import tn.siga.repositories.UserRepository;

@Service
public class RoleService implements IRoleService {

    @Autowired
    private UserRepository userRepository;
    @Override
    public boolean hasRole(String roleName) {
        UserDetails currentUserDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = currentUserDetails.getUsername();

        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase(roleName));
    }
}

