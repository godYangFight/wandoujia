package me.wandoujia;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


import org.jsoup.Jsoup;

import org.jsoup.nodes.Element;


public class SqlOperator
{
	private static SqlConnection Sql;
	private final static String root = System.getProperty("user.dir");
	private ArrayList<String> allPackages;
	private BufferedReader br = null;
	private static Map<String,String> mapMyForum;
	private static Map<String,String>mapClassification;
	private static final int size=7;
	private static String txtName[]={"size.txt","tag.txt","update.txt","version.txt","need.txt","compy.txt","from.txt"};
	private File info[]=new File[size];
	private String giString[]=new String [size];
	public SqlOperator()
	{
		Sql = new SqlConnection();
		allPackages = new ArrayList<String>();
		mapMyForum =new HashMap<String,String>();
		mapClassification=new HashMap<String,String>();
        
	}

	public void readPackages()
    {
		String tempString = null;
		File pc = new File(root + "/allPackages.txt");
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
	public void readMySqlMyForum()
	{
		String sql="SELECT * FROM myforum_games";
		ResultSet rs=Sql.query(sql);
		String game_url="";
		String game_id="";
		try 
		{
			while(rs.next())
			{
				game_url = rs.getString("game_url");
				game_id = rs.getString("game_id");
				mapMyForum.put(game_id, game_url);
			}
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	public void readClassification()
	{
		String sql="SELECT * FROM classification";
		ResultSet rs=Sql.query(sql);
		String classification_id="";
		String name="";
		try
		{
			while(rs.next())
			{
				classification_id=rs.getString("classification_id");
				name=rs.getString("name");
			}
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	

	


	public void quryChange() throws SQLException, IOException
	{
		readPackages();
		readMySqlMyForum();
		
		String sql = "select * from myforum_games";
		String tempString = null;
		String html = null;
		String downLoadNumber = null;
		String desc = null;
		String longDesc = "";
		String game_url = null;
		String url = null;
		String game_id;
		String category_id;
		
		String icon="";
		String game_photos="";
		String ne="";
		String testGameId="";
		int countNumber=0;
		//boolean sameAs=false;
		//java.util.Iterator<Entry<String, String>> iter = mapMyForum.entrySet().iterator();
		File fileSame=new File(root+"/same.txt");
		if(!fileSame.exists())
		{
			fileSame.createNewFile();
		}
		FileWriter fwSame=new FileWriter(fileSame);
		
		File logCheck=new File(root+"/logChect.txt");
		
		if(!logCheck.exists())
		{
			logCheck.createNewFile();
		}
		FileWriter fwLog=new FileWriter(logCheck,true);
		for (int i = 0; i < allPackages.size(); i++)
		{
			System.out.println("Number :"+i);
			fwLog.append("Number:"+i+"\n");
			
			
			File fjudg=new File(root+"/html/"+allPackages.get(i)+"/"+"size.txt");
			if(fjudg.exists())
			{
				
				for(int j=0;j<size;j++)
				{
					info[j]=new File(root+"/html/"+allPackages.get(i)+"/"+txtName[j]);
					
				}
				
				BufferedReader brInfo[]=new BufferedReader[size];
				for(int k=0;k<size;k++)
				{
					brInfo[k]=new BufferedReader(new FileReader(info[k]));
					String tempInfo="";
					tempInfo=brInfo[k].readLine();
				    giString[k]=tempInfo;
					
				}
			}
			
			
			boolean sameAs=false;
			File file=null;
			
			java.util.Iterator<Entry<String, String>> iter = mapMyForum.entrySet().iterator();
			while ( iter.hasNext() )//&& rs.getInt("game_id")==10 )
			{
				Map.Entry<String,String> entry=(Map.Entry<String,String>) iter.next();
				//testGameId=String.valueOf( rs.getInt("game_id"));
				testGameId=entry.getKey();
				
				//String gameUrl = rs.getString("game_url");
				String gameUrl=entry.getValue();
						
				//System.out.println(gameUrl);
				
				if (gameUrl.length() == 0 || gameUrl.length() < 28)
				{
					continue;
				}
				
				String test = gameUrl.substring(0, 28);
				//System.out.println(test);
				if (test.compareTo("http://www.wandoujia.com/apps/") == 0) 
				{
					url = gameUrl.substring(30, gameUrl.length()-1);
					
				}
				
				else 
				{

					if (test.compareTo("http://www.appchina.com/app/") == 0)
					{
						url = gameUrl.substring(28, gameUrl.length() - 1);

					}
					else 
					{
						test = gameUrl.substring(0, 30);
						
						if (test.compareTo("http://www.wandoujia.com/apps/") == 0) 
						{
							
							url = gameUrl.substring(30, gameUrl.length()-1);
						}

					}
				}

				// url=gameUrl.substring(value,gameUrl.length()-1);
				//System.out.println(url);
								
				if (url.compareTo((String) allPackages.get(i)) == 0) 
				{
					sameAs=true;
					//System.out.println("Enter compareTo");
					game_id = entry.getKey();
					file = new File(root + "/html/" + allPackages.get(i)
							+ "/" + allPackages.get(i) + ".html");
					
					
					
					
					//相同的package 写入  same.txt
					
					fwSame.append((String) allPackages.get(i)+"\n");
					
					if (file.exists()) 
					{
						//System.out.println("Enter file.exists()");
						FileInputStream is = new FileInputStream(file);

						br = new BufferedReader(new InputStreamReader(is,
								"utf-8"));
						tempString = br.readLine();
						html = tempString + "\n";
						while ((tempString = br.readLine()) != null) 
						{
							html = html + tempString + "\n";
						}
						org.jsoup.nodes.Document doc = Jsoup.parse(html);
						Element userDownloads = doc.select("div")
								.select("span").select("[class=item]")
								.select("[itemprop=interactionCount]")
								.first();
						String ud = userDownloads.attr("content");
						String uds[] = ud.split(":");
						ud = uds[1]; // 更新下载量
						downLoadNumber = ud;
						game_url = "http://www.wandoujia.com/apps/"
								+ allPackages.get(i) + "/"; // 更新链接
						Element game_desc = doc.select("div")
								.select("[class=editorComment]")
								.select("[class=con]").first();
						
						//System.out.println(game_url);
						String gd = null;
						if (game_desc == null)
						{
							gd = null;
						} 
						else 
						{
							gd = game_desc.text();
						}
						// String gd=game_desc.text(); //更新短点评
						desc = gd;

						Element game_desc_log = doc.select("div")
								.select("[class=desc-info]")
								.select("[itemprop=description]").first();

						String long_desc = game_desc_log.toString();
						// System.out.println(long_desc);
						String[] ld = long_desc.split("<br />");

						String[] ldOne = ld[0].split(">");
						long_desc = ldOne[1];

						for (int i1 = 1; i1 < ld.length; i1++) 
						{
							long_desc = long_desc + ld[i1];

						}
             
						
						String ldsc[]=long_desc.split("</div");
						
						//System.out.println(ldsc[0]);
						
						longDesc = ldsc[0];
                        if(longDesc.endsWith("\n"))
                        {
                        	longDesc=longDesc.substring(0, longDesc.length()-1);
                        }
						//System.out.println(longDesc.length());
						
						Element name =doc.select("div").select("span").select("[class=last]").first();
					    ne=name.text();
					    
					    BufferedReader br1=null;
					    BufferedReader br2=null;
				    	File fileIcon=new File(root + "/html/" + allPackages.get(i)
								+ "/icon.txt");
				    	File filePhoto=new File(root + "/html/" + allPackages.get(i)
								+ "/photo.txt");
				    	if((!fileIcon.exists())&&(!filePhoto.exists()))
				    		continue;
				    	try
				    	{
				    		br1=new BufferedReader(new FileReader(fileIcon));
				    		
				    		String tempString1=null;
				    		
				    		
				    		while((tempString1=br1.readLine())!=null)
				    		{
				    			icon=tempString1;
				    		}
				    		br1.close();
				    	}
				    	catch(IOException e)
				    	{
				    		e.printStackTrace();
				    	}
				    	try
				    	{
				    		br2=new BufferedReader(new FileReader(filePhoto));
				    		String tempString1=null;
				    		while((tempString1=br2.readLine())!=null)
				    		{
				    			game_photos=tempString1;
				    		}
				    	}
				    	catch(IOException e)
				    	{
				    		e.printStackTrace();
				    	}
				    	String sqlUpdate = "UPDATE myforum_games SET game_desc="
								+ "\'"
								+ parse(desc)
								+ "\'"
								+ ",game_url="
								+ "\'"
								+ game_url
								+ "\'"
								+ ",game_downloads="
								+ downLoadNumber
								+ ",game_icon="
								+ "\'"
								+ icon
								+ "\'"
								+ ",game_photos="
								+ "\'"
								+ game_photos
								+ "\'"
								+ ",game_desc_long="
								+ "\'"
								+ parse(longDesc)
								+ "\'"
								+ ",game_title="
								+ "\'"
								+ parse(ne)
								+ "\'"
								+ ",game_size="
								+ giString[0]
								+ ",game_tag="
								+ "\'"
								+ giString[1]
								+ "\'"
								+ ",game_update="
								+ "\'"
								+ giString[2]
								+ "\'"
								+ ",game_version="
								+ "\'"
								+ giString[3]
								+ "\'"
								+ ",game_need="
								+ "\'"
								+ giString[4]
								+ "\'"
								+ ",game_compy="
								+ "\'"
								+ parse(giString[5])
								+ "\'"
								+ ",game_from="
								+ "\'"
								+ giString[6]
								+ "\'"
								+ " WHERE game_id=" + game_id;
				    	
				    	
				    	//System.out.println(sqlUpdate);
				    	
				    	
				    	
						boolean result = Sql.update(sqlUpdate);
						
						if (result == false)
							return;

								
					}
					

							}
				
		
								
				}
			if(sameAs==false)
			{
				file = new File(root + "/html/" + allPackages.get(i)
						+ "/" + allPackages.get(i) + ".html");

				if (file.exists()) 
				{
					
					//System.out.println("Enter file.exists()");
					//System.out.println(allPackages.get(i));
					
					FileInputStream is = new FileInputStream(file);

					br = new BufferedReader(new InputStreamReader(is,
							"utf-8"));
					tempString = br.readLine();
					html = tempString + "\n";
					while ((tempString = br.readLine()) != null)
					{
						html = html + tempString + "\n";
					}
					
					//System.out.println(html);
					
					
					org.jsoup.nodes.Document doc = Jsoup.parse(html);
					
					Element userDownloads=doc.select("div").select("span").select("[class=item]").select("[itemprop=interactionCount]").first();
			        String ud=userDownloads.attr("content").toString();
			        String uds[]=ud.split(":");
			        ud=uds[1];
			        downLoadNumber=ud;
					if(userDownloads.attr("content")==null)
					{
						downLoadNumber="0";
					}
					
					
					/*String ud = userDownloads.attr("content");
					String uds[] = ud.split(":");
					ud = uds[1]; // 更新下载量
					downLoadNumber = ud;*/
					
					game_url = "http://www.wandoujia.com/apps/"
							+ allPackages.get(i) + "/"; // 更新链接
					Element game_desc = doc.select("div")
							.select("[class=editorComment]")
							.select("[class=con]").first();
					
					//System.out.println(game_url);
					String gd = null;
					if (game_desc == null) 
					{
						gd = null;
					}
					else 
					{
						gd = game_desc.text();
					}
					// String gd=game_desc.text(); //更新短点评
					desc = gd;

					Element game_desc_log = doc.select("div")
							.select("[class=desc-info]")
							.select("[itemprop=description]").first();

					String long_desc = game_desc_log.toString();
					// System.out.println(long_desc);
					String[] ld = long_desc.split("<br />");

					String[] ldOne = ld[0].split(">");
					long_desc = ldOne[1];

					for (int i1 = 1; i1 < ld.length - 1; i1++) 
					{
						long_desc = long_desc + ld[i1];

					}
					
					String ldsc[]=long_desc.split("</div");
					longDesc = ldsc[0];
                    if(longDesc.endsWith("\n"))
                    {
                    	longDesc=longDesc.substring(0, longDesc.length()-2);
                    }
					//longDesc = long_desc;

					//System.out.println(longDesc.length());
					
					Element name =doc.select("div").select("span").select("[class=last]").first();
				    ne=name.text();
				    
				    BufferedReader br1=null;
				    BufferedReader br2=null;
			    	File fileIcon=new File(root + "/html/" + allPackages.get(i)
							+ "/icon.txt");
			    	File filePhoto=new File(root + "/html/" + allPackages.get(i)
							+ "/photo.txt");
			    	if((!fileIcon.exists())&&(!filePhoto.exists()))
			    		continue;
			    	try
			    	{
			    		br1=new BufferedReader(new FileReader(fileIcon));
			    		
			    		String tempString1=null;
			    		
			    		
			    		while((tempString1=br1.readLine())!=null)
			    		{
			    			icon=tempString1;
			    		}
			    		br1.close();
			    	}
			    	catch(IOException e)
			    	{
			    		e.printStackTrace();
			    	}
			    	try
			    	{
			    		br2=new BufferedReader(new FileReader(filePhoto));
			    		String tempString1=null;
			    		while((tempString1=br2.readLine())!=null)
			    		{
			    			game_photos=tempString1;
			    		}
			    	}
			    	catch(IOException e)
			    	{
			    		e.printStackTrace();
			    	}
			    	
			    	String sqlNew="INSERT IGNORE INTO classification(name,text)" +
			    			" VALUES("+
			    			"\'"+
			    			parse(ne)+
			    			"\'"+
			    			","+
			    			"\'"+
			    			parse(desc)+
			    			"\'"+")";
			    			
			    	//System.out.println("sqlNew:"+sqlNew);
			    	boolean result=Sql.insert(sqlNew);
			    	
			    	if(result==false)
			    		return;
			    	
			    	String sqlQuery="SELECT classification_id FROM classification WHERE name="+"\'"+parse(ne)+"\'";
			    	//System.out.println(sqlQuery);
			    	ResultSet rs1=Sql.query(sqlQuery);
			    	rs1.next();
			    	
			    	category_id=rs1.getString("classification_id");
			    	
			    	//System.out.println("category_id:"+category_id);
			    	
			    	
			    	String insertJudy="SELECT * FROM myforum_games WHERE category_id="+category_id;
			    	rs1=Sql.query(insertJudy);
			    	
			    	if(rs1.next()==false)
			    	{
			    		String sqlUpdate ="INSERT INTO myforum_games(game_title,category_id,game_desc,game_url," +
				    			"game_downloads,game_icon,game_photos,game_desc_long,game_size,game_tag,game_update,game_version,game_need,game_compy,game_from)"+
				    			" VALUES("+
				    			"\'"+
				    			parse(ne)+
				    			"\'"+
				    			","+
				    			category_id+
				    			","+
				    			"\'"+
				    			parse(desc)+
				    			"\'"+
				    			","+
				    			"\'"+
				    			game_url+
				    			"\'"+
				    			","+
				    			downLoadNumber+
				    			","+
				    			"\'"+
				    			icon+
				    			"\'"+
				    			","+
				    			"\'"+
				    			game_photos+
				    			"\'"+
				    			","+
				    			"\'"+
				    			parse(longDesc)+
				    			"\'" 
				    			+ ","
								+ giString[0]
								+ ","
								+ "\'"
								+ giString[1]
								+ "\'"
								+ ","
								+ "\'"
								+ giString[2]
								+ "\'"
								+ ","
								+ "\'"
								+ giString[3]
								+ "\'"
								+ ","
								+ "\'"
								+ giString[4]
								+ "\'"
								+ ","
								+ "\'"
								+ parse(giString[5])
								+ "\'"
								+ ","
								+ "\'"
								+ giString[6]
								+ "\'"
				    			+")";
				    	//System.out.println("sqlUpdate :"+sqlUpdate);
				    	boolean ss=Sql.insert(sqlUpdate);
				    	if(ss==false)
				    	{
				    		return ;
				    	}
			    		
			    	}
			    	
					
				}
		    }
			
			
			
		}
		
		fwSame.flush();
		fwSame.close();
	}

	
	
	public String parse(String input) 
	{
		if (input == null)
			return "";
        input=input.replaceAll("&nbsp;", " ");
        input=input.replaceAll("&deg;", "°");
		return input.replaceAll("'", "");
	}

	
	
	public void difference()
	{
		String testGameId="";
		String url="";
		java.util.Iterator<Entry<String, String>> iter = mapMyForum.entrySet().iterator();
		File differ=new File(root+"/difference.txt");
		if(!differ.exists())
		{
			try 
			{
				differ.createNewFile();
			}
			catch (IOException e)
			{
				
				e.printStackTrace();
			}
		}
		
		try
		{
			FileWriter fwDiffer=new FileWriter(differ);
			while ( iter.hasNext() )//&& rs.getInt("game_id")==10 )
			{
				Map.Entry<String,String> entry=(Map.Entry<String,String>) iter.next();
				
				testGameId=entry.getKey();
				
				
				String gameUrl=entry.getValue();
						
				
				
				if (gameUrl.length() == 0 || gameUrl.length() < 28)
				{
					fwDiffer.append(testGameId);
					fwDiffer.append(":");
					fwDiffer.append("\n");
					continue;
				}
				
				String test = gameUrl.substring(0, 28);
				if(test.compareTo("http://www.appchina.com/app/")==0)
				{
					url = gameUrl.substring(28, gameUrl.length() - 1);
					fwDiffer.append(testGameId);
					fwDiffer.append(":");
					fwDiffer.append(url);
					fwDiffer.append("\n");
				}
				//System.out.println(test);
				
			}
			fwDiffer.flush();
			fwDiffer.close();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	


	
}
