package com.calinfo.api.common.utils;

/*-
 * #%L
 * common
 * %%
 * Copyright (C) 2019 - 2023 CALINFO
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

import com.calinfo.api.common.security.keycloak.KeycloakUtils;
import com.calinfo.api.common.task.TaskPrincipal;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
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
        X509EncodedKeySpec spec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyValue));
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
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyValue));
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
     * @throws InvalidKeyException Exception si la clé est incorrecte
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
     * @throws InvalidKeyException Exception si la clé est incorrecte
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
     * Permet d'extraire les roles sous forme de liste de String depuis le SecurityContext
     * @return Liste des roles
     */
    public static List<String> getRolesFromSecurityContext(){
        SecurityContext context = SecurityContextHolder.getContext();

        if (context == null){
            return Collections.emptyList();
        }

        Authentication authentication = context.getAuthentication();

        if (authentication == null){
            return Collections.emptyList();
        }

        Collection<? extends GrantedAuthority> grantedAuthorities = authentication.getAuthorities();

        if (grantedAuthorities == null){
            return Collections.emptyList();
        }

        return grantedAuthorities.stream().map(g -> g.getAuthority()).collect(Collectors.toList());
    }

    /**
     * Retourne le nom de l'utilisateur connecté
     *
     * @return Nom de l'utilisateur connecté
     */
    public static String getUsernameFromSecurityContext(){

        SecurityContext context = SecurityContextHolder.getContext();

        if (context == null){
            return null;
        }

        Authentication authentication = context.getAuthentication();

        if (authentication == null){
            return null;
        }

        return authentication.getName();
    }

    /**
     * Retourne true ou false en fonction de si l'utilisateur est connecté
     *
     * @return true si l'utilisateur est connecté
     */
    public static boolean isUserConnected(){
        String login = getUsernameFromSecurityContext();
        if (login != null){
            login = login.trim();
        }

        return !StringUtils.isAllBlank(login) && !Objects.equals(login, KeycloakUtils.ANONYMOUS_USER_NAME);
    }
}
