# Description

 Les connecteurs sont des classe permettant de faire des opération (comme downloader ou uplooader) sur des support externe.
 Pour que le sous package *storage* du *common-io* sache écrire sur un support externe, il vous faudra implémenter le composant *BinaryDataConnector*.

```
import com.calinfo.api.common.io.storage.connector.BinaryDataConnector;
import org.springframework.stereotype.Component;

@Component
public class MyBinaryDataConnector implements BinaryDataConnector {

 ...
}

```

 NB : La classe *BinaryDataConnector* possède deux autres méthodes *createSpace* et *deleteSpace* permettant de crééer ou de supprimer un espace de stockage.
 On peu voir un espace de stockage un peu comme un schéma de base de données.

 Le sous package *storage* du *common-io* apporte plusieurs implémentation de *BinaryDataConnector*

# Connecteur *Google cloud*

 Ce conencteur permet d'envoyer les données binaire dans un *bucket* du *google cloud*. Pour utiliser ce connecteur, il vous suffit d'ajouter dans le fichier
 de configuration (ex : application.yml) les éléments suivants :

```

common-io:
  storage:
    connector:
      provider: google
      configuration: # Voir les propriétés de la classe GoogleConfigProperties

```

 Voici un exemple de configuration

```

common-io:
  storage:
    connector:
      provider: google
      configuration:
        projectId: "services-dev-jeb"
        credentials: >
          {
            "type": "service_account",
            "project_id": "services-dev-jeb",
            "private_key_id": "31f5f121afeec4dc7f38230b50e5234dab366639",
            "private_key": "-----BEGIN PRIVATE KEY-----\nMIIEvgIBADANBgkqhkiG9w0BAQEFBBSCBKgwggSkAgEAAoIBAQCj7XLnMHAGf3/j\nURs5f+nm0dWyKeFnrS8H/zRMeOLmiZn7/RH2X7KdPo3dPYwSNE7ZEGup6AWnl7MN\nmsSIL4kurp3E24ucF6pRP0XRl8HLGrORFIb4NBw2vAXD1A37uVUqxtRaYamzA2x5\nPbA+YjtpFDS2AKP0Q9/9r6RyKu6vOsit/eePYRKT2YTNhT0Xu2xnUzZ0I4n2BLBQ\nMOpnXyTrp8bcqPIthyUzYR0bnRlxHF2YPADn1z4TtflxKzVwXstBT/WVMupyjcBN\n7Bwz29WfqIlky2e+4vrxYrVvsNdW75rK7DTCTyna/GaFKNcwRrVQ6sZnhxYXsCs+\n+w6U1WxXAgMBAAECggEABFF83GcarqpIUS0ayzVnkP7oGcz7NPnUhcVDuG5IhjAw\nWUKZRGT1Q3LijPh2zyf1UOyI0GRL541RDzFm97JOl9TwU+P3jNPUe3smyvti3/VC\n07ZJuH/YEUh7kmU10IUQDW9tcYJc1H6N4WfAeGHzgmtFaw+yHWq/LfXJjDarUPxd\npTKhrupRy2flyN6wEPExRGUz+xi8T2UowU/VrGqvzZFx000aZ4YVclRWUO+N5A0s\nRdbS3Hx9G4Tq2ZUHuSmP66Eak4e3NrZciySixJ1upUwHrBdJ+HvBKa8ds3i0NnrO\niQd6EQfti/93owKeHoYWIt4bOQCc+bwin1XKH3clGQKBgQDUAQLleHaymTNKpxOp\nmJgmNYBdAggVX8GlnNo9J3Sr/aNRsXhF2yTBxSog6VyXY4Q1+VbtBILJqpSi2DvR\n5tLUnx/XrOJBQfvcW9RXtNB4Be2Kg1pfAiwluJfD7tZV4Nkct12NUjCi1FZMkdKv\nnFiZcE5iSErQ1JaPWwu9F99v+wKBgQDF8lCQXC84o91cNHtPC3W7Gk3TfbyOups6\nCfRNwrDB+WtvUURf+BI2zLP/dd33yOMzkPaOUuSKRUjPwVlzWpVbRKOT3jBEa8Fj\nRGJuPjx44eLuQUfNZOhpcJctiMz+xooAshdV6bZOAvX5YnKQOTbGjtQh0snByhWt\n7q1e0SVaVQKBgCGkh3EizLNK3HjcSqJ/NKXbl6Mqz12U9IXzfi52NG2WsnQkVZHA\nVPTq9OSEI81iXXizOLgkHx0hlLTC27tTheF33vW62azBa9ZsPCYu62YgirGQZqbt\nEVRrFqphHGJEbC/CaXYjtNQiHg/IlEaJ6QVwbP/ruPOqyLm3GQXI5AxBAoGBAKrE\n6MIR9V8c50yzrim8Tj4zbC7ny7Mqw93nVo97RfiiUBBCAQX2Quhp42OhcPRip7gF\n+N9CHg43xaAOQzhkTnPlnGVmCygL+lPEXFKVeKAk6Bz5zpMg2eyVCKds3MVzzPza\np40jynY00bXrO8C2y02zTMk9S3fW+qsKPSGOt3XdAoGBAL2Bbu/Ycj0cTUu/pO8f\nAvTa2eI0sBNWeXWMTB5NY42LW02xjb/gd64q/QuMKb0AR1AIlCkB/l0HXSmh35PP\no1OL5EfwRc/BcWD8FrfmTYBuhU3BOb8qlEm/nIuINuXnYzXpln4EmRk+IXjBLdE7\nOklz+wPk1O+SeXha9ntt4WrG\n-----END PRIVATE KEY-----\n",
            "client_email": "calinfo-default-dev-bucket@services-dev-jeb.iam.gserviceaccount.com",
            "client_id": "100439927192752185861",
            "auth_uri": "https://accounts.google.com/o/oauth2/auth",
            "token_uri": "https://oauth2.googleapis.com/token",
            "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
            "client_x509_cert_url": "https://www.googleapis.com/robot/v1/metadata/x509/calinfo-default-dev-bucket%40services-dev-jeb.iam.gserviceaccount.com"
          }
        buckatName: "test_bucket_calinfo"

```

