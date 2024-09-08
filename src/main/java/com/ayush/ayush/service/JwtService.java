package com.ayush.ayush.service;

import com.ayush.ayush.model.Customer;
import com.ayush.ayush.model.Seller;
import com.ayush.ayush.model.TokenCustomer;
import com.ayush.ayush.model.TokenSeller;
import com.ayush.ayush.model.embeddedable.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

    private static final String BEARER = "Bearer ";

    private final String secret;

    private final Long accessExpiration;

    private final Long refreshExpiration;

    private final TokenCustomerRepository tokenCustomerRepository;
    private final TokenSellerRepository tokenSellerRepository;
    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.access}") Long accessExpiration,
                      @Value("${app.jwt.refresh}") Long refreshExpiration,
                      TokenCustomerRepository tokenCustomerRepository,
                      TokenSellerRepository tokenSellerRepository) {
        this.secret = secret;
        this.accessExpiration = accessExpiration;
        this.refreshExpiration = refreshExpiration;
        this.tokenCustomerRepository = tokenCustomerRepository;
        this.tokenSellerRepository = tokenSellerRepository;
    }
    public String generateAccessJwtSeller(Seller seller){
        return generateJwt(accessExpiration,seller.getId(),seller.getRole().ordinal());
    }
    public String generateRefreshJwtSeller(Seller seller){
        return generateJwt(refreshExpiration,seller.getId(),seller.getRole().ordinal());
    }
    public String generateAccessJwtCustomer(Customer customer){
        return generateJwt(accessExpiration,customer.getId(),customer.getRole().ordinal());
    }
    public String generateRefreshJwtCustomer(Customer customer){
        return generateJwt(refreshExpiration,customer.getId(),customer.getRole().ordinal());
    }

    public boolean isValidToken(String jwt){
        Long role = getRoleClaim(jwt);
        Long id = getIdClaim(jwt);
        if (role == Role.SELLER.ordinal()){
            TokenSeller token =  tokenSellerRepository.findByJwt(jwt).orElseThrow(RuntimeException::new);
            return !token.isRevoked();
        }
        TokenCustomer token = tokenCustomerRepository.findByJwt(jwt).orElseThrow(RuntimeException::new);
        return !token.isRevoked();
    }
    //01234567
    //BEARER s
    public String extractJwtFromReq(HttpServletRequest request){
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader!=null&&authHeader.length()>=8 && authHeader.startsWith(BEARER)){
            return authHeader.substring(7);
        }
        return null;
    }
    public void saveAllTokensSeller(List<TokenSeller> list){
        tokenSellerRepository.saveAll(list);
    }
    public Long getIdClaim(String jwt){
        return getAllClaims(jwt).get("id", Long.class);
    }
    public Long getRoleClaim(String jwt){
        return getAllClaims(jwt).get("role", Long.class);
    }
    public void revokeAllTokensForSeller(Seller seller){
        tokenSellerRepository.revokeAllTokensBySellerId(seller.getId());
    }
    public void revokeAllTokenForCustomer(Customer customer){
        tokenCustomerRepository.revokeAllTokensByCustomerId(customer.getId());
    }
    private Claims getAllClaims(String jwt){
        return Jwts.parserBuilder()
                .setSigningKey(secretKey())
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }

    private String generateJwt(Long exp, Long id, Integer role){
        Long iat = System.currentTimeMillis();
        return Jwts.builder()
                .claim("id", id)
                .setSubject("user")
                .claim("role", role)
                .setIssuedAt(new Date(iat))
                .setExpiration(new Date(iat + exp))
                .signWith(secretKey())
                .compact();
    }
    private Key secretKey(){
        byte[] key = Decoders.BASE64URL.decode(secret);
        return Keys.hmacShaKeyFor(key);
    }

}
