package mobigen.integrated_search_engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.FileChangeEvent;
import org.apache.commons.vfs2.FileContent;
import org.apache.commons.vfs2.FileListener;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.impl.DefaultFileMonitor;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.net.InetAddresses;

/**
 * Hello world!
 *
 */
public class App 
{
    private static final Logger logger = LoggerFactory.getLogger(App.class);
	
	public static void main( String[] args ) throws FileSystemException
    {
        logger.trace("Integrated Search Engine");
        
        Executor runner = Executors.newFixedThreadPool(2);
        runner.execute(new Runnable() {
			
			public void run() {
	            FileObject listendir = null;
	            try {
	            	String base = Paths.get(".").toAbsolutePath().normalize().toString();
	                FileSystemManager fsManager = VFS.getManager();
	                //listendir = fsManager.resolveFile("C:/dev/search/input");
	                listendir = fsManager.resolveFile(base+"/input");
	            } catch (FileSystemException e) {
	                e.printStackTrace();
	            }
	            
	            DefaultFileMonitor fm = new DefaultFileMonitor(new FileListener() {

	            	String base = Paths.get(".").toAbsolutePath().normalize().toString();
	            	
	                public void fileDeleted(FileChangeEvent event) throws Exception {
	                    logger.trace(event.getFile().getName().getPath()+" Deleted.");
	                }

	                public void fileCreated(FileChangeEvent event) throws Exception {
	                	logger.trace(event.getFile().getName().getPath()+" Created.");
	                    String outPath = event.getFile().getName().getPath().replaceAll("/input/", "/output/");
	                    if(event.getFile().getType()==FileType.FOLDER){
	                    	File outDirectory = new File(outPath);
	                    	if(!outDirectory.exists()){
	                    		while(outDirectory.mkdirs()){
	                    			logger.trace(outDirectory+" mkdir.");
	                    		}
	                    	}
	                    	return;
	                    }
	                    String extention = "src";
	                    String outFile = outPath.substring(0, outPath.lastIndexOf('/'))  + "/" + outPath.substring(0, outPath.lastIndexOf('.')).replaceAll("/", "_") + "." + extention; 
	                    //String outFile = outPath.substring(0, outPath.lastIndexOf('.')).replaceAll("/", "_") + "." + extention; 
	                    
	                    String command = base + "/sn3f_exe.20150311";
	                    String arg0 = "-D";
	                    String arg1 = event.getFile().getName().getPath();
	                    String arg2 = "-O";
	                    String arg3 = outFile;
	                    String arg4 = "-C";
	                    String arg5 = "utf8";
	                    
	                    ProcessBuilder pb = new ProcessBuilder(command, arg0, arg1, arg2, arg3, arg4, arg5);
	                    pb.redirectErrorStream(true);
	                    try {
	                        Process p = pb.start();
	                        String s;
	                        BufferedReader stdout = new BufferedReader (
	                            new InputStreamReader(p.getInputStream()));
	                        while ((s = stdout.readLine()) != null) {
	                            System.out.println(s);
	                            logger.trace(s);
	                        }
	                        p.getInputStream().close();
	                        p.getOutputStream().close();
	                        p.getErrorStream().close();
	                     } catch (Exception ex) {
	                        ex.printStackTrace();
	                    }
	                }

	                public void fileChanged(FileChangeEvent event) throws Exception {
	                    logger.trace(event.getFile().getName().getPath()+" Changed.");
	                }
	            });
	            fm.setRecursive(true);
	            fm.addFile(listendir);
	            fm.start();
			}
		});
        
        runner.execute(new Runnable() {
			
			public void run() {
				FileObject listendir = null;
				String base = Paths.get(".").toAbsolutePath().normalize().toString();
                try {
                	FileSystemManager fsManager = VFS.getManager();
					listendir = fsManager.resolveFile(base+"/output");
				} catch (FileSystemException e) {
					e.printStackTrace();
				}
                DefaultFileMonitor fm = new DefaultFileMonitor(new FileListener() {
                    
                    //BoolQueryBuilder qb = null;
                    //QueryStringQueryBuilder qS = null;
                    //SearchRequestBuilder srb = null;
                    //SearchResponse response  = null;
                    
                    //GetResponse getresponse = client.prepareGet("test", "", "").get();
                    
					public void fileDeleted(FileChangeEvent event) throws Exception {
						logger.trace(event.getFile().getName().getPath()+" Deleted.");
						
					}
					
					public void fileCreated(FileChangeEvent event) throws Exception {
						logger.trace(event.getFile().getName().getPath()+" Created.");
						if(event.getFile().getType()==FileType.FOLDER)return;
						
						Settings settings = Settings.settingsBuilder().put("cluster.name", "elasticsearch").build();
						
						Client client = TransportClient.builder().settings(settings).build()
								.addTransportAddress(new InetSocketTransportAddress(InetAddresses.forString("127.0.0.1"), 9300)); // 아이피와 포트 입력
						
						System.out.println("========================================================================================");
						System.out.println("path : "+ event.getFile().getName().getPath());
						System.out.println("base : "+ event.getFile().getName().getBaseName());
						System.out.println("extention : "+ event.getFile().getName().getExtension());
						System.out.println("rootURI : "+ event.getFile().getName().getRootURI());
						System.out.println("scheme : "+ event.getFile().getName().getScheme());
						System.out.println("========================================================================================");
						
						FileObject file = event.getFile();
						String _path_ = file.getName().getPath();
						String name = file.getName().getBaseName();
						String path = _path_.substring(0, _path_.lastIndexOf(name));
						
						System.out.println("========================================================================================");
						final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
						Pattern pattern;
						Matcher matcher;
						String author = null;
						for(String nm : path.split("/")){
							pattern = Pattern.compile(EMAIL_PATTERN);
							matcher = pattern.matcher(nm);
							if(matcher.matches()){
								author = nm;
								System.out.println("author : "+author);
							}
						}
						System.out.println("========================================================================================");
						System.out.println("========================================================================================");
						FileContent _content_ = file.getContent();
						String content = IOUtils.toString(_content_.getInputStream(), "utf8");
						System.out.println("content : "+content);
						System.out.println("content size: "+content.length());
						System.out.println("========================================================================================");
						
						/*
				        BulkRequestBuilder blkreq = client.prepareBulk();
				        blkreq.add(client.prepareIndex("files", "file", "")
				        		.setSource(XContentFactory.jsonBuilder()
				        				.startObject()
				        				.field("author",author)
				        				.field("path",_path_)
				        				.field("title",name)
				        				.field("content",content)
				        				.endObject()
				        				)
				        		);
				        BulkResponse blkres = blkreq.execute().actionGet();
						System.out.println("========================================================================================");
						System.out.println("bulk response : "+ blkres.toString());
						if(blkres.hasFailures()){
							System.out.println("has failure!");
							System.out.println(blkres.buildFailureMessage());
						}
						System.out.println("========================================================================================");
				        */
				        
				        IndexRequestBuilder idxreqb = client.prepareIndex("files", "file");
				        idxreqb.setSource(XContentFactory.jsonBuilder()
				        		.startObject()
				        		.field("author",author)
				        		.field("path",_path_)
				        		.field("title",name)
				        		.field("content",content)
				        		.endObject()
				        		);
				        IndexResponse idxres = idxreqb.execute().actionGet();
				        
				        System.out.println("========================================================================================");
				        System.out.println("bulk response : "+ idxres.toString());
				        if(!idxres.isCreated()){
				        	System.out.println("not created!");
				        }
				        System.out.println("========================================================================================");
				        
				        client.close();
					}
					
					public void fileChanged(FileChangeEvent event) throws Exception {
						logger.trace(event.getFile().getName().getPath()+" Changed.");
						
					}
					
				});
                fm.setRecursive(true);
                fm.addFile(listendir);
                fm.start();
			}
		});
    }
}
