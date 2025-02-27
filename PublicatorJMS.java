package apachetomeejms;

import jakarta.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.*;
import java.util.Base64;
import java.util.Properties;

public class PublicatorJMS {
	 public static Properties getProp(String ip, String port) {
		 Properties props = new Properties();
		 props.setProperty(Context.INITIAL_CONTEXT_FACTORY,
		 "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
	         props.setProperty(Context.PROVIDER_URL, "tcp://"+ip+":"+port);
		 return props;
	 }
    public static void main(String[] args) {
        try {
            InitialContext jndiContext = new InitialContext(getProp(args[0],args[1]));
            ConnectionFactory connectionFactory = (ConnectionFactory) jndiContext.lookup("ConnectionFactory");
            Connection connection = connectionFactory.createConnection();
            connection.start();  
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createTopic("jms/topic/test"); 
            MessageProducer producer = session.createProducer(destination);

            File imageFile = new File("C:\\Users\\Stefania\\Documents\\Imagine.bmp"); 
            if (imageFile.exists()) {
                byte[] imageData = new byte[(int) imageFile.length()];
                try (FileInputStream fis = new FileInputStream(imageFile)) {
                    fis.read(imageData);
                }

                String imageBase64 = Base64.getEncoder().encodeToString(imageData);

                String jsonMessage = "{"
                        + "\"image\":\"" + imageBase64 + "\","
                        + "\"fileName\":\"" + imageFile.getName() + "\""
                        + "}";

                TextMessage message = session.createTextMessage(jsonMessage);

                producer.send(message);
                System.out.println("Imaginea a fost trimisă pe topic!");

            } else {
                System.out.println("Imaginea se trimite dupa ce se porneste serverul Javalin...");
            }

            connection.close();

        } catch (JMSException | NamingException | IOException e) {
            e.printStackTrace();
        }
    }
}