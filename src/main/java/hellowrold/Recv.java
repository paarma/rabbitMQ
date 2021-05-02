package hellowrold;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class Recv {
	
	private final static String QUEUE_NAME = "hello";
	
	public static void main(String[] argv) throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		
		/**
		 * A diferencia del "Send.java" en este caso no se cierra la conexión con el try-con resources 
		 * ya que el consumidor siempre debe estar escuchando la cola para porcesar los mensajes.
		 */
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		
		channel.queueDeclare(QUEUE_NAME, false, false, false, null);
		System.out.println(" [*] Waiting for messages. To  exit press CTRL+C");
		
		
		//Obtenemos los mensajes de la cola "hello" estructurandolos en un objeto "deliverCallback" en el cual indicamos que debe imprimir el mensaje
		DeliverCallback deliverCallback = (consumerTag, delivery) -> {
			
			String message = new String(delivery.getBody(), "UTF-8");
			System.out.println(" [X] Received '" + message + "'");	
		};
		
		//Consumimos los mensajes de la cola el cual ejecuta las acciones indicadas en el objeto "deliverCallback"
		channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumetTag -> { });
		
		
	}
	
}
