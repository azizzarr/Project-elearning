package tn.siga.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tn.siga.entities.User;
import tn.siga.repositories.UserRepository;


import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;




    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<User> user = userRepository.findByUserName(username);
        if (!user.isPresent()) {
            throw new UsernameNotFoundException("user name not found");
        }

        User actUser = user.get();





        return new org.springframework.security.core.userdetails.User(
                actUser.getUserName(),
                actUser.getPassword(),
                actUser.isEnabled(),
                actUser.isAccountNonExpired(),
                actUser.isCredentialsNonExpired(),
                actUser.isAccountNonLocked(),
                actUser.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName().toString())).collect(Collectors.toList())

        );
    }






}


