package farsight.testing.jbehave.jbehave;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.jbehave.core.configuration.Configuration;

import com.wm.app.b2b.services.DocumentToRecordService;
import com.wm.data.IData;
import com.wm.data.IDataCursor;
import com.wm.data.IDataUtil;
import com.wm.lang.xml.Document;
import com.wm.util.Values;
import com.wm.util.coder.IDataXMLCoder;

import farsight.testing.jbehave.steps.StepException;
import farsight.testing.utils.jexl.context.ResourceContext;
import farsight.utils.idata.SerializeableInputStream;

public class StoryResourceContext implements ResourceContext {
	
	private final Configuration configuration;
	
	public StoryResourceContext(Configuration configuration) {
		this.configuration = configuration;
	}
	
	/*
	 * Converts an xml string into an IData object. The xml string may either be an
	 * XML-encoded pipeline or a plain document. If the xml string does not contain
	 * an IDataXMLCoder element the webMethos logic to convert XML-Documents into
	 * IData documents is used.
	 */
	protected IData convertToIData(String xmlString) throws Exception {
		if (xmlString.contains("IDataXMLCoder")) {
			try {
				IData data = new IDataXMLCoder().decode(IOUtils.toInputStream(xmlString, StandardCharsets.UTF_8));
				return data;
			} catch(Exception e) {
				e.printStackTrace();
				throw e;
			}
			
		} else {
			Document node = new com.wm.lang.xml.Document(xmlString, null, "UTF-8", false, null, true);
			Values in = new Values();
			DocumentToRecordService dtrs = new DocumentToRecordService(in, false);
			dtrs.setIsXTD(true);
			IData idata = (IData) dtrs.bind(node);
			IDataCursor cursor = idata.getCursor();
			IDataUtil.remove(cursor , "@version"); // Inserted during conversion
			cursor.destroy();
			return idata;
		}
	}
	
	@Override
	public String getAsString(String path) {
		String resource = null;
		try {
			if(configuration == null) {
				//fallback to class loader
				InputStream is = this.getClass().getClassLoader().getResourceAsStream(path);
				if(is != null)
					resource = IOUtils.toString(is, "UTF-8");
			} else {
				resource = configuration.storyLoader().loadResourceAsText(path);
			}
			if(resource == null)
				throw new StepException("Unable to load " + path + " from story loader");
			
			return resource;
		} catch (Exception e) {
			throw new StepException("Unable to load " + path + " from story loader");
		}
	}
	
	@Override
	public byte[] getAsBytes(String path) {
		return getAsString(path).getBytes(StandardCharsets.UTF_8);
	}
	
	//for now, there is only the possibility to load data as string
	//good point is, this reader must not be closed! 
	@Override
	public InputStream getAsStream(String path) {
		return new SerializeableInputStream(getAsBytes(path));
	}

	@Override
	public IData getAsIData(String path) throws Exception {
		return convertToIData(getAsString(path));
	}
	
	public IData[] getAsIDataArray(List<String> paths) throws Exception {
		IData[] array = new IData[paths.size()];
		int i = 0;
		for(String xmlString: getAsString(paths)) {
			array[i++] = convertToIData(xmlString);
		}
		return array;
	}

	public List<String> getAsString(List<String> paths) {
		List<String> contents = new ArrayList<>(paths.size());
		String filePathSuffix = "";
		for (String fileName : paths) {
			int slashPos = fileName.lastIndexOf('/'); // Check for path
			if (slashPos != -1) { // Keep path for future use
				filePathSuffix = fileName.substring(0, slashPos);
				contents.add(getAsString(fileName));
			} else if (filePathSuffix.length() > 0) { // Prepend path if defined
				contents.add(getAsString(filePathSuffix + '/' + fileName));
			} else {
				contents.add(getAsString(fileName));
			}
		}
		return contents;
	}
	
}
