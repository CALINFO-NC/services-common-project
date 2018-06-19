package com.calinfo.api.common.manager;

/**
 * Created by dalexis on 20/06/2018.
 */
public interface ApiKeyManager {

    /**
     * Renvoie un nouveau token de l'utilisateur connecté. null si impossible de renvoyer
     * un nouveau token.
     * @param apiKey Clé de rafraichissement
     * @return Token d'authentification ou null
     */
    String refreshToken(String apiKey);
}
