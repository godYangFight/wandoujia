package me.wandoujia;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class Photo 
{
	
	final static String root = System.getProperty("user.dir");
	ArrayList allPackages=new ArrayList();
	BufferedReader br = null;

	

	public void readPackages() 
	{
		String tempString = null;
		File pc = new File(root + "/allPackages.txt");
		try {
			br = new BufferedReader(new FileReader(pc));
			while ((tempString = br.readLine()) != null) 
			{
				allPackages.add(tempString);
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	

	public void getMd5() throws IOException 
	{
		readPackages();
		String path = root + "/html/";
		for (int i = 0; i < allPackages.size(); i++)
		{
			File fileImage = new File(path + allPackages.get(i));
			String[] list = fileImage.list();
			// Arrays.sort(list);
			System.out.println(allPackages.get(i));
			String icon = "";
			String screenshot = "";
			
			File newFileImage=new File(root + "/html/" + allPackages.get(i) + "/");
			if(!newFileImage.exists())
			{
				System.out.println( newFileImage + " not exists.." );
				continue;
			}
			File newFile=new File(root + "/html/" + allPackages.get(i) + "/icon.txt");
			
			//跑完一遍之后请去掉注释之后新加的就可以继续跑了
			
			if( newFile.exists())
			{
				BufferedReader brNew=new BufferedReader(new FileReader(newFile));
				String tempBrString="";
				tempBrString=brNew.readLine();
				if(tempBrString!="")
				{
					System.out.println( "already done." );
					continue;
				}
				
			}
			else
			{
				System.out.println( newFile.getAbsolutePath() + " on going.");
			}
			
			for (String l : list)
			{
				String imagePath = root + "/html/" + allPackages.get(i)
						+ "/" + l;
				
				
				if (l.compareTo("0.png") == 0)
				{
				
					String md5 = upload(imagePath, l);

					if (md5 != null)
						icon = md5;
				}
				else if (l.endsWith(".jpeg"))
				{
					String md5 = upload(imagePath, l);

					if (md5 != null)
						screenshot += md5 + ",";
				}
			}
			if( screenshot.length() > 0 )
			{
				screenshot = screenshot.substring(0, screenshot.length()-1);
			}
			
			File f1 = new File(path + allPackages.get(i) + "/icon.txt");
			File f2 = new File(path + allPackages.get(i) + "/photo.txt");
			
					
			FileWriter fw1 = new FileWriter(f1);
			fw1.write( icon );
			fw1.flush();
			fw1.close();
			
		
			FileWriter fw2 = new FileWriter(f2);
			fw2.write( screenshot );
			fw2.flush();
			fw2.close();

		}
		
	}

	public String upload(String imagePath, String filename) 
	{
		byte[] buf = readImage(imagePath);
		HttpResponse httpResponse = uploadImage(buf, filename);
		HttpEntity httpEntity = httpResponse.getEntity();
		if (httpEntity != null) 
		{
			try 
			{
				String html1 = EntityUtils.toString(httpEntity, "UTF-8");
				System.out.println(html1);
				Document doc1 = Jsoup.parse(html1);
				Element md5 = doc1.select("h1").first();
				String m5s = md5.text();
				if(m5s.compareTo("Upload Failed!")==0)
				{
					m5s="";
					return m5s;
				}
				String md5gs[] = m5s.split(":");
				System.out.println(m5s);
				m5s = md5gs[1].replaceAll(" ", "");
				return m5s;

			}

			catch (ParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public String parse(String input)
	{
		if (input == null)
			return "";

		return input.replaceAll("'", "");
	}

	public byte[] readImage(String imagePath) 
	{
		File fileImage = new File(imagePath);
		try {
			FileInputStream fis = new FileInputStream(fileImage);
			byte[] buf = new byte[(int) fileImage.length()];
			fis.read(buf);
			fis.close();
			return buf;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public HttpResponse uploadImage(byte[] bArr, String fileName)
	{

		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		CloseableHttpClient httpClient = httpClientBuilder.build();

		HttpPost httppost = new HttpPost("http://115.28.156.104:4869/image/add");

		MultipartEntityBuilder mpEntity = MultipartEntityBuilder.create();
		mpEntity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		ContentBody cbFile = new ByteArrayBody(bArr, fileName);
		mpEntity.addPart("image", cbFile);

		httppost.setEntity(mpEntity.build());

		HttpResponse response = null;
		try {
			response = httpClient.execute(httppost);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}


		return response;
	}


}
