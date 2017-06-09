package com.infotech.jms.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.infotech.jms.util.JmsUtil;
public class JMSQueueMessageSender
{
	private QueueSender queueSender;
	private Queue queue;
	private TextMessage textMessage;

	private void init(Context ctx, String queueName)throws NamingException, JMSException
	{
		QueueSession queueSession = JmsUtil.getQueueSession(ctx);
		queue = (Queue) ctx.lookup(queueName);
		queueSender = queueSession.createSender(queue);
		textMessage = queueSession.createTextMessage();
	}
	
	private void send(String message) throws JMSException {
		textMessage.setText(message);
		queueSender.send(textMessage);
	}
	
	private void close() throws JMSException {
		if(queueSender !=null)
			queueSender.close();
		JmsUtil.cleanUp();
	}
	
	private static void readAndSend(JMSQueueMessageSender jMSQueueMessageSender) throws IOException, JMSException
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String line = null;
		boolean quitNow = false;
		do {
			System.out.print("Enter message (\"quit\" to quit): \n");
			line = br.readLine();
			if (line != null && line.trim().length() != 0) {
				jMSQueueMessageSender.send(line);
				System.out.println("JMS Message Sent: " + line + "\n");
				quitNow = line.equalsIgnoreCase("quit");
			}
		} while (!quitNow);
	}
	
	public static void main(String[] args) throws Exception {
		JMSQueueMessageSender jMSQueueMessageSender = null;
		try {
			InitialContext ic = JmsUtil.getInitialContext();
			jMSQueueMessageSender = new JMSQueueMessageSender();
			jMSQueueMessageSender.init(ic, JmsUtil.QUEUE_JNDI_NAME);
			readAndSend(jMSQueueMessageSender);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(jMSQueueMessageSender != null)
			jMSQueueMessageSender.close();
		}
		
	}
}