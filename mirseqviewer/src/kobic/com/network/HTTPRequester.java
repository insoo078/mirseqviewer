package kobic.com.network;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.HttpEntity;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import kobic.msb.system.engine.MsbEngine;
 
/**
 *
 * @author insoo078
 */
@SuppressWarnings("deprecation")
public class HTTPRequester {
	private static HTTPRequester requester = new HTTPRequester();
	private final String homeUrl = MsbEngine.getInstance().getSystemProperties().getProperty("msb.http.url");
//	private final String homeUrl = "http://210.218.222.211/msb/";
	
	private CloseableHttpClient client;
	
	public static HTTPRequester getInstance() {
		if( requester != null )
			return requester;
		return new HTTPRequester();
	}
	
	public HTTPRequester() {
		this.client = HttpClients.createDefault();

//		this.client.getParams().setParameter("http.protocol.expect-continue", false);
//		this.client.getParams().setParameter("http.connection.timeout", 15000);
//		this.client.getParams().setParameter("http.socket.timeout", 15000);
	}
	
	public void close() throws IOException {
		this.client.close();
	}

	/********************************************************************************
	 * To get Sequence by locus and organism information
	 * 
	 * @param organism
	 * @param chr
	 * @param start
	 * @param end
	 * @param strand
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public String getReferenceSequence(String organism, String chr, int start, int end, char strand) throws ClientProtocolException, IOException, URISyntaxException {
        String sequence = "";

//        long a = System.currentTimeMillis();
        HttpGet httpget = new HttpGet(this.homeUrl + "/servlet/ChrServlet");
        
//        long b= System.currentTimeMillis();
//        System.out.println( "HttpGet constructor : " + ((double)b-a)/1000);
        
        URIBuilder builder = new URIBuilder(httpget.getURI());
        builder.addParameter("organism", organism);
        builder.addParameter("chr", chr);
        builder.addParameter("start", Integer.toString( start ));
        builder.addParameter("end", Integer.toString( end ));
        builder.addParameter("strand", Character.toString( strand ));
        
//        long c= System.currentTimeMillis();
//        System.out.println( "Http constructor : " + ((double)c-b)/1000);

        URI uri = builder.build();

        httpget.setURI( uri );
        
        long d= System.currentTimeMillis();

        CloseableHttpResponse response = this.client.execute(httpget);

//    	long e= System.currentTimeMillis();
//        System.out.println( "HttpResponse constructor : " + ((double)e-d)/1000);

    	HttpEntity entity = response.getEntity();
    	
//    	long e2= System.currentTimeMillis();
//        System.out.println( "getEntity constructor : " + ((double)e2-e)/1000);


        if (entity != null) {
            InputStream is = entity.getContent();
            StringBuffer sb = new StringBuffer();
            byte[] buf = new byte[4096];
            for (int n; (n = is.read(buf)) != -1;) {
            	sb.append(new String(buf, 0, n));
            }
            sequence = sb.toString().replace("\n", "");

//            entity.consumeContent();
            is.close();
        }
//        long f= System.currentTimeMillis();
//        System.out.println( "After get data from server : " + ((double)f-e2)/1000);

        response.close();
        httpget.releaseConnection();

        return sequence;
	}

	/********************************************************************************
	 * To get organism information (Service organism)
	 * 
	 * @return
	 * @throws Exception
	 */
    @SuppressWarnings("rawtypes")
	public LinkedHashMap<String, String> getOrganismInfo() throws Exception {
		LinkedHashMap<String, String> resultMap = new LinkedHashMap<String, String>();

		ContainerFactory containerFactory = new ContainerFactory(){
			public List<Map<String, String>> creatArrayContainer() {
				return new LinkedList<Map<String, String>>();
			}

			public Map<String, String> createObjectContainer() {
				return new LinkedHashMap<String, String>();
			}                   
		};

        HttpGet httpget = new HttpGet(this.homeUrl + "/servlet/OrganismServlet");

        CloseableHttpResponse response = this.client.execute(httpget);
        HttpEntity entity = response.getEntity();

        if (entity != null) {
            InputStream is = entity.getContent();
            
            StringBuffer sb = new StringBuffer();
            byte[] b = new byte[4096];
            for (int n; (n = is.read(b)) != -1;) {
            	sb.append(new String(b, 0, n));
            }
            String content = sb.toString();

            entity.consumeContent();
            is.close();
            
            JSONParser j = new JSONParser();
			Map o = (Map)j.parse(content, containerFactory);
            
            LinkedList list = (LinkedList)o.get("organisms");
            
            for( Iterator iter=list.iterator(); iter.hasNext(); ) {
            	LinkedHashMap map = (LinkedHashMap)iter.next();
            	resultMap.put( map.get("organism").toString(), map.get("id").toString() );
            }
        }
        
        response.close();
        httpget.releaseConnection();
        return resultMap;
    }
	
	public static void main(String[] args) throws Exception {
//		LinkedHashMap<String, String> map = HTTPRequester.getOrganismInfo();
		HTTPRequester requester = HTTPRequester.getInstance();

		System.out.println( requester.getReferenceSequence("hsa", "chr22", 46509566, 46509648, '+') );
		long b = System.currentTimeMillis();

		requester.close();
	}
}