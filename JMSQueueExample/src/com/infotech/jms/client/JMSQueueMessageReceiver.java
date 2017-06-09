package com.infotech.jms.client;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.infotech.jms.util.JmsUtil;
public class JMSQueueMessageReceiver implements MessageListener
{
	private QueueReceiver queueReceiver;
	private boolean quit = false;
	
	@Override
	public void onMessage(Message message)
	{
		try {
			String jmsTextMessage;
			if (message instanceof TextMessage) {
				jmsTextMessage = ((TextMessage) message).getText();
			} else {
				jmsTextMessage = message.toString();
			}
			System.out.println("Message Received: " + jmsTextMessage);
			if (jmsTextMessage.equalsIgnoreCase("quit")) {
				synchronized (this) {
					quit = true;
					this.notifyAll(); // Notify main thread to quit
				}
			}
		} catch (JMSException jmse) {
			System.err.println("An exception occurred: " + jmse.getMessage());
		}
	}

	private void init(Context ctx, String queueName)throws NamingException, JMSException
	{
		QueueSession queueSession = JmsUtil.getQueueSession(ctx);
		Queue queue = (Queue) ctx.lookup(queueName);
		queueReceiver = queueSession.createReceiver(queue);
		queueReceiver.setMessageListener(this);
	}
	
	private void close() throws JMSException
	{
		if(queueReceiver !=null)
			queueReceiver.close();
		JmsUtil.cleanUp();
	}
	
	public static void main(String[] args) throws JMSException {
		InitialContext initialContext;
		JMSQueueMessageReceiver jMSQueueReceiver = new JMSQueueMessageReceiver();
		try {
			initialContext = JmsUtil.getInitialContext();
			jMSQueueReceiver.init(initialContext, JmsUtil.QUEUE_JNDI_NAME);
			System.out.println("JMS Ready To Receive Messages (To quit, send a \"quit\" message).");
			synchronized (jMSQueueReceiver) {
				while (!jMSQueueReceiver.quit) {
					try {
						jMSQueueReceiver.wait();

					} catch (InterruptedException ie) {
						ie.printStackTrace();
					}
				}
			}
		} catch (NamingException | JMSException e) {
			e.printStackTrace();
		}finally {
			jMSQueueReceiver.close();
		}
		
	}
}