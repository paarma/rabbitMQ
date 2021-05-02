package workqueues;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

public class NewTask {
	
	private static final String TASK_QUEUE_NAME = "task_queue";		//Cola en este caso para definirla como duradera
	
	/**
	 * En este caso se enviarán mensajes que se ingresan por consola.
	 * 
	 * Los mensajes en este caso cadenas de texto ingresadas por consola que contengan el caracter "." 
	 * representará 1 segundo de trabajo pora cada "."(simulnado tiempo de procesamiento para el mensaje)
	 * 
	 * @param argv
	 * @throws Exception
	 */
	public static void main(String[] argv) throws Exception {
		
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		
		try (Connection connection = factory.newConnection();
			Channel channel = connection.createChannel()){
			
			/**
			 * En caso de que el servidor de rabbitMQ se reinicie o algo por el estilo, deberán persistir tanto las colas como los mensajes para que no se pierdan.
			 * para ello se requieren dos cosas para asegurarnos de que los mensajes no se pierdan: debemos marcar tanto la cola como los mensajes como duraderos.
			 */
			boolean durable = true;
			
			channel.queueDeclare(TASK_QUEUE_NAME, durable, false, false, null);
			
			String message = String.join(" ", argv);
			
			/**
			 * Se marcan los mensajes como duraderos (o persistentes) para que no se pierdan en caso de un reinicio del rabbit.
			 * para ello se configura la propiedad "MessageProperties" que implementa "BasicProperties" con el valor de PERSISTENT_TEXT_PLAIN
			 * (Guarda el mensaje en el disco en caso de reinicios o fallas en el servidor de rabbit)
			 */
			
			channel.basicPublish("", TASK_QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes("UTF-8"));
			
			System.out.println(" [X] Sent '"+ message + "'");
		}
		
	}
	
}
