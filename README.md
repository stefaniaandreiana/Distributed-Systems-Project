In acest proiect am creat un Rest Api in Javalin (portul 7000), si totodata am incarcat imaginea in Topic.
Am trimis, ca JMS Publicator (JNDI), poza in format JSON(baza 64) catre Client prin intermediul brokerului.
Dupa ce imaginea a ajuns la client, am impartit-o in doua si am trimis-o catre cele 2 servere RMI(portul 1099 pentru c04 si 1100 pentru c05) si am aplicat ZoomIn pentru ambele jumatati de imagine;
La final, am creat un cod in care am unit la loc imaginea.
In containerul c06 am creat o tabela prin care am salvat imaginea ca BLOB. Tot in c06, am creat doua Rest Api-uri: primul (restApiDB.js) permite gestionarea de date SNMP și
încărcarea de imagini într-o bază de date, iar al doilea (restApiWebAcces.js) definește un server web care folosește Express, MySQL și WebSocket pentru a
gestiona imaginea(cu zoom aplicat) și a trimite notificări către client.
La crearea frontend-ului am folosit HTML+CSS+JavaScript.


#Fisierele de tip text(Notepad) se salveaza cu extensia .bat sau .sh pentru a porni containerele.

#Rulare broker (container02)
cd dadc/project/src/
ifconfig
(Compilare:/opt/software/java/jdks/jdk-21.0.2/bin/javac -cp .:/opt/software/apache-tomee-plume-10.0.0-M3/lib/* apachetomeejms/JMSBrokerStart_JakartaTomEE.java)
/opt/software/java/jdks/jdk-21.0.2/bin/java -cp .:/opt/software/apache-tomee-plume-10.0.0-M3/lib/* apachetomeejms.JMSBrokerStart_JakartaTomEE 172.17.0.2(sau adresa IP curenta) 61617

#Rulare publicator(container01)
cd dad/project/src/
/opt/software/java/jdks/jdk-21.0.2/bin/java -jar Javalinc1.jar 
(Compilare:/opt/software/java/jdks/jdk-21.0.2/bin/javac -cp .:/opt/software/apache-tomee-plume-10.0.0-M3/lib/* apachetomeejms/PublicatorJMS.java)
/opt/software/java/jdks/jdk-21.0.2/bin/java -cp .:/opt/software/apache-tomee-plume-10.0.0-M3/lib/* apachetomeejms.PublicatorJMS 172.17.0.2(sau adresa IP curenta a brokerului) 61617

#Rulare client(container03)
cd dad/project/src/
(Compilare:/opt/software/java/jdks/jdk-21.0.2/bin/javac -cp .:/opt/software/apache-tomee-plume-10.0.0-M3/lib/* apachetomeejms/JMSClient.java)
/opt/software/java/jdks/jdk-21.0.2/bin/java -cp .:/opt/software/apache-tomee-plume-10.0.0-M3/lib/* apachetomeejms.JMSClient tcp://172.17.0.2(sau adresa IP curenta a brokerului):61617 jms/topic/test

#Rulare Serverc04(container04)
cd dadc/project/src/
(Compilare: /opt/software/java/jdks/jdk-21.0.2/bin/javac rmi_package/ImageServer.java)
/opt/software/java/jdks/jdk-21.0.2/bin/java rmi_package.ImageServer

#Rulare Serverc05(container05)
cd dadc/project/src/
(Compilare: /opt/software/java/jdks/jdk-21.0.2/bin/javac rmi_package/ImageServer.java)
/opt/software/java/jdks/jdk-21.0.2/bin/java rmi_package.ImageServer
