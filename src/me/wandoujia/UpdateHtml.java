package me.wandoujia;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;




public class UpdateHtml 
{
	
	private int coreCpuNum;
	private ExecutorService exec;
	private CompletionService completionService;
	
	private final static String root=System.getProperty("user.dir");
	private ArrayList<String>arrayList;
	private HttpUtil httpUtil;
	
	public UpdateHtml()
	{
		arrayList=new ArrayList<String>();
		httpUtil=new HttpUtil();
		coreCpuNum=10;
		exec=Executors.newFixedThreadPool(coreCpuNum);
		completionService=new ExecutorCompletionService(exec);
	}
	
	public class UpdateRun implements Callable
	{
		private int start;
		private int end;
        public UpdateRun(int start,int end)
        {
			this.start=start;
			this.end=end;
		}
        
		@Override
		public Object call() throws Exception 
		{
			// TODO Auto-generated method stub
			for(int j=start;j<end;j++)
			{
				for(int i=0;i<arrayList.size();i++)
				{
					String urlString="http://www.wandoujia.com/apps/"+arrayList.get(i);
					String htmlString=httpUtil.get(urlString);
					if(htmlString.compareTo("false")==0)
					{
						continue;
					}
					else
					{
						File folder=new File(root+"/html/"+arrayList.get(i)+"/");
						if(!folder.isDirectory())
						{
							folder.mkdir();
						}
						File htmlFile=new File(root+"/html/"+arrayList.get(i)+"/"+arrayList.get(i)+".html");
						if(!htmlFile.exists())
						{
							try 
							{
								htmlFile.createNewFile();
							} 
							catch (IOException e) 
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						try 
						{
							FileWriter fWriter=new FileWriter(htmlFile);
							fWriter.append(htmlString);
							fWriter.flush();
							fWriter.close();
						} 
						catch (IOException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
				}
			}
		    return null;
		}
		
	}  
	
	
	public void getAllPackages()
	{
		File getAllFile=new File(root+"/newAllPackages.txt");
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
	
	public void updateHtmlRun()
	{
		getAllPackages();
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
			
			UpdateRun updateRun =new UpdateRun(start,end);
			if(!exec.isShutdown())
			{
				completionService.submit(updateRun);
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
	
	public void getHtml()
	{
		for(int i=0;i<arrayList.size();i++)
		{
			String urlString="http://www.wandoujia.com/apps/"+arrayList.get(i);
			String htmlString=httpUtil.get(urlString);
			if(htmlString.compareTo("false")==0)
			{
				continue;
			}
			else
			{
				File folder=new File(root+"/html/"+arrayList.get(i)+"/");
				if(!folder.isDirectory())
				{
					folder.mkdir();
				}
				File htmlFile=new File(root+"/html/"+arrayList.get(i)+"/"+arrayList.get(i)+".html");
				if(!htmlFile.exists())
				{
					try 
					{
						htmlFile.createNewFile();
					} 
					catch (IOException e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				try 
				{
					FileWriter fWriter=new FileWriter(htmlFile);
					fWriter.append(htmlString);
					fWriter.flush();
					fWriter.close();
				} 
				catch (IOException e)
				{
					e.printStackTrace();
				}
				
			}
		}
	}

}
