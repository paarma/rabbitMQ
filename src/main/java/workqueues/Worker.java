package workqueues;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class Worker {
	
	private static final String TASK_QUEUE_NAME = "task_queue";	//Cola en este caso para definirla como duradera
	
	/**
	 * Por cada "." encontrado en el mensaje se creará una pausa de 1 segundo para simular procesamiento de trabajo en el mensaje.
	 * @param argv
	 * @throws Exception
	 */
	public static void main(String[] argv) throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		
		/**
		 * A diferencia del "Send.java" en este caso no se cierra la conexión con el try-con resources 
		 * ya que el consumidor siempre debe estar escuchando la cola para porcesar los mensajes.
		 */
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		
		
		/**
		 * En caso de que el servidor de rabbitMQ se reinicie o algo por el estilo, deberán persistir tanto las colas como los mensajes para que no se pierdan.
		 * para ello se requieren dos cosas para asegurarnos de que los mensajes no se pierdan: debemos marcar tanto la cola como los mensajes como duraderos.
		 * (Los mensajes se marcan en el productor (la clase  NewTask.java))
		 */
		boolean durable = true;
		
		channel.queueDeclare(TASK_QUEUE_NAME, durable, false, false, null);
		System.out.println(" [*] Waiting for messages. To  exit press CTRL+C");
		
		
		
		/**
		 * Dado que rabbit reparte los mensajes de manera uniforme entre los consumidores. Puede ocurrir que un consumidor se sobrecarque 
		 * en caso de recibir muchos mensajes pesados y otro consumidor quede desocupado al terminar de procesar sus mensajes que fueron livianos.
		 * 
		 * Para solucionar eso se puede utiliizar la propiedad "basicQos = 1" lo cual le indica a rabbit que no le dé mas de un mensaje a un consumidor
		 * a la vez. Es decir que no envié un nuevo mensaje al consumidor hasta que haya procesado y reconocido el menaje anterior. Con esto lo enviará 
		 * al siguiente consumidor desocupado. 
		 */
		int prefetchCount = 1;
		channel.basicQos(prefetchCount);
		
		
		//Obtenemos los mensajes de la cola "hello" estructurandolos en un objeto "deliverCallback" en el cual indicamos que debe imprimir el mensaje
		DeliverCallback deliverCallback = (consumerTag, delivery) -> {
			
			String message = new String(delivery.getBody(), "UTF-8");
			System.out.println(" [X] Received '" + message + "'");	
			
			try {
				doWork(message);
			} finally {
				System.out.println(" [X] Done ");
				
				/**
				 * Confirmacion basica de mensaje procesado.
				 * De esta manera se garantiza en caso de que un mensaje no sea procesaso ya sea que un consumidor falle o se caiga,
				 * el mensaje al no tener confirmación de procesado será enviado nuvamente para que se procese por los consumidores.
				 */
				channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);	
			}
			
		};
		
		/**
		 * Bandera para que el consumidor confirme que el mensaje fue recibido y procesado exitosamente VALOR = FALSE;
		 * Si su valor es TRUE indica que se desactiva esta verificación y el mensaje se puede perder en caso de que el consumidor se caiga.
		 * 
		 * Por esta razón lo recomendable es que esta bandera esté en FALSE ya que en caso de que el consumidor se caiga, rabbit lo reenviará a otro.
		 */
		boolean autoAck = false; 
		
		//Consumimos los mensajes de la cola el cual ejecuta las acciones indicadas en el objeto "deliverCallback"
		channel.basicConsume(TASK_QUEUE_NAME, autoAck, deliverCallback, consumetTag -> { });
		
	}
	
	//Tarea que simula tiempo de procesamiento en el mensaje. 1 Segundo por cada caracter ".".
	private static void doWork(String task) {
		for (char ch : task.toCharArray()) {
			
			if(ch == '.')
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
		}
	}
	
}
