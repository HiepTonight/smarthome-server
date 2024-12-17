package com.hieptran.smarthome_server.Service;

import com.hieptran.smarthome_server.config.security.UserDetailsImpl;
import com.hieptran.smarthome_server.model.User;
import com.hieptran.smarthome_server.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userBasicInfo = userRepository.findByUsername(username);
        if (userBasicInfo.isPresent()) {
            return new UserDetailsImpl(userBasicInfo.get());
        }
        return null;
    }

    public UserDetails loadUserById(String id) throws UsernameNotFoundException {
        Optional<User> userBasicInfo = userRepository.findById(id);
        if (userBasicInfo.isPresent()) {
            return new UserDetailsImpl(userBasicInfo.get());
        }
        return null;
    }
}
