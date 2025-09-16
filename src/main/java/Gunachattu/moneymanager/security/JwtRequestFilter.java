package Gunachattu.moneymanager.security;

import Gunachattu.moneymanager.service.AppUserDetailsService;
import Gunachattu.moneymanager.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    private final AppUserDetailsService userDetailsService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        System.out.println("yes i am coming !!----------------------------------------------------------------------");


        final String authHeader = request.getHeader("Authorization");
        String email = null;
        String jwt = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            email = jwtUtil.extractUsername(jwt);
        }
//        System.out.println("Issued At: " + jwt.getIssuedAt());
//        System.out.println("Expiration: " + jwt.getExpiration());
        System.out.println(email);
        System.out.println(jwt);
        // Extract claims
//        Claims claims = jwtUtil.extractAllClaims(jwt);
//
//        // Log token times
//        System.out.println("Token Created Time: " + claims.getIssuedAt());
//        System.out.println("Token Expiry Time : " + claims.getExpiration());
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);
            if (jwtUtil.validateToken(jwt, userDetails)) {
                System.out.println("after validation i ma from jwt securtityv !!!!");
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                System.out.println(authToken+" auth tokemnn  nnn ");
            }
        }

        // VERY IMPORTANT — let the request proceed to the rest of the filter chain
        filterChain.doFilter(request, response);
    }

}
