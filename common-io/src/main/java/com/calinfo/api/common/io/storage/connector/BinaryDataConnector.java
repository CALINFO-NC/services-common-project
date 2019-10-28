package com.calinfo.api.common.io.storage.connector;

/*-
 * #%L
 * common-io
 * %%
 * Copyright (C) 2019 CALINFO
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Cette interface doit pouvoir être injecté. Donc la classe implémentant cette interface
 * doit être un composant, ou un service, ou ...
 */
public interface BinaryDataConnector {

    OutputStream getOutputStream(String spaceName, String id) throws IOException;

    InputStream getInputStream(String spaceName, String id) throws IOException;

    /**
     * Permet de supprimer un fichier
     *
     * @param spaceName Domain (ou multi tenant). Peut être à null
     * @param id Identifiant du fichier
     * @return true si le fichier à été supprimer, false sinon.
     * @throws IOException
     */
    boolean delete(String spaceName, String id) throws IOException;

    /**
     *
     * Créé un espace de stockage.
     * @param spaceName Nom de l'espace de stockage (peut être un domain)
     * @return true si l'espace à été créé
     * @throws IOException
     */
    boolean createSpace(String spaceName) throws IOException;

    /**
     * Supprime u nespace de stockage et tou son contenue
     *
     * @param spaceName Nom de l'espace de sotckage
     * @return true si supprimer false sinon
     * @throws IOException
     */
    boolean deleteSpace(String spaceName) throws IOException;
}
