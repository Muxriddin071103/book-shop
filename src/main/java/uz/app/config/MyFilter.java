package uz.app.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class MyFilter extends OncePerRequestFilter {
    @Autowired
    @Lazy
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtProvider jwtProvider;

    @Lazy
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {

            String authorization = request.getHeader("Authorization");
            if (authorization == null || authorization.equals("annonumous")) {
                filterChain.doFilter(request, response);
                return;
            }
            if (authorization.startsWith("Bearer ")) {
                authorization = authorization.substring(7);
                String username = jwtProvider.getSubject(authorization);
                setAuthenticationToContext(username);
            }

        }catch (Exception e){
        }

        filterChain.doFilter(request, response);
    }

    public void setAuthenticationToContext(String  username){
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken authUser =new UsernamePasswordAuthenticationToken(
                userDetails,
                userDetails.isCredentialsNonExpired() &&
                        userDetails.isAccountNonExpired() &&
                        userDetails.isAccountNonLocked() &&
                        userDetails.isEnabled(),
                userDetails.getAuthorities()
        );
        SecurityContextHolder
                .getContext()
                .setAuthentication(authUser);
    }
}
