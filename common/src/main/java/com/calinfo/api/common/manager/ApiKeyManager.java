package com.calinfo.api.common.manager;

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