# Connecteur *mémoire*

 Il existe aussi un connecteur permettant de transférer les fichiers en mémoire cache de java, et ce jusqu'a ce que l'application soit éteinte.
 Ce connecteur peut servir principalement pour lancer tests unitaires. Pour utiliser ce connecteur, il vous suffit d'ajouter dans le fichier
 de configuration (ex : application.yml) les éléments suivants :

```

common-io:
  storage:
    connector:
      provider: mem

```

# Connecteur *FileSystem*

 Ce connecteur permet de stocker les données binaires dans un système de fichier. Pour utiliser ce connecteur, il vous suffit d'ajouter dans le fichier
 de configuration (ex : application.yml) les éléments suivants :

```

common-io:
  storage:
    connector:
      provider: file
      configuration: # Voir les propriétés de la classe FileConfigProperties

```

 Voici un exemple de configuration

```

common-io:
  storage:
    connector:
      provider: file
      configuration: "/foo/bar"

```

# Connecteur *Ftp*

 Ce connecteur permet de stocker les données binaires sur un FTP (non sécurisé). Pour utiliser ce connecteur, il vous suffit d'ajouter dans le fichier
 de configuration (ex : application.yml) les éléments suivants :

```

common-io:
  storage:
    connector:
      provider: ftp
      configuration: # Voir les propriétés de la classe FtpConfigProperties

```

 Voici un exemple de configuration

```

common-io:
  storage:
    connector:
      provider: ftp
      configuration:
        host: "localhost"
        port: 21
        username: "admin"
        password: "password"
        path: "/test"

```