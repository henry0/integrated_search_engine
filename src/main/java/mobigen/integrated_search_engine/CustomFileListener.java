package mobigen.integrated_search_engine;

import java.io.File;
import java.io.FileWriter;
import java.nio.BufferOverflowException;

import org.apache.commons.vfs2.FileChangeEvent;
import org.apache.commons.vfs2.FileListener;
import org.apache.commons.vfs2.FileObject;

public class CustomFileListener implements FileListener {

	public void fileChanged(FileChangeEvent event) throws Exception {
		FileObject fo = event.getFile();
		String uri = fo.getURL().toURI().toString();
		int hashCode = fo.hashCode();
		System.out.println("uri["+ uri +"], hashCode["+ hashCode +"]");
	}

	public void fileCreated(FileChangeEvent event) throws Exception {
		FileObject fo = event.getFile();
		String uri = fo.getURL().toURI().toString();
		int hashCode = fo.hashCode();
		System.out.println("uri["+ uri +"], hashCode["+ hashCode +"]");
		FileWriter file = new FileWriter(new File("C:/dev/search/output/"+"_"+uri+"_"+hashCode+".txt"));
		file.write("");
		file.flush();
		file.close();
	}

	public void fileDeleted(FileChangeEvent event) throws Exception {
		FileObject fo = event.getFile();
		String uri = fo.getURL().toURI().toString();
		int hashCode = fo.hashCode();
		System.out.println("uri["+ uri +"], hashCode["+ hashCode +"]");
	}

}
