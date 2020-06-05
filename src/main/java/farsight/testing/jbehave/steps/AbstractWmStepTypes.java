package farsight.testing.jbehave.steps;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.regex.Pattern;

import com.wm.data.IData;
import com.wm.data.IDataCursor;
import com.wm.util.coder.IDataXMLCoder;

import farsight.testing.jbehave.TestExecutor;
import farsight.testing.jbehave.jbehave.StoryResourceContext;
import farsight.utils.idata.DataBuilder;

/**
 * Base class to implement steps
 * 
 */
public class AbstractWmStepTypes {
	
	public static class DataBuilderIterable implements Iterable<DataBuilder> {
		
		private final Collection<IData> source;
		
		public DataBuilderIterable(Collection<IData> source) {
			this.source = source == null ? Collections.emptyList() : source;
		}
		
		public DataBuilderIterable(IData[] source) {
			this(source == null ? null : Arrays.asList(source));
		}

		@Override
		public Iterator<DataBuilder> iterator() {
			final Iterator<IData> sourceIterator = source.iterator(); 
			return new Iterator<DataBuilder>() {

				@Override
				public boolean hasNext() {
					return sourceIterator.hasNext();
				}

				@Override
				public DataBuilder next() {
					return DataBuilder.wrap(sourceIterator.next());
				}
			};
		}
	}
	
	public static class DataList extends ArrayList<IData> {
		
		private static final long serialVersionUID = 1L;

		public static DataList create() {
			return new DataList();
		}
		
		public IData[] toArray() {
			return super.toArray(new IData[size()]);
		}

		public DataList add(DataBuilder builder) {
			add(builder.build());
			return this;
		}
		
		public int index() {
			return size();
		}
		
		public IData[] build() {
			return toArray();
		}
		
	}

	// not exact e.g. service names are not allowed to start with a number, ...
	public static final Pattern PATTERN_SERVICENAME = Pattern.compile("[^ .:]+(?:\\.[^ .:]+)*:[^ .:]+");

	// helpers
	
	public static <T> T read(IData data, String path, Class<T> type) {
		return wrap(data).read(path.trim(), type);
	}
	
	public static DataBuilder wrap(IData source) {
		return DataBuilder.wrap(source);
	}
	
	public static DataBuilder wrap(IData source, String path) {
		return wrap(read(source, path, IData.class));
	}
	
	// access to executor
	
	public TestExecutor executor() {
		return TestExecutor.current();
	}
	
	public void stepDone() {
		executor().stepDone();
	}
	
	// access to pipeline

	public IData pipeline() {
		return executor().getPipeline();
	}
	
	public <T> T pipeline(String path, Class<T> type) {
		return read(pipeline(), path, type);
	}
	
	public IData pipelineDocument(String path) {
		return pipeline(path, IData.class);
	}
	
	public IData[] pipelineDocumentList(String path) {
		return pipeline(path, IData[].class);
	}
	
	// access to resources
	
	public StoryResourceContext resources() {
		return executor().getContext().getResources();
	}
	
	public String getStringResource(String path) {
		return resources().getAsString(path);
	}
	
	public IData getIDataResource(String path) throws Exception {
		return resources().getAsIData(path);
	}
	
	// validation
	
	public void validate(Pattern pattern, String input, String message) {
		if (!pattern.matcher(input).matches())
			throw new StepException(String.format(message, input));
	}

	public String validateServicename(String serviceName) {
		serviceName = serviceName.trim();
		validate(PATTERN_SERVICENAME, serviceName, "parameter $serviceName is not a valid service name ('%s')");
		return serviceName;
	}
	
	// helper for documents
	
	public static DataBuilder newData() {
		return DataBuilder.create();
	}
	
	// helper for document lists
	
	public static DataList newList() {
		return DataList.create();
	}
	
	public Iterable<DataBuilder> walk(String path) {
		return walk(pipelineDocumentList(path));
	}
	
	public Iterable<DataBuilder> walk(final IData[] list) {
		return new DataBuilderIterable(list);
	}
	
	// helper for serialization 
	
	public static String toAttributeString(IData data, int level) {
		if(data == null)
			return null;
		
		StringBuilder buf = new StringBuilder();
		toAttributeString(data.getCursor(), buf, "", level);
		return buf.toString();
	}
	
	private static void toAttributeString(IDataCursor c, StringBuilder buf, String prefix, int level) {
		while(c.hasMoreData()) {
			c.next();
			Object value = c.getValue();
			if(value == null || value instanceof Object[])
				continue;
			if(value instanceof IData) {
				if(level > 1)
					toAttributeString(((IData)value).getCursor(), buf, prefix + c.getKey() + ".", level - 1);
			} else {
				if(buf.length() > 0)
					buf.append(", ");
				buf.append(c.getKey()).append("=").append(c.getValue());
			}
		}
	}
	
	public static String toXMLString(IData data) throws IOException {
		return new String(new IDataXMLCoder("UTF-8").encodeToBytes(data));
	}
	public static String toXMLString(DataBuilder builder) throws IOException {
		return toXMLString(builder.build());
	}


	
}
