# rabbitMQ

<b>Ejemplo 1. hellowrold</b>

1. Se ejecuta desde el IDE la clase "hellowrold.Recv.java" la cual hace de "consumidor", se puede ver en la consola del IDE que este servicio está a la 
espera para procesar mensajes.

2. Se ejecuta desde el IDE la clase "hellowrold.Send.java" la cual hace de "productor". Como resultado se puede ver en la consola de esta ejeción que se envía 
un mensaje y al ver la consola del punto (1) se puede ver que el mensaje es procesado por el consumidor.



<b>Ejemplo 2. Work Queues (Dos consumidores y un productor)</b>

1. Compilar las clases java: 
    * Ubicarse en la ruta "rabbitMQ\rabbitMQ\src\main\java\librerias" desde una terminal y ejecutar el siguiente comando:
    
      javac -cp amqp-client-5.7.1.jar ..\workqueues\NewTask.java ..\workqueues\Worker.java
      
2. Ejecutar los dos consumidores: (repetir este paso en una segunda terminal)
    * Se puede ubicar en la ruta "rabbitMQ\rabbitMQ\src\main\java" desde una terminal y ejecutar el siguiente comando (para windows):

      java -cp .;librerias\amqp-client-5.7.1.jar;librerias\slf4j-api-1.7.26.jar;librerias\slf4j-simple-1.7.26.jar workqueues.Worker
      
3. Ejecutar el productor:
    * En una tercera consola (ubicandose el la ruta del paso anterior) se ejecuta el productor y se envían varios mensajes ("primer message.", "segundo message..")
    Ejemplo: 
    
      * java -cp .;librerias\amqp-client-5.7.1.jar;librerias\slf4j-api-1.7.26.jar;librerias\slf4j-simple-1.7.26.jar workqueues.NewTask primer message.
      * java -cp .;librerias\amqp-client-5.7.1.jar;librerias\slf4j-api-1.7.26.jar;librerias\slf4j-simple-1.7.26.jar workqueues.NewTask segundo message..
      * java -cp .;librerias\amqp-client-5.7.1.jar;librerias\slf4j-api-1.7.26.jar;librerias\slf4j-simple-1.7.26.jar workqueues.NewTask tercer message...
      
 Al revisar las consolas de los consumidores se puede observar como estos mensajes son procesados de manera BALANCEADA a medida que van siendo enviados.
      
