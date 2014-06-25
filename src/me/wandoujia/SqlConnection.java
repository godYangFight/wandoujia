package me.wandoujia;


import java.net.URLEncoder;
import java.sql.*;
import java.io.*;
public class SqlConnection 
{
	public static Connection conn;   // connection
	public static Statement st;      //statement 
	public static String temp;
	
	SqlConnection()
	{
		conn=null;
		st=null;
	}
	
	public static Connection getConnection()
	{
		conn=null;
		try
		{
			Class.forName("com.mysql.jdbc.Driver"); //mysql Driver
			conn=DriverManager.getConnection(
					"jdbc:mysql://localhost:3307/MyForum?useUnicode=true&amp;characterEncoding=UTF-8" ,"root","rabbithole");
		}
		catch(Exception e)
		{
			System.out.println("connecting database error !"+e.getMessage());
		}
		return conn;
	}
	
	
	public static boolean insert(String sql)
	{
		conn=getConnection();
		try
		{
			
			st=(Statement) conn.createStatement();  
			int count=st.executeUpdate(sql);
			System.out.println("Insert into the user "+count+"data sentence");
			conn.close();
			return true;
		}
		catch(SQLException e)
		{
			System.out.println("Insert data error !"+e.getMessage());
		}
		finally
		{
			if(st!=null)
			{
				try 
			    {
					st.close();
				} 
			    catch (SQLException e)
			    {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return false;
		
	}
	
	
	public static boolean update(String sql)
	{
		conn=getConnection();
		try
		{
			
			System.out.println(sql);
			st=(Statement) conn.createStatement();
			int count=st.executeUpdate(sql);
			System.out.println("user update"+ count +"data sentence");
			conn.close();
			return true;
		}
		catch(Exception e)
		{
			System.out.println("Update error!"+e.getMessage());
			return false;
		}
		finally
		{
			if(st!=null)
			{
				try 
			    {
					st.close();
				} 
			    catch (SQLException e)
			    {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
	
	
	public static ResultSet query(String sql)
	{
		conn=getConnection();
		try
		{
			st=(Statement) conn.createStatement();
			
			ResultSet rs=st.executeQuery(sql);
			
			return rs;
		}
		catch(SQLException e)
		{
			System.out.println("query error !"+e.getMessage());
		}
		
		return null;
		
	}
	
	
	public static void delete(String sqlString)
	{
		conn=getConnection();
		try
		{
			
			st=(Statement) conn.createStatement();
			int count=st.executeUpdate(sqlString);
			System.out.println("Delete from user"+count+"data sentence");
			conn.close();
		}
		catch(SQLException e)
		{
			System.out.println("Delete error !"+e.getMessage());
		}
		finally
		{
			if(st!=null)
			{
				try 
			    {
					st.close();
				} 
			    catch (SQLException e)
			    {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
