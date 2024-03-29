package com.calinfo.api.common.io.storage.connector;

/*-
 * #%L
 * common-io
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Future;

/**
 * Cette interface doit pouvoir être injecté. Donc la classe implémentant cette interface
 * doit être un composant, ou un service, ou ...
 */
public interface BinaryDataConnector {

    /**
     *
     * @param spaceName Domain (ou multi tenant). Peut être à null
     * @param id Identifiant du fichier
     * @param in Contenue du fichier à downloader
     * @throws IOException Exception d'entrée/sortie
     */
    void upload(String spaceName, String id, InputStream in) throws IOException;

    /**
     *
     * @param spaceName Domain (ou multi tenant). Peut être à null
     * @param id Identifiant du fichier
     * @param out Déversseur du fichier uploader
     * @throws IOException Exception d'entrée/sortie
     */
    void download(String spaceName, String id, OutputStream out) throws IOException;

    /**
     * Permet de supprimer un fichier. Ce service doit être asynchrone
     *
     * @param spaceName Domain (ou multi tenant). Peut être à null
     * @param id Identifiant du fichier
     * @return true si le fichier à été supprimer, false sinon.
     * @throws IOException Exception d'entrée/sortie
     */
    Future<Boolean> delete(String spaceName, String id) throws IOException;

    /**
     *
     * Créé un espace de stockage. Ce service doit être asynchrone
     *
     * @param spaceName Nom de l'espace de stockage (peut être un domain)
     * @return true si l'espace à été créé
     * @throws IOException Exception d'entrée/sortie
     */
    Future<Boolean> createSpace(String spaceName) throws IOException;

    /**
     * Vérifie si un espace existe
     *
     * @param spaceName Nom de l'espace de stockage (peut être un domain)
     * @return true si l'espace existe
     * @throws IOException Exception d'entrée/sortie
     */
    boolean isSpaceExist(String spaceName) throws IOException;

    /**
     * Supprime u nespace de stockage et tou son contenue. Ce service doit être asynchrone
     *
     * @param spaceName Nom de l'espace de sotckage
     * @return true si supprimer false sinon
     * @throws IOException Exception d'entrée/sortie
     */
    Future<Boolean> deleteSpace(String spaceName) throws IOException;
}
