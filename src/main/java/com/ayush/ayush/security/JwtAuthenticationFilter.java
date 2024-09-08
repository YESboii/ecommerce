package com.ayush.ayush.security;


import com.ayush.ayush.model.embeddedable.Role;
import com.ayush.ayush.repository.CustomerRepository;
import com.ayush.ayush.repository.SellerRepositoryJpa;
import com.ayush.ayush.service.JwtService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final SellerRepositoryJpa sellerRepository;
    private final CustomerRepository customerRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String jwt = jwtService.extractJwtFromReq(request);
        System.out.println(jwt);
        if (jwt==null){
            filterChain.doFilter(request, response);
            return;
        }
        try {
            boolean isValid = jwtService.isValidToken(jwt);
            System.out.println(isValid);
            if (isValid){
                UsernamePasswordAuthenticationToken authentication = null;
                UserDetails user = null;

                Long id = jwtService.getIdClaim(jwt);
                Long role = jwtService.getRoleClaim(jwt);
                System.out.println(id + " " + role);
                if (role== Role.SELLER.ordinal()){
                    user = sellerRepository.findById(id).orElseThrow(
                            () -> new EntityNotFoundException("Seller with id::%s does not exist".formatted(id))
                    );
                    System.out.println(id+" " + user.getUsername());
                    authentication = new SellerAuthenticationToken(user, null, user.getAuthorities());
                }
                else {
                    user = customerRepository.findById(id).orElseThrow(
                            () -> new EntityNotFoundException("Customer with id::%s does not exist".formatted(id))
                    );
                    authentication = new CustomerAuthenticationToken(user, null, user.getAuthorities());
                }
                WebAuthenticationDetails details = new WebAuthenticationDetailsSource().buildDetails(request);
                authentication.setDetails(details);

                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(authentication);
                SecurityContextHolder.setContext(context);
            }
            filterChain.doFilter(request, response);
        }catch (Exception e){
            //catch and rethrow subclasses of Authentication Exception to delegate it to the
            //Exception Translation Filter
            System.out.println(e.getClass());
        }
    }
}
