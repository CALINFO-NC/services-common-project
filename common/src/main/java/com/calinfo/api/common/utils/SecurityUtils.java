package com.calinfo.api.common.utils;

/*-
 * #%L
 * common
 * %%
 * Copyright (C) 2019 - 2021 CALINFO
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

import com.calinfo.api.common.task.TaskPrincipal;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.keycloak.representations.AccessToken;
import org.springframework.security.core.GrantedAuthority;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Set;
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
        //X509EncodedKeySpec spec = new X509EncodedKeySpec(DatatypeConverter.parseBase64Binary(publicKeyValue));
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
     * Extrait les roles du principal
     *
     * @param intPrincipal
     * @return List des rôles associé au principal
     */
    public static List<String> getRoleFormPrincipal(Principal intPrincipal){

        List<String> result = new ArrayList<>();

        if (intPrincipal instanceof KeycloakPrincipal){
            return getRoleFromKecloakPrincipal((KeycloakPrincipal<? extends KeycloakSecurityContext>) intPrincipal);
        }

        if (intPrincipal instanceof TaskPrincipal){
            return getRoleFromTaskPrincipal((TaskPrincipal) intPrincipal);
        }

        return result;
    }

    private static List<String> getRoleFromTaskPrincipal(TaskPrincipal principal){
        return principal.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
    }

    private static List<String> getRoleFromKecloakPrincipal(KeycloakPrincipal<? extends KeycloakSecurityContext> principal){

        List<String> result = new ArrayList<>();
        result.addAll(getRealmRoleFromKecloakPrincipal(principal));
        result.addAll(getResourceRoleFromKecloakPrincipal(principal));

        return  result;
    }

    private static List<String> getResourceRoleFromKecloakPrincipal(KeycloakPrincipal<? extends KeycloakSecurityContext> principal){

        List<String> result = new ArrayList<>();


        if (!(principal.getKeycloakSecurityContext() instanceof RefreshableKeycloakSecurityContext)){
            return result;
        }

        RefreshableKeycloakSecurityContext context = ((RefreshableKeycloakSecurityContext)principal.getKeycloakSecurityContext());

        KeycloakDeployment deployment = context.getDeployment();
        if (deployment == null){
            return result;
        }

        AccessToken accessToken = context.getToken();
        if (accessToken == null){
            return result;
        }


        String resourceName = deployment.getResourceName();
        AccessToken.Access resourceAccess = accessToken.getResourceAccess().get(resourceName);
        if (resourceAccess == null){
            return result;
        }

        Set<String> roles = resourceAccess.getRoles();
        if (roles != null){
            result.addAll(roles.stream().collect(Collectors.toList()));
        }

        return  result;
    }

    private static List<String> getRealmRoleFromKecloakPrincipal(KeycloakPrincipal<? extends KeycloakSecurityContext> principal){

        List<String> result = new ArrayList<>();

        AccessToken accessToken = principal.getKeycloakSecurityContext().getToken();
        if (accessToken == null){
            return result;
        }

        AccessToken.Access realmAccess = accessToken.getRealmAccess();
        if (realmAccess == null){
            return result;
        }

        Set<String> roles = realmAccess.getRoles();
        if (roles != null){
            result.addAll(roles.stream().collect(Collectors.toList()));
        }

        return  result;
    }
}
