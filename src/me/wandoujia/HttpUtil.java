package me.wandoujia;



import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;

import org.apache.http.client.cache.HttpCacheContext;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.ManagedClientConnection;
import org.apache.http.entity.BufferedHttpEntity;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.CachingHttpClients;
import org.apache.http.impl.conn.PoolingClientConnectionManager;

import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;




/*
 * HttpClient 4.3
 * get()  返回对象为：String
 * */

public class HttpUtil 
{
	private static CloseableHttpClient httpClient;
	private static HttpGet httpGet;
	private static CloseableHttpResponse response;
	private String judg;
	HttpUtil()
	{
		httpClient=HttpClients.createDefault();
		httpGet=null;
		response=null;
		judg="true";              //用于链接不上的判断
		
	}
	public String getJudg()
	{
		return judg;
	}
	public void setJudg(String str)
	{
		judg=str;
	}
	public String get(String url)                        
	{
		System.out.println(url);
		httpGet=new HttpGet(url);
		httpGet.setHeader("User-Agent","Mozilla/5.0 (X11; Linux i686) AppleWebKit/534.30 (KHTML, like Gecko) Chrome/12.0.742.91 Safari/534.30");
		
		
		ConnectionKeepAliveStrategy myStrategy=new ConnectionKeepAliveStrategy()
		{
			public long getKeepAliveDuration(HttpResponse response,HttpContext context)
			{
				HeaderElementIterator it=new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
				while(it.hasNext())
				{
					org.apache.http.HeaderElement he=it.nextElement();
					String  param=he.getName();
					String value=he.getValue();
					if(value!=null&&param.equalsIgnoreCase("timeout"))
					{
						try
						{
							return Long.parseLong(value)*5000;
						}
						catch(NumberFormatException ignore)
						{
							
						}
					}
				}
				HttpHost target=(HttpHost) context.getAttribute(HttpClientContext.HTTP_TARGET_HOST);
				if("www.naughty-server.com".equalsIgnoreCase(target.getHostName()))
				{
					return 5*1000;
				}
				else
				{
					return 30*1000;
				}
				
				
			}

			
		};
		
		CacheConfig cacheConfig = CacheConfig.custom().setMaxCacheEntries(70000)
	            .setMaxObjectSize(70000)
	            .build();
		RequestConfig requestConfig=RequestConfig.custom().setSocketTimeout(50000)
				.setConnectTimeout(80000).setCookieSpec(CookieSpecs.BEST_MATCH).build();
		CloseableHttpClient cachingClient = CachingHttpClients.custom()  
		        .setCacheConfig(cacheConfig)  
		        .setDefaultRequestConfig(requestConfig)
		        .setKeepAliveStrategy(myStrategy)
		        .build();  
		httpGet.setConfig(requestConfig);
		HttpCacheContext context = HttpCacheContext.create();
		try
		{
			
			response=cachingClient.execute(httpGet,context);
			System.out.println(response);
			int statusCode=response.getStatusLine().getStatusCode();
			//System.out.println(statusCode);
			if(statusCode!=HttpStatus.SC_OK)
			{
				judg="false";
				return judg ;
				
			}
			System.out.println("httpUtil.java");
			HttpEntity entity=response.getEntity();
			String html=null;
			if(entity!=null)
			{ 
				//读入内存中
				entity=new BufferedHttpEntity(entity);
				html=EntityUtils.toString(entity,"utf-8");
				
			}
			
			EntityUtils.consume(entity);
			return html;
		}
		catch(IOException e)
		{   
			e.printStackTrace();
		} 
		return null;
		
	}
	
}
