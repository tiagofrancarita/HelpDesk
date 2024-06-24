package br.com.franca.helpdesk.security;

import br.com.franca.helpdesk.exceptions.ObjectnotFoundException;
import br.com.franca.helpdesk.usecases.ChamadoUseCase;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    private Logger log = LoggerFactory.getLogger(ChamadoUseCase.class);

    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.secret}")
    private String secret;

     public String generateToken(String email) {
         return Jwts.builder()
                 .setSubject(email)
                 .signWith(SignatureAlgorithm.HS512, secret.getBytes())
                 .setExpiration(new Date(System.currentTimeMillis() + expiration ))
                 .compact();

     }

    public boolean tokenValido(String token) {
        log.info("Iniciando validação do token");
        Claims claims = getClaims(token);
        if (claims != null) {
            String username = claims.getSubject();
            Date expirationDate = claims.getExpiration();
            Date now = new Date(System.currentTimeMillis());
            if (username != null && expirationDate != null && now.before(expirationDate)) {
                log.info("Token validado com sucesso.");
                return true;
            }
        }
        log.error("Token inválido");
        throw new ObjectnotFoundException("Token inválido");
    }

    private Claims getClaims(String token) {

         try {

             return Jwts.parser().setSigningKey(secret.getBytes()).parseClaimsJws(token).getBody();

         }catch (Exception e) {
             return null;
         }
     }


    public String getUsername(String token) {

        Claims claims = getClaims(token);
        if (claims != null) {
            return claims.getSubject();
        }
        return null;
    }
}