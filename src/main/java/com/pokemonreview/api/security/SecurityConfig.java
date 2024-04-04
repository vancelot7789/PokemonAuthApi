package com.pokemonreview.api.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig{

    private JwtAuthEntryPoint jwtAuthEntryPoint;

    private CustomUserDetailsService customUserDetailsService;



    @Autowired
    public SecurityConfig(CustomUserDetailsService customUserDetailsService, JwtAuthEntryPoint jwtAuthEntryPoint) {
        this.jwtAuthEntryPoint = jwtAuthEntryPoint;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws  Exception{
        http
                .csrf().disable()

                /*JWT*/
                .exceptionHandling()
                // The AuthenticationEntryPoint is used to handle what should happen when an unauthenticated request accesses a secured resource.
                // It kicks in when the request requires authentication but the user is not authenticated.
                .authenticationEntryPoint(jwtAuthEntryPoint)
                .and()
                .sessionManagement()
                // since using jwt we have to disable session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                /*JWT*/

                .and()
                .authorizeRequests()
                .antMatchers("/api/auth/**").permitAll()
                .antMatchers("/api/pokemons/**").hasAuthority("USER") // Adjust according to actual role names

                /*
                Here's a step-by-step explanation of what happens:

                1.  Request Matching: For each incoming HTTP request, Spring Security checks
                    if it matches any of the patterns defined in your security configuration with antMatchers or similar methods.


                2.  Authority Check: If the request matches the /api/pokemons/** pattern, Spring Security then checks the
                    Authentication object stored in the SecurityContextHolder to determine if the current user has the required authority ("USER" in this case).


                3.  Role vs. Authority: It's important to distinguish between roles and authorities within Spring Security.
                    Roles are considered a subset of authorities. By convention, roles have a prefix (usually "ROLE_"),
                    but when you're using .hasAuthority("USER"), you're checking for an authority directly.
                    If your configuration or UserDetails service assigns roles with the "ROLE_" prefix,
                    you would use .hasRole("USER") (without the "ROLE_" prefix in the argument).


                4.  Access Control Decision:

                    If the Authentication object contains the required authority, the request is allowed to proceed to the controller method.
                    If the Authentication object does not contain the required authority, or if there is no Authentication object
                    in the SecurityContextHolder (indicating the user is not authenticated), Spring Security will reject the request.
                    The default behavior is to send a 403 Forbidden response, although you can customize this behavior.

                5.  SecurityContextHolder: The SecurityContextHolder is the mechanism by which Spring Security makes the current
                    security context (including the Authentication object) available to various parts of the application.
                    The Authentication object includes details about the current user, including their authorities.

                */

                .anyRequest().authenticated()

                //  configures your application to allow all users,
                //  whether authenticated or not, to access any resources available via GET requests.
                //  you can also filter roles like.antMatchers(HttpMethod.GET).hasAnyRole(<role name>)

                .and()
                .httpBasic();
        /*JWT*/

        // jwtAuthenticationFilter() its main role is to intercept incoming requests,
        // extract the JWT token from the request headers, validate it,
        // and set the authentication in the SecurityContextHolder if the token is valid.
        // This effectively authenticates the user for the duration of the request.

        // If the JWT token is valid, it allows the request to proceed with the user authenticated.
        // If the token is invalid (e.g., expired, malformed), it does not authenticate the user,
        // but it also doesn’t stop the request from going through the filter chain.
        // It’s more about authentication rather than handling what happens when authentication fails.
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        /*JWT*/

        return http.build();
    }


    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception{

        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JWTAuthenticationFilter jwtAuthenticationFilter(){
        return new JWTAuthenticationFilter();
    }

}