package com.beloved.identityservices.Security.service;

import com.beloved.identityservices.Entity.User;
import com.beloved.identityservices.Security.dto.UserInfoUserDetails;
import com.beloved.identityservices.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

public class UserUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> userInfo = Optional.ofNullable(userRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("User with provided information not found")));

        return userInfo.map(UserInfoUserDetails::new)
                .orElseThrow(()-> new UsernameNotFoundException("User not found"+ email));
    }
}
