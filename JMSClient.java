package apachetomeejms;

import jakarta.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.activemq.ActiveMQConnectionFactory;
import java.util.Properties;
import java.io.IOException;
import java.io.InputStreamReader;

public class JMSClient {
    protected static final String url = "tcp://localhost:61617";
    
    public static void main(String[] args) {
        String topicName = null;
        Context jndiContext = null;
        TopicConnectionFactory topicConnectionFactory = null;
        TopicConnection topicConnection = null;
        TopicSession topicSession = null;
        Topic topic = null;
        TopicSubscriber topicSubscriber = null;
        JsonMessageListener topicListener = null;
        InputStreamReader inputStreamReader = null;
        char answer = '\0';
        topicName = new String(args[1]);
        System.out.println("Topic name = "+topicName);

        try {
            Properties props = new Properties();
            props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
            props.setProperty(Context.PROVIDER_URL, args[0]);

            jndiContext = new InitialContext(props);
            topicConnectionFactory = (TopicConnectionFactory) jndiContext.lookup("ConnectionFactory");

        } catch (NamingException ne) {
            ne.printStackTrace();
            System.exit(2);
        }

        try {
            topicConnection = topicConnectionFactory.createTopicConnection();
            topicSession = topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            topic = topicSession.createTopic("jms/topic/test");
            topicSubscriber = topicSession.createSubscriber(topic);

            topicListener = new JsonMessageListener();
            topicSubscriber.setMessageListener(topicListener);
            topicConnection.start();

            System.out.println("Se asteapta trimiterea imaginii...");
            System.out.println("Pentru a inchide programul, apasa q + CR/LF");
            inputStreamReader = new InputStreamReader(System.in);
            while (!(answer == 'q')) {
                try {
                    answer = (char) inputStreamReader.read();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        } catch (JMSException jmse) {
            jmse.printStackTrace();
        } finally {
            if (topicConnection != null) {
                try {
                    topicConnection.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

class JsonMessageListener implements MessageListener {
    
    @Override
    public void onMessage(Message message) {
        TextMessage msg = null;
        
        try {
            if (message instanceof TextMessage) {
                msg = (TextMessage) message;
                String jsonText = msg.getText();
                
                String imageName = extractValue(jsonText, "imageName");
                String imageSize = extractValue(jsonText, "imageSize");
                String imageData = extractValue(jsonText, "imageData");

                System.out.println("Imagine trimisa");
            } else {
                System.out.println("Imaginea nu s-a trimis!");
            }
        } catch (JMSException jmse) {
            jmse.printStackTrace();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private String extractValue(String jsonText, String key) {
        String searchKey = "\"" + key + "\":";
        int start = jsonText.indexOf(searchKey) + searchKey.length();
        int end = jsonText.indexOf(",", start);
        
        if (end == -1) {
            end = jsonText.indexOf("}", start);
        }
        
        return jsonText.substring(start, end).replaceAll("\"", "").trim();
    }
}
