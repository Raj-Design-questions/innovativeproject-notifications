package services;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class ReceiveFromRabbit {
	
	private String QUEUE_NAME = null;
	private byte[] notification = null;
	Deserialization deserialization;
	
	public ReceiveFromRabbit(String queueName){
		this.QUEUE_NAME = queueName;
	}

	public ReceiveFromRabbit(String queueName, Deserialization deserialization) {
		this.QUEUE_NAME = queueName;
		this.deserialization = deserialization;
	}

	public void configure() throws IOException, java.lang.InterruptedException, TimeoutException{
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		channel.queueDeclare(QUEUE_NAME, false, false, false, null);
		System.out.println(" [*] Waiting for messages from queue: "+ QUEUE_NAME);
		Consumer consumer = new DefaultConsumer(channel){
			@Override
			public void handleDelivery(String consumerTag,
									   Envelope envelope,
									   AMQP.BasicProperties properties,
									   byte[] body)
					throws IOException{
				deserialization.processNotification(body);
			}
		};
		channel.basicConsume(QUEUE_NAME, true, consumer);
	}

	
	public void getNotificationBytes() throws IOException, java.lang.InterruptedException, TimeoutException{
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		
		channel.queueDeclare(QUEUE_NAME, false, false, false, null);
		System.out.println(" [*] Waiting for messages from queue: "+ QUEUE_NAME);
		Consumer consumer = new DefaultConsumer(channel){
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
			throws IOException{
				notification = body;
			}
		};
		channel.basicConsume(QUEUE_NAME, true, consumer);
	}
	
	public byte[] getNotification(){
		byte[] temp = notification;
		notification = null;
		return temp;
	}
}
