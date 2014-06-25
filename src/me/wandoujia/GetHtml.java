package me.wandoujia;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.wandoujia.GetImages.GetRun;



public class GetHtml 
{
	private int coreCpuNum;
	private ExecutorService exec;
	private CompletionService completionService;
	
	private final static String root=System.getProperty("user.dir");
	private ArrayList<String>arrayList;
	private HttpUtil httpUtil;
	private static Map<String,Integer> map;
	private ArrayList<String> map2array;
	public GetHtml()
	{
		arrayList=new ArrayList<String>();
		httpUtil=new HttpUtil();
		map2array=new ArrayList<String>();
		
		//coreCpuNum=10;
		coreCpuNum=Runtime.getRuntime().availableProcessors();
		exec=Executors.newFixedThreadPool(coreCpuNum);
		completionService=new ExecutorCompletionService(exec);
		
	}
	
	
	public void MapToArrayList()
	{
		java.util.Iterator<Entry<String,Integer>> iter=map.entrySet().iterator();
		while(iter.hasNext())
		{
			Map.Entry<String, Integer> entry=(Map.Entry<String, Integer>) iter.next();
			arrayList.add(entry.getKey());
		}
	}
	
	public class GetRun implements Callable
	{
		private int start;
		private int end;
        public GetRun(int start,int end)
        {
        	this.start=start;
        	this.end=end;
        }
		@Override
		public Object call() throws Exception 
		{
			for(int j=start;j<end;j++)
			{
				BufferedReader br=null;
				String tempString=null;
				File pc=new File(root+"/allPackages.txt");
				FileWriter pcs;
				boolean ex=false;
				String url=null;
				HttpUtil httpUtil=new HttpUtil();
				
				
				if(!pc.exists())
				{
					try 
					{
						pc.createNewFile();
					} 
					catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				try
				{
					br=new BufferedReader(new FileReader(pc));
					while((tempString=br.readLine())!=null)
					{
						arrayList.add(tempString);
					}
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
				
				for(int i=0;i<arrayList.size();i++)
				{
					if(map2array.get(j).compareTo((String) arrayList.get(i))==0)
					{
							ex=true;
					}
				}
				if(ex==false)
				{
						
						
					try
					{    
							
							
						url="http://www.wandoujia.com/apps/"+map2array.get(j);
								
						System.out.println(url);
								
						String html=httpUtil.get(url);
						if((html.compareTo("false"))==0)
						{
							continue;
						}
						else
						{
							try
							  {
								 File folderHtml=new File(root+"/html/"+map2array.get(j));
								 if(!folderHtml.isDirectory())
								 {
										folderHtml.mkdir();
									}
							    
								    pcs=new FileWriter(root+"/allPackages.txt",true);
								    pcs.append(map2array.get(j)+"\n");
								    pcs.flush();
								    pcs.close();
							    }
							    catch (IOException e)
						    	{
						    		// TODO Auto-generated catch block
							    	e.printStackTrace();
							    }
								FileWriter fwHtml;
								fwHtml=new FileWriter(root+"/html/"+map2array.get(j)+"/"+map2array.get(j)+".html");
									
								fwHtml.append(html);
								fwHtml.flush();
								fwHtml.close();
							}
							
							
					    }
						
							
						
						catch(IOException e)
						{
							e.printStackTrace();
						}
					}        
					ex=false;
						
				}
			
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	public void getAllPackages()
	{
		File getAllFile=new File(root+"/allPackages.txt");
		try 
		{
			BufferedReader br=new BufferedReader(new FileReader(getAllFile));
			String tempString="";
			while((tempString=br.readLine())!=null)
			{
				arrayList.add(tempString);
			}
		} 
		catch (FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void close()
	{
		exec.shutdown();
	}
	
	public void setMap(Map<String,Integer> map)
	{
		this.map=map;
	}
	
	
	public void getHtmlRun()
	{
		getAllPackages();
		MapToArrayList();
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
