package mobigen.integrated_search_engine;

import java.io.File;
import java.io.FileReader;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.FileObject;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryStringQueryBuilder;

import com.google.common.net.InetAddresses;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }
    
    /**
     * Rigourous Test :-)
     */
    public void testRegex() throws Exception
    {
    	String test = "_home_henry_search_henry.0th@gmail.com_com_sk_filename";
    	
    	final String EMAIL_PATTERN = 
    			"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
    			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    	
    	Pattern pattern;
    	Matcher matcher;
    	
    	String author = null;
    	for(String nm : test.split("_")){
    		pattern = Pattern.compile(EMAIL_PATTERN);
    		matcher = pattern.matcher(nm);
    		if(matcher.matches()){
    			author = nm;
    			System.out.println("match: " + matcher.matches());
    		}
    	}
    	System.out.println("author: " + author);
    	
    	
    	assertTrue( true );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() throws Exception
    {
        //String path = "/input/foo@bar/mobigen/sktelecom/presentation/pb.pptx";
    	//System.out.println(path.substring(0, path.lastIndexOf("/")).replaceAll("/input/", "/output/"));
    	
    	Settings settings = Settings.settingsBuilder().put("cluster.name", "elasticsearch").build();
        
        Client client = TransportClient.builder().settings(settings).build().addTransportAddress(new InetSocketTransportAddress(InetAddresses.forString("127.0.0.1"), 9300));
        
        BoolQueryBuilder qb = null;
        QueryStringQueryBuilder qS = null;
        SearchRequestBuilder srb = null;
        SearchResponse response  = null;
        
        System.out.println("==================================================================================================");
        System.out.println("index script : "+ client.prepareGetIndexedScript("files", "file"));
        System.out.println("==================================================================================================");
        
        File file = new File("C:/dev/search/output/Integrated_Search_Engine.src");
        FileReader reader = new FileReader(file);
        System.out.println("==================================================================================================");
        System.out.println("content:"+ IOUtils.toString(reader));
        System.out.println("==================================================================================================");
        
        reader = new FileReader(file);
        /*
        BulkRequestBuilder blkreq = client.prepareBulk();
        blkreq.add(client.prepareIndex("files", "file", "3")
        		.setSource(XContentFactory.jsonBuilder()
        				.startObject()
        				.field("author","d1")
        				.field("path",file.getAbsolutePath())
        				.field("title",file.getName())
        				.field("content",IOUtils.toString(reader))
        				.endObject()
        				)
        		).execute().actionGet();
        */
        IndexRequestBuilder idxreqb = client.prepareIndex("files", "file");
        idxreqb.setSource(XContentFactory.jsonBuilder()
        		.startObject()
        		.field("author","e1")
        		.field("path",file.getAbsolutePath())
        		.field("title",file.getName())
        		.field("content",IOUtils.toString(reader))
        		.endObject()
        		).execute().actionGet();
        
        
        GetResponse getresponse = client.prepareGet("files", "file", "3").execute().actionGet();
        
        Map<String, Object> source = getresponse.getSource();
        
        System.out.println("==================================================================================================");
        System.out.println("index: " + getresponse.getIndex());
        System.out.println("type: " + getresponse.getType());
        System.out.println("id: " + getresponse.getId());
        System.out.println("version: " + getresponse.getVersion());
        System.out.println(source);
        System.out.println("==================================================================================================");
        
        client.close();
    	assertTrue( true );
    }
}
