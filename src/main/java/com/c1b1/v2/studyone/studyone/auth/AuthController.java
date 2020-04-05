package com.c1b1.v2.studyone.studyone.auth;

import com.c1b1.v2.studyone.studyone.domain.User;
import com.c1b1.v2.studyone.studyone.domain.rest.OathVerification;
import com.c1b1.v2.studyone.studyone.repository.UserRepository;
import com.c1b1.v2.studyone.studyone.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    UserRepository users;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    OathVerifier oathVerifier;

    @PostMapping("/signin")
    public ResponseEntity signin(@RequestBody AuthenticationRequest data) {

        try {
            String username = data.getUsername();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, data.getPassword()));
            String token = jwtTokenProvider.createToken(username, this.users.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Username " + username + "not found")).getRoles());

            Map<Object, Object> model = new HashMap<>();
            model.put("username", username);
            model.put("token", token);
            return ok(model);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username/password supplied");
        }
    }

    @PostMapping("/getorcreate")
    public ResponseEntity getOrCreate(@RequestBody AuthenticationRequest data) {
        try {
            // TODO(shgar): Procure token from Google api.
            String gToken = data.getPassword();

            OathVerification verification = oathVerifier.verify(gToken);

            String encodedPwd = passwordEncoder.encode(verification.getSub());
            String username = data.getUsername();


            Optional<User> user = users.findByUsername(username);

            if(!user.isPresent()) {
                // Let's create user
                users.save(User.builder()
                        .username(username)
                        .password(encodedPwd)
                        .roles(Arrays.asList( "ROLE_USER"))
                        .build());
            }

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, verification.getSub()));
            String token = jwtTokenProvider.createToken(username, this.users.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Username " + username + "not found")).getRoles());

            Map<Object, Object> model = new HashMap<>();
            model.put("username", username);
            model.put("token", token);
            return ok(model);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username/password supplied");
        }
    }
}