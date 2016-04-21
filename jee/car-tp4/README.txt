=================
CAR TP4 - Java EE
=================

Cette archive contient la version 1.7.2 de Apache TomEE
<http://tomee.apache.org>, un serveur Java EE 6 basé sur Tomcat, OpenEJB et
HSQLDB.

Utiliser la commande suivante pour lancer Tomcat :

    ./apache-tomee/bin/catalina.sh run


Les fichiers de configuration .project et .classpath permettent d'importer le
projet dans Eclipse. Si vous utilisez un autre environnement de développement,
il faut configurer le projet avec les répertoires suivants :

* répertoire source pour les fichiers Java : src/main/java/
* répertoire source pour les fichiers de configuration : src/main/resources/
* répertoire source pour les fichiers HTML et JSP : apache-tomee/webapps/ROOT/
* librairies : lib/javaee-api-6.0.6.jar
* répertoire cible pour la compilation : apache-tomee/webapps/ROOT/WEB-INF/classes/


HSQLDB utilise le répertoire apache-tomee/data/ pour le stockage des données
persistentes. En cas de problème, ce répertoire peut être supprimé. Il est alors
re-créé et ré-initialisé automatiquement lors du lancement suivant du serveur.


Lionel Seinturier.
15 octobre 2015.
