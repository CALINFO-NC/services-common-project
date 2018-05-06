package com.calinfo.api.common.utils;

import com.calinfo.api.common.security.AbstractCommonPrincipal;
import com.calinfo.api.common.security.JwtUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.xml.bind.DatatypeConverter;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by dalexis on 20/11/2017.
 */
public class SecurityUtils {

    /**
     * Permet déviter que l'on puisse instancier une classe utilitaire
     */
    private SecurityUtils(){
    }

    /**
     * Permet de créer un token JWT
     *
     * @param privateKeyValue Valeur de la clée privée permettant de crypter le token
     * @param durationMillis Durée de validité du jeton. 0 ou null si valable indéfiniment
     * @param user Utilisateur et ses rôle à encoder
     * @return Token
     */
    public static String getJwtFromUser(String privateKeyValue, Long durationMillis, JwtUser user) throws NoSuchAlgorithmException, InvalidKeySpecException {

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKeyValue));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(spec);

        // Signature JWT utilisé pour encoder le jeton
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.RS512;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        Map<String, Object> mapClaims = new HashMap<>();
        mapClaims.put("login", user.getLogin());
        mapClaims.put("domain", user.getDomain());
        if (!user.getRolesApp().isEmpty()) {
            mapClaims.put("rolesApp", user.getRolesApp());
        }


        // Fabriquer le jeton JWT
        // Voir ici (https://www.iana.org/assignments/jwt/jwt.xhtml) les propriétés recomandé d'utiliser
        JwtBuilder builder = Jwts.builder()
                .setClaims(mapClaims)
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(now)  // iat : Date de construction du jeton
                .signWith(signatureAlgorithm, privateKey); // typ et alg : Type d'algorithm


        // Ajouter la durée de validité du jeton
        if (durationMillis != null) {
            long expMillis = nowMillis + durationMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }

        // Encoder le tenon JWT
        return builder.compact();

    }

    /**
     * Permet de décrypter le token JWT
     *
     * @param token Token à décrypter
     * @param publicKeyValue Clé public
     * @return Token décrypté
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static JwtUser getUserFromJwt(String token, String publicKeyValue) throws NoSuchAlgorithmException, InvalidKeySpecException {

        X509EncodedKeySpec spec = new X509EncodedKeySpec(DatatypeConverter.parseBase64Binary(publicKeyValue));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(spec);

        Claims claims = Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token).getBody();


        JwtUser tokenUser = new JwtUser();
        tokenUser.setLogin(claims.get("login", String.class));
        tokenUser.setDomain(claims.get("domain", String.class));

        Map<String, List<String>> rolesApp = (Map<String, List<String>>) claims.get("rolesApp", Map.class);

        if (rolesApp != null){
            tokenUser.setRolesApp(rolesApp);
        }

        return tokenUser;
    }

    // Utiliser le PrincipalManager
    @Deprecated
    public static AbstractCommonPrincipal getPrincipal(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null){
            return null;
        }

        return (AbstractCommonPrincipal)authentication.getPrincipal();
    }
}
