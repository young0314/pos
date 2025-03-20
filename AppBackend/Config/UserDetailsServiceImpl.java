package com.example.pos_app.Config;

import com.example.pos_app.Repository.UserRepository;
import com.example.pos_app.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String adminId) throws UsernameNotFoundException {
        User user = userRepository.findById(adminId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + adminId));
        System.out.println("사용");
        return new org.springframework.security.core.userdetails.User(user.getAdminId(), user.getPassword(), new ArrayList<>());
    }
}