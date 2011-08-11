package org.jasig.portlet.notice.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jasig.portlet.notice.serviceresponse.NotificationResponse;
import org.jasig.portlet.notice.serviceresponse.iface.NotificationResponseService;
import org.jasig.portlet.notice.serviceresponse.iface.exceptions.NotificationResponseServiceException;
import org.jasig.portlet.notice.source.NotificationIdentifier;

import net.sf.json.JSON;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

public class NotificationDemoService implements NotificationResponseService{

	private String demoFilename = "DemoNoticationResponse.dat";
	
	/**
	 * Returns the name of the service.
	 * @return String.
	 */
	public String getName()
	{
		return "DemoService";
	}

	/**
	 * Set the filename of the demo data.
	 * @param filename is the demo filename.
	 */
	public void setFilename(String filename)
	{
		demoFilename = filename;
	}

    /**
     * Retrieves a single ServiceRequest by uid for the requester from the configured DAO
     * 
     * @param partyNumber
     * @param username
     * @param sourceIdentifier
     * @return
     * @throws ServiceRequestServiceException
     */
    public NotificationResponse getResponse(String partyNumber, String username, NotificationIdentifier sourceIdentifier) throws NotificationResponseServiceException
    {
    	List<NotificationResponse> list = getAllResponses(partyNumber, username);
    	
    	if(list != null && !list.isEmpty())
    	{
    		//simply return the first and only demo item
    		return list.get(0);
    	}
    	else
    	{
    		return null;
    	}
    }
    
    /**
     * Retrieves current service requests for requester
     * 
     * @param partyNumber
     * @param username
     * @return List of service requests or an empty list
     */
    public List<NotificationResponse> getCurrentResponses(String partyNumber, String username)
    {
    	return getAllResponses(partyNumber, username);
    }
    
    /**
     * Retrieves all available service requests for requester
     * 
     * @param partyNumber
     * @param username
     * @return List of service requests or an empty list
     */
    public List<NotificationResponse> getAllResponses(String partyNumber, String username)
    {
    	List<NotificationResponse> list = new ArrayList<NotificationResponse>();
  	
    	NotificationResponse response = readFromFile(demoFilename);
    	
    	if(response != null)
    	{   	
        	list.add(response);	
    	}
    	
    	return list;
    }

	/**
	 * Serialize the given instance to JSON data and write it to a file.
	 *
	 * @param request is the NotificationRequest instance to be serialized to a file.
	 * @param filename is the path and name of the file to be written.
	 * @return boolean, false if the data write fails.
	 */
	public boolean writeToFile(NotificationResponse request, String filename)
	{
		try
		{
			JSON json = JSONSerializer.toJSON(request.toMap());
			String data = json.toString(1);
			
			FileOutputStream fos = new FileOutputStream(filename);
			fos.write(data.getBytes());
			fos.close();
			return true;
		}
		catch(JSONException je)
		{
			je.printStackTrace();
			return false;
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
			return false;
		}
	}

	/**
	 * De-serialize the given JSON formatted file back into a object.
	 *
	 * @param filename is the path and name of the file to be read.
	 * @return NotificationRequest, null if the de-serialization fails.
	 */
	public NotificationResponse readFromFile(String filename)
	{
		try
		{
			InputStream fis = getClass().getClassLoader().getResourceAsStream(demoFilename);
			int available = fis.available();
			byte[] bytes = new byte[available];
			fis.read(bytes);
			fis.close();

			return NotificationResponse.fromJson(new String(bytes));
		}
		catch(JSONException je)
		{
			je.printStackTrace();
			return null;
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
			return null;
		}
	}
}
