package me.wandoujia;





import java.awt.font.NumericShaper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;








import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


 

public class TakeAllApp 
{
	
	private int coreCpuNum;
	private ExecutorService exec;
	private CompletionService completionService;
	
	public  static Map<String,Integer> map;
	private static FileWriter Details;
	private static ArrayList<String> arrayListAllPackage;
	private static ArrayList<Integer> arrayListClassify;
	private static File folder;
	private static File file;
	
	private final static String root=System.getProperty("user.dir");
	static String []classify={"休闲时间","跑酷竞速","宝石消除","网络游戏","动作射击","扑克棋牌","儿童益智","塔防守卫","体育格斗","角色扮演","经营策略"};
	
	public TakeAllApp()
	{
		
		map=new HashMap<String,Integer>();
		arrayListClassify=new ArrayList<Integer>();
		arrayListAllPackage=new ArrayList<String>();
	    //coreCpuNum=10;
		coreCpuNum=Runtime.getRuntime().availableProcessors();
		exec=Executors.newFixedThreadPool(coreCpuNum);
		completionService=new ExecutorCompletionService(exec);
		
		folder=new File(root+"/html");
		if(!folder.isDirectory())
		{
			folder.mkdir();
		}
		
	}
	
	public String[] getUrls()
	{
		final int urlSize=33;
		String urls[]=new String[urlSize];
		File fileUrl=new File(root+"/urls.txt");
		if(!fileUrl.exists())
		{
			System.err.println("File urls.txt do not exist !!");
		}
		BufferedReader brUrl=null;
		try
		{
			brUrl=new BufferedReader(new FileReader(fileUrl));
			String tempString="";
			int line=0;
			while((tempString=brUrl.readLine())!=null)
			{
				urls[line]=tempString;
				line++;
			}
			brUrl.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return urls;
	}
	
		
	static class TakeRun implements Callable
	{
		private String urls[];
		private String url="";
		private int start;
		private final int max;
		private int end;
		private int begin;
		public TakeRun(String urls[],int begin,int end)
		{
			this.urls=urls;
			this.max=12;
			this.begin=begin;
			this.end=end;
			start=0;
		}
		
		@Override
		public Object call() throws Exception 
		{
			int fileNumber=0;
			boolean judg;
			for(int i=begin;i<end;)
			{
				while(true)
				{
					
				    url=urls[i]+max+urls[i+1]+start+urls[i+2];
				    
				    judg=take(url,fileNumber);
				   
				    start+=max;
				    if(judg==false)
				    	break;
				}
				start=0;
				i+=3;
				fileNumber++;
			}
			return null;
		}
	
	}
	
	
	public void takeAllAppRun()
	{
		String urls[]=getUrls();
		System.out.println("urls.lenth="+urls.length);
		
		int start=0;
		int increment;
		int end;
		for(int i=0;i<coreCpuNum;i++)
		{
			
			increment=urls.length/coreCpuNum+2;
			//increment=urls.length/coreCpuNum;
			start=i*increment;
			end=start+increment;
			if(end>urls.length)
			{
				end=urls.length;
			}
			TakeRun takeRun=new TakeRun(urls,start,end);
			//FutureTask task=new FutureTask(takeRun);
			if(!exec.isShutdown())
			{
				//exec.submit(takeRun);
				completionService.submit(takeRun);
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
		
		System.out.println(map.size());
		close();
		
	}
	
	public void close()
	{
		exec.shutdown();
	}
	
	public Map<String,Integer> getHashMap()
	{
		return map;
	}
	
	
	
	
	
	public static Boolean take(String url,int n)
	{
		HttpUtil httpUtil=new HttpUtil();
		String html=httpUtil.get(url);
		if(url==null||url.length()==0)
		{
			return false;
		}
		if(httpUtil.getJudg().compareTo("false")==0)
		{
			httpUtil.setJudg("true");
			return false;					
		}
		
		Document doc=Jsoup.parse(html);
		String body=doc.select("body").text(); //获得json数据
		
        int location=0;
		for(int i=0;i<body.length();i++)
		{
			if(body.charAt(i)=='(')
			{
				location=i;
				break;
			}
		}
		body=body.substring(location,body.length()-1);
		String jsonContent=body.substring(2,body.length()-2);
		
	
		try
		{
			JSONObject jo=new JSONObject(jsonContent);
			JSONArray jsonArray=jo.getJSONArray("apps");
			
			for(int i=0;i<jsonArray.length();i++)
			{
				JSONObject apps=(JSONObject) jsonArray.get(i);
				String packageName=(String) apps.getString("packageName");
			    map.put(packageName, n);
				arrayListAllPackage.add(packageName);
				arrayListClassify.add(n);
			}
			
		} 
		catch (JSONException e) 
		{
			
			e.printStackTrace();
		}
		
		return true;
	}
	

	
	
	
	
	
	public static void  details()
	{
		
		java.util.Iterator<Entry<String, Integer>> iter = map.entrySet().iterator();
		
		try
		{
			Details.append("Pkg Name,");
			Details.append("Name,");
			Details.append("Size,");
			Details.append("Download,");
			Details.append("Like,");
			Details.append("Comment,");
			Details.append("Update Time,");
			Details.append("Compy,");
			Details.append("Tag,");
			Details.append("Classify,\n");
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		while(iter.hasNext())
		{
			Map.Entry<String,Integer> entry=(Map.Entry<String,Integer>) iter.next();
			String html=null;
			System.out.println(entry.getKey());
			file=new File(root+"/html/"+entry.getKey()+"/"+entry.getKey()+".html");
			if(!file.exists())
			{
				continue;
			}
			BufferedReader br=null;
			String tempString=null;
			try
			{
				br=new BufferedReader(new FileReader(file));
				tempString=br.readLine();
				html=tempString+"\n";
				while((tempString=br.readLine())!=null)
				{
					html=html+tempString+"\n";
				}
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
			
			
			org.jsoup.nodes.Document doc=Jsoup.parse(html);
			Element size=doc.select("div").select("dd").select("meta").select("[itemprop=fileSize]").first();
			String sz=size.attr("content");
	    
			Element tag=doc.select("div").select("dd").select("[class=tag-box]").first();
		    Element updateTime=doc.select("div").select("time").first();
		    Element name =doc.select("div").select("span").select("[class=last]").first();
		    String ne=name.text();
		    ne="\""+ne+"\"";
		    
		    Element userDownloads=doc.select("div").select("span").select("[class=item]").select("[itemprop=interactionCount]").first();
		    String ud=userDownloads.attr("content");
		    String uds[]=ud.split(":");
		    Element love=doc.select("div").select("span").select("[class=item love]").select("i").first();
		    
		    Element comment=doc.select("div").select("a").select("[class=item last comment-open]").select("i").first();
		    
		    Elements compy=doc.select("div").select("dd").select("[itemprop=author]").select("span").select("[itemprop=name]");
		    Element cy = null;
	        for(Element e:compy)
	        {
	        	cy=e;
	        }
		    try
		    {
		    	Details.append(entry.getKey()+",");
		    	Details.append(ne+",");
		    	Details.append(sz+",");
		    	Details.append(uds[1]+",");
		    	Details.append(love.text()+",");
		    	Details.append(comment.text()+",");
		    	Details.append(updateTime.text()+",");
		    	
		    	if(cy==null)
		    	{
		    		Details.append("     ,");
		    	}
		    	else
		    	{
		    		String c=cy.text();
		    		
		    		c="\""+c+"\"";
		    		
		    		Details.append(c+",");
		    	}
		    	
		    	Details.append(tag.text()+",");
		    	
		    	for(int i=0;i<arrayListAllPackage.size();i++)
		    	{
		    		if((entry.getKey().compareTo((String) arrayListAllPackage.get(i)))==0)
		    		{
		    			
		    			Details.append(classify[arrayListClassify.get(i)]+";");
		    		}
		    	}
		    	Details.append(",\n");
		    	
		    	
		    	
		    	
		    }
		    catch(IOException e)
		    {
		    	e.printStackTrace();
		    }
		     
		     
		}
		try
		{
			Details.flush();
			Details.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	
	
	
	public static void main(String[] args)
	{
		System.out.println("Enter main");
		TakeAllApp takeAllApp=new TakeAllApp();
		takeAllApp.takeAllAppRun();
		System.out.println("TakeAllApp is over !");
		System.out.println("\n\n");
		
		
		GetHtml getHtml=new GetHtml();
		getHtml.setMap(takeAllApp.getHashMap());
		getHtml.getHtmlRun();
		System.out.println("GetHtml finish !");
		System.out.println("\n\n");
		
		GetImages getImages=new GetImages();
		getImages.GetAllPackages();
		System.out.println("GetImages finish !");
		System.out.println("\n\n");
		Photo photo=new Photo();
		try
		{
			photo.getMd5();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Photo finish !");
		System.out.println("\n\n");
		SqlOperator sqlOperator=new SqlOperator();
		try 
		{
			sqlOperator.quryChange();
			sqlOperator.difference();
		} 
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("SqlOperator finish !");
		
	}


}
