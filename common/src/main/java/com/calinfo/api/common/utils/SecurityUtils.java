package com.calinfo.api.common.utils;

/*-
 * #%L
 * common
 * %%
 * Copyright (C) 2019 - 2020 CALINFO
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.calinfo.api.common.ex.MessageStatusException;
import com.calinfo.api.common.security.AbstractCommonPrincipal;
import com.calinfo.api.common.security.CommonPrincipal;
import com.calinfo.api.common.security.JwtUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.bind.DatatypeConverter;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.util.stream.Collectors;

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
     * Retourne une instance de clé public via la chaine de caractère
     *
     * @param publicKeyValue Clé public sous forme de chaine de caractère
     * @return Instance de clé public
     * @throws NoSuchAlgorithmException Exception si l'algorithme utilisé pour décrypté la clé n'est pas bon
     * @throws InvalidKeySpecException Exception si la clé est incorrecte
     */
    public static PublicKey getPublicKeyFromString(String publicKeyValue) throws NoSuchAlgorithmException, InvalidKeySpecException {
        X509EncodedKeySpec spec = new X509EncodedKeySpec(DatatypeConverter.parseBase64Binary(publicKeyValue));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }

    /**
     * Retourne une instance de clé privée via la chaine de caractère
     *
     * @param privateKeyValue Clé privée sous forme de chaine de caractère
     * @return Instance de clé privée
     * @throws NoSuchAlgorithmException Exception si l'algorithme utilisé pour décrypté la clé n'est pas bon
     * @throws InvalidKeySpecException Exception si la clé est incorrecte
     */
    public static PrivateKey getPrivateKeyFromString(String privateKeyValue) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKeyValue));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(spec);
    }

    /**
     * Permet de crypter une donnée
     *
     * @param data Donnée à crypter
     * @param privateKey Clé privée
     * @return Donnée crypté
     * @throws NoSuchAlgorithmException Exception si l'algorithme utilisé pour décrypté la clé n'est pas bon
     * @throws InvalidKeySpecException Exception si la clé est incorrecte
     * @throws NoSuchPaddingException Problème pour encrypter la clé
     * @throws IllegalBlockSizeException Problème pour encrypter la clé
     * @throws BadPaddingException Problème pour encrypter la clé
     */
    public static byte[] rsaEncryption(byte[] data, PrivateKey privateKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

    /**
     * Permet de décrypter une donnée
     *
     * @param data Donnée à décrypter
     * @param publicKey Clé public
     * @return Donnée décrypté
     * @throws NoSuchAlgorithmException Exception si l'algorithme utilisé pour décrypté la clé n'est pas bon
     * @throws InvalidKeySpecException Exception si la clé est incorrecte
     * @throws NoSuchPaddingException Problème pour encrypter la clé
     * @throws IllegalBlockSizeException Problème pour encrypter la clé
     * @throws BadPaddingException Problème pour encrypter la clé
     */
    public static byte[] rsaDecryption(byte[] data, PublicKey publicKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        return cipher.doFinal(data);
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

        PrivateKey privateKey = getPrivateKeyFromString(privateKeyValue);

        // Signature JWT utilisé pour encoder le jeton
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.RS512;

        long nowMillis = DateUtils.now().toInstant().toEpochMilli();
        Date now = new Date(nowMillis);

        Map<String, Object> mapClaims = new HashMap<>();
        mapClaims.put("login", user.getLogin());
        mapClaims.put("domain", user.getDomain());
        if (!user.getRoles().isEmpty()) {
            mapClaims.put("rolesApp", user.getRoles());
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
     * @throws NoSuchAlgorithmException Exception si l'algorithme utilisé pour décrypté la clé n'est pas bon
     * @throws InvalidKeySpecException Exception si la clé est incorrecte
     */
    public static JwtUser getUserFromJwt(String token, String publicKeyValue) throws NoSuchAlgorithmException, InvalidKeySpecException {

        PublicKey publicKey = getPublicKeyFromString(publicKeyValue);

        Claims claims = Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token).getBody();


        JwtUser tokenUser = new JwtUser();
        tokenUser.setLogin(claims.get("login", String.class));
        tokenUser.setDomain(claims.get("domain", String.class));

        List<String> rolesApp = (List<String>) claims.get("rolesApp", List.class);

        if (rolesApp != null){
            tokenUser.setRoles(rolesApp);
        }

        return tokenUser;
    }

    /**
     * Retourne le principal associé au token
     *
     * @param token Token à décrypter
     * @param apiKey Clé d'api permettant de renouveller le token
     * @param publicKey Clé public
     * @return Le Principal
     */
    public static AbstractCommonPrincipal getPrincipalFromTokens(String token, String apiKey, String publicKey){

        JwtUser user;
        try{
            user = SecurityUtils.getUserFromJwt(token, publicKey);
        }
        catch(InvalidKeySpecException | NoSuchAlgorithmException e){
            throw new MessageStatusException(HttpStatus.UNAUTHORIZED, "JWT Expired or invalid");
        }

        List<String> lstRoles = user.getRoles();
        if (lstRoles == null){
            lstRoles = new ArrayList<>();
        }
        List<? extends GrantedAuthority> authorities = lstRoles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());

        // Création du principal
        return new CommonPrincipal(apiKey, token, user.getDomain(), user.getLogin(), "", authorities);
    }
}
