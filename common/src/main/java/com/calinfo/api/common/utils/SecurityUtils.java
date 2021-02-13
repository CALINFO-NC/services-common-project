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

import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.bind.DatatypeConverter;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

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
}
