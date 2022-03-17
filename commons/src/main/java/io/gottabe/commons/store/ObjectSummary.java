package io.gottabe.commons.store;

import org.springframework.core.io.Resource;

import java.util.Date;

public interface ObjectSummary {
	
	Date getLastModified();
	
	String getFilename();
	
	long getSize();
	
	String getETag();

	Resource getResource();

}
