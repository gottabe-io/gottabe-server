package io.gottabe.commons.store;

import io.gottabe.commons.entities.BaseOwner;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

public interface FileStore {


	Optional<Resource> findFile(String baseFolder, String objectName);

	void putFile(String baseFolder, String objectName, InputStream data) throws IOException;

	Optional<ObjectSummary> getObject(String baseFolder, String objectName);

	List<ObjectSummary> listObjects(String baseFolder);

	void removeFile(String baseFolder, String objectName) throws IOException;
	
}
