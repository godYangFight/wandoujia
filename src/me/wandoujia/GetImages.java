package me.wandoujia;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GetImages 
{

	private int coreCpuNum;
	private ExecutorService exec;
	private CompletionService completionService;
	
	final static String root=System.getProperty("user.dir");
	File file;
	ArrayList<String> arrayList;
	File fileTestFile;
	
	
	
	public GetImages()
	{
		arrayList=new ArrayList<String >();
		//coreCpuNum=10;
		coreCpuNum=Runtime.getRuntime().availableProcessors();
		exec=Executors.newFixedThreadPool(coreCpuNum);
		completionService=new ExecutorCompletionService(exec);
	}
	public class GetRun implements Callable
	{
		private int start;
		private int end;
		public GetRun(int start, int end)
		{
			this.start=start;
			this.end=end;
		}

		@Override
		public Object call() throws Exception 
		{
			for(int j=start;j<end;j++)
			{
				for(int i=0;i<arrayList.size();i++)
		    	{
		    		System.out.println("Enter get()");
		    		String html="";
		        	file=new File(root+"/html/"+arrayList.get(i)+"/"+arrayList.get(i)+".html");
		        	fileTestFile=new File(root+"/html/"+arrayList.get(i)+"/"+"0.png");
		        	
		        	if(fileTestFile.exists())
		        	{
		        		continue;
		        	}
		        	
		        	
		        	if(file.exists())
		        	{
		        		BufferedReader br=null;
		            	
		            	try
		            	{
		            		br=new BufferedReader(new FileReader(file));
		            		String tempString=null;
		            		tempString=br.readLine();		            		
		            		html=tempString+"\n";
		            		while((tempString=br.readLine())!=null)
		            		{
		            			html=html+tempString+"\n";
		            		}
		            		br.close();
		            	}
		            	catch(IOException e)
		            	{
		            		e.printStackTrace();
		            	}
		                org.jsoup.nodes.Document doc=Jsoup.parse(html);
		                String imagesPath=null;
		                
		                System.out.println(arrayList.get(i));
		                
		                int count=0;
		                String folderPath=root+"/html/"+arrayList.get(i)+"/";
		                String fileName=folderPath+count+".png";
		                //获取图片链接
		                Element srcLinks=doc.select("div").select("[class=app-icon]").select("img[src$=.png]").first();
		                imagesPath=srcLinks.attr("src");
		                System.out.println("enter");
		                System.out.println("visited path :"+imagesPath);
		                getImages(imagesPath,fileName);
		                count++;
		                Elements snapshotLinks=doc.select("div").select("[class=screenshot]").select("img[src$=.jpeg]");
		                for(Element links:snapshotLinks)
		                {
		                	fileName=folderPath+count+".jpeg";
		                	imagesPath=links.attr("src");
		                	System.out.println("visited path:"+imagesPath);
		                	getImages(imagesPath,fileName);
		                	count++;
		                }
		        
		        	}
		        	
		            
		    	}
			}
			return null;
		}
		
	}
	public void GetAllPackages()
	{
		File fileGetAllPackages=new File(root+"/newAllPackages.txt");
        BufferedReader br=null;
    	
    	try
    	{
    		br=new BufferedReader(new FileReader(fileGetAllPackages));
    		String tempString=null;
    		
    		while((tempString=br.readLine())!=null)
    		{
    			arrayList.add(tempString);
    		}
    		br.close();
    	}
    	catch(IOException e)
    	{
    		e.printStackTrace();
    	}
	}	
    
    public static void getImages(String urlPath,String fileName) throws Exception
    {
    	URL url1=new URL(urlPath);
    	
		HttpURLConnection conn=(HttpURLConnection)url1.openConnection(); 
    	conn.setRequestMethod("GET");
    	conn.setReadTimeout(6*10000);
    	if(conn.getResponseCode()<10000)
    	{
    		InputStream inputStream=conn.getInputStream();
    		byte[] data=readStream(inputStream);
    		
    		//if(data.length>(1024*10))
    		//{
    		FileOutputStream outputStream=new FileOutputStream(fileName);
    		outputStream.write(data);
    		outputStream.flush();
    		outputStream.close();
    		//}
    	}
    }
    
    public static byte[] readStream(InputStream inputStream) throws IOException
    {
    	ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    	byte[] buffer = new byte[1024];
    	int len = -1;
    	while((len = inputStream.read(buffer)) !=-1)
    	{
    		outputStream.write(buffer, 0, len);
    	
    	 }
    	outputStream.close();
    	inputStream.close();
    	return outputStream.toByteArray();
    	
    }
    
    public void close()
    {
    	exec.shutdown();
    }
    public void GetImagesRun()
    {
    	GetAllPackages();
    	int start;
		int end;
		int increment; 
		for(int i=0;i<coreCpuNum;i++)
		{
			increment=arrayList.size()/coreCpuNum+1;
			start=i*increment;
			end=start+increment;
			if(end>arrayList.size())
			{
				end=arrayList.size();
			}
			
			GetRun getRun=new GetRun(start,end);
			if(!exec.isShutdown())
			{
				completionService.submit(getRun);
			}
		}
		try
		{
			for(int i=0;i<coreCpuNum;i++)
				completionService.take();
		} 
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		close();
		
    }
    
    

}
