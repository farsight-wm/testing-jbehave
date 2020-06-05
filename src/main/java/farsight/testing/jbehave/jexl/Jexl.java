package farsight.testing.jbehave.jexl;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlScript;
import org.apache.commons.jexl3.internal.ModifiedBuilder;

import com.wm.data.IData;

import farsight.testing.utils.jexl.IDataJexlContext;
import farsight.testing.utils.jexl.context.ResourceContext;
import farsight.testing.utils.jexl.context.WmIDataContext;
import farsight.testing.utils.jexl.functions.LoadFunction;

public class Jexl {
	
	public static class ScriptEnv {
		
		private ArrayList<Object> parameter;
		private ArrayList<String> names;
		
		private IDataJexlContext context = null;
		
		public ScriptEnv() {}
		
		public String[] getNames() {
			if(names == null)
				return null;
			return names.toArray(new String[names.size()]);
		}

		public Object[] getArgs() {
			if(parameter == null)
				return null;
			return parameter.toArray(new Object[parameter.size()]);
		}
		
		public ScriptEnv addArgument(String name, Object arg) {
			if(parameter == null) {
				parameter = new ArrayList<>();
				names = new ArrayList<>();
			}
			
			names.add(name);
			parameter.add(WmIDataContext.wrap(arg));
			
			return this;
		}
		
		public ScriptEnv addResources(ResourceContext resources) {
			return addNamespace("load", new LoadFunction(resources, getContext()));
		}
		
		public ScriptEnv addNamespace(String name, Object namespace) {
			getContext().registerNamespace(name, namespace);
			return this;
		}
		
		public ScriptEnv setContext(IDataJexlContext context) {
			this.context = context;
			return this;
		}
		
		public IDataJexlContext getContext() {
			if(context == null)
				context = new IDataJexlContext(null);
			return context; 
		}
		
		public Object execute(String script) {
			return execute(engine(), script);
		}
		
		public Object execute(JexlEngine engine, String script) {
			return engine.createScript(script, getNames()).execute(getContext(), getArgs());
		}
		
	}


	private static JexlEngine CLIENT_ENGINE = null;
	private static final int JEXL_CACHE_SIZE = 0;
	
	public static JexlEngine engine() {
		if(CLIENT_ENGINE == null)
			return CLIENT_ENGINE = createJexlEngine();
		return CLIENT_ENGINE;
	}
	
	private static JexlEngine createJexlEngine() {
		JexlBuilder builder = new ModifiedBuilder();
		builder.charset(StandardCharsets.UTF_8);
		builder.cache(JEXL_CACHE_SIZE);
		builder.strict(true).silent(false);
		return builder.create();
	}
	
	private Jexl() {}
	
	public static JexlScript createScript(String script, String... names) {
		return engine().createScript(script, names);
	}
	
	public static ScriptEnv prepareScript(IData root) {
		return new ScriptEnv().setContext(new IDataJexlContext(root));
	}
	
	public static ScriptEnv prepareAssertionScript(IData root) {
		return prepareAssertionScript(root, "/");
	}
	
	public static ScriptEnv prepareAssertionScript(IData root, String pathPrefix) {
		return new ScriptEnv().setContext(new WmAssertJContext(root, pathPrefix));
	}
	
	
	
}
