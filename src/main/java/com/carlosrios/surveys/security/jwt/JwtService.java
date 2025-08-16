package com.carlosrios.surveys.security.jwt;

import com.carlosrios.surveys.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${api.security.secret}")
    private String SECRET_KEY;

    // method to return the JWT token
    // through the use of a hashmap we can pass additional information in the token
    public String getToken(User user) {
        return createToken(new HashMap<>(), user);
    }

    private String createToken(Map<String, Object> extraClaims, User user) {
        return Jwts
                .builder()
                .claims(extraClaims) // additional data to be passed in the token
                .claim("userId", user.getId())
                .subject(user.getUsername())
                .issuedAt(new java.util.Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000*60*24)) // token valid for 24 minutes
                .signWith(getKey()) // sign the token with our secret key
                .compact();
    }

    private SecretKey getKey() {
    byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY);
    return Keys.hmacShaKeyFor(keyBytes); // create a new instance of our secret key compatible with the hs256 algorithm
    }

    //validates the token checking if the username in the token matches the user details and if the token has not expired
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // method to extract the username from the token
    public String getUsernameFromToken(String token) {
        return getClaim(token, Claims::getSubject); // especificamos que necesitamos extraer el subject del token
    }

    // claims are extra information stored in the token
    private Claims getClaimsFromToken(String token) {
        return Jwts
                .parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Function<Claims, T> claimsResolver: takes value of type Claims and returns a value of type T
    // define which claim we want to extract from the token
    public <T> T getClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Boolean isTokenExpired(String token) {
        return getExpiration(token).before(new Date());
    }

     //extracts the expiration date from the token with the <T> T getClaim method
    public Date getExpiration(String token) {
        return getClaim(token, Claims::getExpiration);
    }

}
