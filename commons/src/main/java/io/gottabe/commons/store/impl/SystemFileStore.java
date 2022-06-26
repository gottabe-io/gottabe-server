package io.gottabe.commons.store.impl;

import io.gottabe.commons.entities.BaseOwner;
import io.gottabe.commons.store.FileStore;
import io.gottabe.commons.store.ObjectSummary;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.*;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SystemFileStore implements FileStore {
	
	File baseAbstractOwnersDir;

	public SystemFileStore(File baseDir) {
		this.baseAbstractOwnersDir = new File(baseDir,"AbstractOwners");
	}

	public SystemFileStore(String baseDir) {
		this.baseAbstractOwnersDir = new File(baseDir,"AbstractOwners");
	}

	@Override
	public Optional<Resource> findFile(String baseFolder, String objectName) {
		File AbstractOwnerFolder = new File(baseAbstractOwnersDir, baseFolder);
		File object = new File(AbstractOwnerFolder, objectName);
		return object.exists() ? Optional.of(new FileSystemResource(object)) : Optional.empty();
	}

	@Override
	public void putFile(String baseFolder, String objectName, InputStream data) throws IOException {
		File AbstractOwnerFolder = new File(baseAbstractOwnersDir, baseFolder);
		putFile(objectName, data, AbstractOwnerFolder);
	}

	@Override
	public Optional<ObjectSummary> getObject(String baseFolder, String objectName) {
		File AbstractOwnerFolder = new File(baseAbstractOwnersDir, baseFolder);
		File object = new File(AbstractOwnerFolder, objectName);
		return object.exists() ? Optional.of(new FileObjectSummary(object)) : Optional.empty();
	}

	private void putFile(String objectName, InputStream data, File folder)
			throws FileNotFoundException, IOException {
		File file = new File(folder, objectName);
		File parent = file.getParentFile();
		if (!parent.exists())
			parent.mkdirs();
		FileOutputStream os = new FileOutputStream(file);
		data.transferTo(os);
		os.flush();
		os.close();
	}

	@Override
	public List<ObjectSummary> listObjects(String baseFolder) {
		File AbstractOwnerFolder = new File(baseAbstractOwnersDir, baseFolder);
		return listFiles(AbstractOwnerFolder);
	}

	private List<ObjectSummary> listFiles(File folder) {
		if (!folder.exists())
			folder.mkdirs();
		File[] files = folder.listFiles();
		return Arrays.stream(files)
				.map(FileObjectSummary::new)
				.collect(Collectors.toList());
	}

	private static class FileObjectSummary implements ObjectSummary {

		final File file;

		public FileObjectSummary(File file) {
			this.file = file;
		}

		@Override
		public Date getLastModified() {
			return new Date(file.lastModified());
		}

		@Override
		public String getFilename() {
			return file.getName();
		}

		@Override
		public long getSize() {
			return file.length();
		}

		@Override
		public String getETag() {
			return DigestUtils.md5Hex(file.lastModified() + "|" + file.length() + "|" + file.getName());
		}

		@Override
		public Resource getResource() {
			return new FileSystemResource(file);
		}

	}

	@Override
	public void removeFile(String baseFolder, String objectName) throws IOException {
		File AbstractOwnerFolder = new File(baseAbstractOwnersDir, baseFolder);
		removeFile(AbstractOwnerFolder, objectName);
	}
	
	private void removeFile(File folder, String objectName) {
		if (!folder.exists()) 
			folder.mkdirs();
		File file = new File(folder, objectName);
		file.delete();
	}
	
}
