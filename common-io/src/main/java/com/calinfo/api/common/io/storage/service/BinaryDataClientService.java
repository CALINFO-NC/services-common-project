package com.calinfo.api.common.io.storage.service;

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

import java.io.InputStream;
import java.util.List;

public interface BinaryDataClientService {

    /**
     * @return Liste des identifiants des fichier à traiter. DomainCotext a été initialisé avant l'appel de cette méthode.
     */
    List<String> listId();

    /**
     *
     * @param id Identifiant obtenue grâce à la méthode {@link #listId()}
     * @return Un handle sur le fichier à transférer
     */
    InputStream startTransfert(String id);

    /**
     * Cette méthode est appelée à la fin du transfert du fichier
     *
     * @param id Identifiant obtenue grâce à la méthode {@link #listId()}
     * @param success true si le transfert s'est correctement terminé. false sinon
     */
    void finalizeTransfert(String id, boolean success);
}
