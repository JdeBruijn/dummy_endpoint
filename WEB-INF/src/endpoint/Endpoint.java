/*
Jon de Bruijn
2023-07-19
Dummy endpoint for testing.
Receives and stores POST request data.
*/



package endpoint;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.ResultSetMetaData;

import java.io.IOException;
import java.io.InputStreamReader;

import java.util.logging.Logger;
import java.util.LinkedList;
import java.util.Set;
import java.util.Enumeration;

import java.lang.Math;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.servlet.ServletException;

@MultipartConfig
public class Endpoint extends HttpServlet
{
	private static final String class_name="Endpoint";
	private static final Logger log = Logger.getLogger(class_name);

	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
	{
		
		returnData(false, null, resp, "Invalid method.");
		return;

	}//doGet().

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
	{
		Enumeration<String> req_header_names = req.getHeaderNames();
		JSONObject headers_json = new JSONObject();
		while(req_header_names.hasMoreElements())
		{
			String name = req_header_names.nextElement();
			String header = req.getHeader(name);
			headers_json.put(name, header);
		}//while().

		log.info(class_name+".doPost(): headers = "+headers_json.toString());//debug**

		JSONObject body = null;
		try
		{
			InputStreamReader part_reader = new InputStreamReader(req.getInputStream());
			JSONParser parser = new JSONParser();
			body = (JSONObject) parser.parse(part_reader);
		}//try
		catch(IOException | ParseException pe)
		{
			body = new JSONObject();
			log.severe(class_name+" Exception while trying to read request body:\n"+pe); 
			//returnData(false, null, resp, "Failed to extract request body");
			//return;
		}//catch().

		log.info(class_name+".doPost(): body = "+body.toString());//debug**

		JSONObject all_data = new JSONObject();
		all_data.put("headers",headers_json);
		all_data.put("body",body);

		String file_name = "/var/lib/tomcat/webapps/dummy_endpoint/saved_requests/"+StaticStuff.getUTCDateString()+".json";

		StaticStuff.writeToFile(all_data.toString(),file_name);


		if( body==null || body.size()<=0)
		{
			returnData(false, null, resp, "No data found in request body.");
			return;
		}//if.

		returnData(true,null,resp,"All good");
	}//doPost().


	private void returnData(boolean success, JSONObject json_data, HttpServletResponse resp, String message)
	{
		if(json_data==null)
		{json_data = new JSONObject();}

		if(message==null)
		{message="";}

		json_data.put("success", success);
		json_data.put("message",message);
		
		try
		{
			resp.setHeader("Access-Control-Allow-Origin","*");
			resp.getWriter().println(json_data.toString());
		}//try.
		catch(IOException ioe)
		{log.severe(class_name+" IO Exception while trying to return data:\n"+ioe);}
	}//returnData().

}//class Endpoint.
