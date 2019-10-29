package com.calinfo.api.common.io;

import com.calinfo.api.common.io.storage.connector.google.GoogleBinaryDataConnector;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

import java.io.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("google")
public class MyTest extends AbstractTestNGSpringContextTests {


    @Autowired
    private GoogleBinaryDataConnector googleBinaryDataConnector;

    /**
     * Pour faire passer ce test, il faut mettre le bon paramétrage dans le fichier application-google.yml
     * 
     * @throws Exception
     */
    @Ignore("Ce test est ignoré car il ne peut pas passé en TU. On est dépendant de la plateforme google pour vérifier son bon fonctionnement")
    @Test
    public void call() throws Exception{

        // Ajout de fichier dans différent espace
        System.out.println("--- Création fichier --------");

        upload("toto", "id1", "AZERT");
        upload("toto", "id2", "1234");
        upload("toto", "id3", "1234AZERT");
        upload(null, "id1", ",;:=azaza");
        upload(null, "id2", ",;:=3997");
        upload(null, "id3", "azae&@é");

        // Lecture et affichage à la console de ces fichier récupéré de ces différents espaces
        System.out.println("--- Affichage ---------------");

        print("toto", "id1");
        print("toto", "id2");
        print("toto", "id3");
        print(null, "id1");
        print(null, "id2");
        print(null, "id3");

        // On supprime des fichiers dans ces différents espaces
        System.out.println("--- Supression fichier-------");

        googleBinaryDataConnector.delete("toto", "id1");
        googleBinaryDataConnector.delete(null, "id1");

        // Lecture et affichage à la console de ces fichier récupéré de ces différents espaces (null c'est que le fichier n'existe plus)
        print("toto", "id1");
        print("toto", "id2");
        print("toto", "id3");
        print(null, "id1");
        print(null, "id2");
        print(null, "id3");

        // Supression des espaces
        System.out.println("--- Supression espace -------");

        googleBinaryDataConnector.deleteSpace("toto");
        googleBinaryDataConnector.deleteSpace(null);

        // Lecture et affichage à la console de ces fichier récupéré de ces différents espaces (null c'est que le fichier n'existe plus)
        System.out.println("--- Affichage ---------------");

        print("toto", "id1");
        print("toto", "id2");
        print("toto", "id3");
        print(null, "id1");
        print(null, "id2");
        print(null, "id3");


    }

    private void upload(String spaceName, String id, String data) throws IOException {

        try (OutputStream out = googleBinaryDataConnector.getOutputStream(spaceName, id);
             InputStream in = new ByteArrayInputStream(data.getBytes())) {

            IOUtils.copy(in, out);
        }
    }

    private void print(String spaceName, String id) throws IOException{

        try (InputStream in = googleBinaryDataConnector.getInputStream(spaceName, id);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            String val = null;
            if (in != null){
                IOUtils.copy(in, out);

                val = new String(out.toByteArray());
            }

            System.out.println("-----------------------------");
            System.out.println(val);
            System.out.println("-----------------------------");
        }
    }
}