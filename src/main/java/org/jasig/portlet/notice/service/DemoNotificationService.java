package org.jasig.portlet.notice.service;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;

import javax.portlet.PortletRequest;

import org.jasig.portlet.notice.response.NotificationResponse;
import org.jasig.portlet.notice.service.exceptions.NotificationServiceException;
import org.jasig.portlet.notice.service.iface.INotificationService;

import net.sf.json.JSON;
import net.sf.json.JSONException;
import net.sf.json.JSONSerializer;

/**
 * This is a simple demo service provider. It reads data from
 * a file and returns it.
 */
public class DemoNotificationService implements INotificationService{

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
     * Retrieves all available service requests for requester
     * 
     * @param partyNumber
     * @param username
     * @return List of service requests or an empty list
     */
    public NotificationResponse getNotifications(PortletRequest req)
    throws NotificationServiceException
    {
    	return readFromFile(demoFilename);
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
			InputStream fis = getClass().getClassLoader().getResourceAsStream(filename);
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
