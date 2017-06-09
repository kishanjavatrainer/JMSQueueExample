package com.infotech.jms.util;

import java.util.Hashtable;

import javax.jms.JMSException;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class JmsUtil {
	public final static String QUEUE_JNDI_NAME = "com/info/InfoDistributedQueue";// Weblogic Queue JNDI
	private static final String WEBLOGIC_JMS_URL = "t3://localhost:7001"; // Weblogic JMS URL
	private final static String WEBLOGIC_JNDI_FACTORY_NAME = "weblogic.jndi.WLInitialContextFactory";
	private final static String CONNECTION_FACTORY_JNDI_NAME = "com/info/InfoConnectionFactory";// Weblogic ConnectionFactory JNDI
	private static QueueConnection queueConnection = null;
	private static QueueSession queueSession = null;
	
	public static InitialContext getInitialContext()
			throws NamingException
	{
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, WEBLOGIC_JNDI_FACTORY_NAME);// set Weblogic JNDI
		env.put(Context.PROVIDER_URL, WEBLOGIC_JMS_URL);// set Weblogic JMS URL
		env.put(Context.SECURITY_PRINCIPAL, "weblogic"); // set Weblogic UserName
		env.put(Context.SECURITY_CREDENTIALS, "weblogic1"); // set Weblogic PassWord
		InitialContext initialContext = new InitialContext(env);
		return initialContext;
	}
	
	public static QueueSession getQueueSession(Context ctx) throws NamingException, JMSException {
		QueueConnectionFactory queueConnectionFactory = (QueueConnectionFactory) ctx.lookup(CONNECTION_FACTORY_JNDI_NAME);
		queueConnection = queueConnectionFactory.createQueueConnection();
		queueConnection.start();
		queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
		return queueSession;
	}
	
	public static void cleanUp() throws JMSException{
		if(queueSession != null)
		queueSession.close();
		if(queueConnection !=null)
		queueConnection.close();
	}
	
}
