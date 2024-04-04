package com.pokemonreview.api.controllers;

import com.pokemonreview.api.dto.AuthResponseDTO;
import com.pokemonreview.api.dto.LoginDto;
import com.pokemonreview.api.dto.RegisterDto;
import com.pokemonreview.api.models.Role;
import com.pokemonreview.api.models.UserEntity;
import com.pokemonreview.api.repository.RoleRepository;
import com.pokemonreview.api.repository.UserRepository;
import com.pokemonreview.api.security.JWTGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api/auth")
public class AuthController {

    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private RoleRepository roleRepository;

    private PasswordEncoder passwordEncoder;

    private JWTGenerator jwtGenerator;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository,
                          RoleRepository roleRepository, PasswordEncoder passwordEncoder,
                          JWTGenerator jwtGenerator) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtGenerator = jwtGenerator;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto){
        if(userRepository.existsByUsername(registerDto.getUsername())){
            return new ResponseEntity<>("Username is taken !", HttpStatus.BAD_REQUEST);
        }
        UserEntity newUserEntity = new UserEntity();
        newUserEntity.setUsername(registerDto.getUsername());
        newUserEntity.setPassword(passwordEncoder.encode(registerDto.getPassword()));

//        Role roles = roleRepository.findByName("USER").get();

        Optional<Role> optionalRole = roleRepository.findByName("USER");
        if (!optionalRole.isPresent()) {
            System.out.println("*************************USER does not exist**********************");
            roleRepository.save(new Role("USER"));
        }
        Role roles = roleRepository.findByName("USER").get();
        newUserEntity.setRoles(Collections.singletonList(roles));
        userRepository.save(newUserEntity);
        return new ResponseEntity<>("User registered success!", HttpStatus.CREATED);

    }



    // when a user's JWT token has expired and they need to log in again, it means their current authentication session is no longer valid,
    // and effectively, the user is no longer authenticated within the SecurityContextHolder.
    // At this point, for the user to access protected resources again, they must re-authenticate.
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginDto loginDto){


        // Internally, the AuthenticationManager uses the configured UserDetailsService to load the user from the database
        // (or another user store) and compares the submitted password (after encoding) with the stored password for the user.

        // If successful, an Authentication object is returned, containing the principal, credentials, and authorities (roles).
        // This object indeed has the user's authorities, even though it's not explicitly mentioned in the code snippet you provided.
        // The authorities are loaded as part of the user details by UserDetailsService and included in the Authentication object created
        // by Spring Security.
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));


        // Once the Authentication object is returned by the AuthenticationManager, it's considered that the user has been authenticated successfully.
        // The application then places this Authentication object into the SecurityContextHolder,
        // establishing the user's security context for the duration of the session:
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // After setting the authentication in the SecurityContextHolder, your application generates a JWT token for the user,
        // which includes the necessary claims (e.g., username, roles) and sets an expiration time:
        String token = jwtGenerator.generateToken(authentication);

        return new ResponseEntity<>(new AuthResponseDTO(token), HttpStatus.OK);
    }
    /*
    1.  Password Encoding: When a user's password is saved in the database, it is encoded (hashed) using a PasswordEncoder
        that you've configured in your security configuration. This ensures that plain text passwords are never stored in the database,
        enhancing security.

    2.  Authentication Process: During the login process, when a UsernamePasswordAuthenticationToken is created and passed
        to the AuthenticationManager for authentication, the AuthenticationManager delegates to the appropriate AuthenticationProvider,
        typically DaoAuthenticationProvider for username and password authentication.

    3.  Loading UserDetails: The AuthenticationProvider uses the UserDetailsService (your CustomUserDetailsService in this case)
        to load the UserDetails by username. The UserDetails include the encoded password that was previously stored in the database.

    4.  Password Verification: The AuthenticationProvider then takes the password provided in the UsernamePasswordAuthenticationToken,
        encodes it using the same PasswordEncoder, and compares it to the encoded password retrieved with the UserDetails.
        This comparison is secure because it uses the encoded forms, ensuring that plain text passwords are not used during
        the verification process.

    5.  Successful Authentication: If the encoded passwords match, and any additional checks (such as account non-expired,
        account non-locked, etc.) pass, the authentication is considered successful. The AuthenticationProvider returns a fully
        authenticated Authentication object, which is then stored in the SecurityContextHolder.
    */



}