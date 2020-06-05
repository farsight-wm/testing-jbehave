
package farsight.testing.jbehave;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;

import com.wm.app.b2b.client.Context;
import com.wm.app.b2b.client.ServiceException;
import com.wm.data.IData;
import com.wm.data.IDataFactory;
import com.wm.lang.ns.NSName;

import farsight.testing.jbehave.jbehave.ExecutionProperties;
import farsight.testing.jbehave.jbehave.StoryResourceContext;
import farsight.testing.jbehave.junit.ConfigProperties;
import farsight.testing.jbehave.utils.AdviceIDManager;


/**
 * 
 * Execution Context holds all relevant data for a single test
 * 
 * TODO check if it is possible to support tests based on ProcessorScope Session / User
 * TODO check if it is possible to support parallel testing for stories
 * 
 * relevant data includes:
 * 	- Scope dependent properties
 *  - Resource Context
 *  - AdviceIDManager ?!
 */
public class ExecutionContext {
	
	private static final Logger logger = LogManager.getLogger(ExecutionContext.class);
	
	private Context context;
	private IData pipeline;
	private Throwable thrownException;
	
	private ExecutionProperties props = new ExecutionProperties(ConfigProperties.instance().properties());
	private AdviceIDManager advices = new AdviceIDManager();
	private StoryResourceContext resources = null;

	private int stepIndex = 0;
	
	public ExecutionContext() {
		pipeline = IDataFactory.create();
	}

	public Context getConnectionContext() throws ServiceException {
		if (context == null) {
			context = createConnectionContext();
		}
		return context;
	}

	private Context createConnectionContext() throws ServiceException {
		String host = props.get("wm.server.host", "localhost");
		int port = Integer.parseInt(props.get("wm.server.port", "5555"));
		String username = props.get("wm.server.username", "Administrator");
		String password = props.get("wm.server.password", "manage");
		boolean secure = Boolean.parseBoolean(props.get("wm.server.secure", "false"));

		return connectToServer(host, port, username, password, secure);
	}

	protected Context connectToServer(String host, int port, String username, String password, boolean secure)
			throws ServiceException {
		Context ctx = new Context();
		if (secure) {
			throw new UnsupportedOperationException();
		} else {
			try {
				ctx.connect(host, port, username, password);
			} catch (Exception e) {
				Assert.fail("Unable to connect to " + host + ':' + port + " with user " + username + " - " + e.getMessage());
				// Abort to prevent repeated failures which can lead to account lockout 
			}
		}
		return ctx;
	}
	
	public boolean testConnection() throws ServiceException{
		return this.getConnectionContext().isConnected();
	}

	public IData getPipeline() {
		return pipeline;
	}

	public void setPipeline(IData pipeline) {
		this.pipeline = pipeline;
	}

	public void setThrownException(Throwable e) {
		thrownException = e;
	}

	public Throwable getThrownException() {
		return thrownException;
	}

	public void terminate() {
		if(context != null){
			context.disconnect();
		}
		context = null;
	}
	
	public void reset(Scope scope) {
		props.reset(scope);
		switch(scope) {
		case Test:
		case Story:
		case Scenario:
			advices.reset();
			stepIndex = 0;
		}
	}
	
	public void setStepIndex(int index) {
		this.stepIndex = index; //XXX not so beautiful :/
	}
	
	public void setContextProperty(String key, String value) {
		props.set(key, value);
	}
	
	public void setResources(StoryResourceContext resources) {
		this.resources = resources;
		
	}

	public StoryResourceContext getResources() {
		return resources;
	}

	public AdviceIDManager getAdviceIDManager() {
		return advices;
	}

	public int stepIndex() {
		return stepIndex;
	}

	public void setProperty(String key, String value, Scope scope) {
		props.set(key, value, scope);
	}

	public String getProperty(String key, String defaultValue) {
		return props.get(key, defaultValue);
	}
	
	public String getProperty(String key) {
		return props.get(key);
	}
	
	public IData invokeService(String serviceName, IData input) throws ServiceException {
		Context context = getConnectionContext();
		if(!context.isConnected())
			throw new IllegalStateException("Not connected to integration server");
		logger.trace("invoking remote service: " + serviceName);
		return context.invoke(NSName.create(serviceName), input);
	}

}
