package me.wandoujia;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GameInfo 
{
	private final static String root = System.getProperty("user.dir");
	private ArrayList<String> allPackages;
    private BufferedReader br=null;
    
    private int coreCpuNum;
	private ExecutorService exec;
	private CompletionService completionService;
	
	public GameInfo()
	{
		allPackages=new ArrayList<String>();
		coreCpuNum=10;
		//coreCpuNum=Runtime.getRuntime().availableProcessors();
		exec=Executors.newFixedThreadPool(coreCpuNum);
		completionService=new ExecutorCompletionService(exec);
	}
	
	
	public class GameRun implements Runnable
	{
		private int start;
		private int end;
		public GameRun(int start,int end)
		{
			this.start=start;
			this.end=end;
		}

		@Override
		public void run() 
		{
			try 
			{
				for(int i=start;i<end;i++)
				{
					File file =null;
					String html="";
					System.out.println("Enter game_info");
					
					
					String tempString="";
					System.out.println("Enter compareTo");
					file = new File(root + "/html/" + allPackages.get(i)
							+ "/" + allPackages.get(i) + ".html");
	                
					if (file.exists()) 
					{
							System.out.println("Enter file.exists()");
							

							br = new BufferedReader(new FileReader(file));
							tempString = br.readLine();
							html = tempString + "\n";
							while ((tempString = br.readLine()) != null)
							{
								html = html + tempString + "\n";
							}
							File sizeFile=new File(root + "/html/" + allPackages.get(i)
									+ "/"+"size.txt" );
								
							System.out.println(allPackages.get(i));
								
							if(sizeFile.exists())
							{
								BufferedReader brNew=new BufferedReader(new FileReader(sizeFile));
								String tempBrString="";
								tempBrString=brNew.readLine();
								if(tempBrString!="")
									continue;
							}
							else 
							{
								sizeFile.createNewFile();
									
							}
								
								
								
							File info[]=new File[6];
							String txtName[]={"tag.txt","update.txt","version.txt","need.txt","compy.txt","from.txt"};
							for(int j=0;j<6;j++)
							{
								info[j]=new File(root + "/html/" + allPackages.get(i)
										+ "/"+txtName[j]);
								if(!info[j].exists())
								{
									info[j].createNewFile();
								}
							}
								
								
						    org.jsoup.nodes.Document doc = Jsoup.parse(html);
						    Element size=doc.select("div").select("dd").select("meta").select("[itemprop=fileSize]").first();
						    String sz=size.attr("content");
						    FileWriter fSize=new FileWriter(sizeFile);
						    fSize.append(sz);
						    fSize.flush();
						    fSize.close();
						    
						    Elements infor=doc.select("div").select("[class=infos]").select("dd");
						    for(int i1=1;i1<infor.size();i1++)
						    {
						        FileWriter fi=new FileWriter(info[i1-1]);
						        if(infor.get(i1)==null)
						        {
						        	fi.append("");
						        }
						        else
						        {
						        	fi.append(infor.get(i1).text());
						        }
						        	//fi.append(infor.get(i1).text());
						        fi.flush();
						        fi.close();
						       
						    }
							
							    
						}	
					}
			}
			catch (Exception e) 
			{
				// TODO: handle exception
			}
				
			
		
				
			
		}
		
	}
	
	public void readAllPackages()
	{
		String tempString = null;
		File pc = new File(root + "/newAllPackages.txt");
		try
		{
			br = new BufferedReader(new FileReader(pc));
			while ((tempString = br.readLine()) != null)
			{
				allPackages.add(tempString);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	
	public void GameInfoRun()
	{
		readAllPackages();
		int start;
		int end;
		int increment; 
		for(int i=0;i<coreCpuNum;i++)
		{
			increment=allPackages.size()/coreCpuNum+1;
			start=i*increment;
			end=start+increment;
			if(end>allPackages.size())
			{
				end=allPackages.size();
			}
			
			GameRun gameRun=new GameRun(start,end);
			if(!exec.isShutdown())
			{
				exec.submit(gameRun);
				//completionService.submit(gameRun);
			}
		}
		
		/*try
		{
			for(int i=0;i<coreCpuNum;i++)
			{   
				completionService.take().get();
			}
		} 
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch 
		(ExecutionException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		System.out.println(allPackages.size());
		close();
	}
	
	public void close()
	{
		exec.shutdown();
	}
	
	

}
