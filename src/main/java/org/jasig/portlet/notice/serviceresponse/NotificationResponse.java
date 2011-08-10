package org.jasig.portlet.notice.serviceresponse;

import org.jasig.portlet.notice.source.NotificationIdentifier;

import java.io.Serializable;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSON;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

public class NotificationResponse implements Serializable {
	private static final long serialVersionUID = 1L;

	private List<NotificationCategory> categories = new ArrayList<NotificationCategory>();
	private List<NotificationError> errors = new ArrayList<NotificationError>();

	public NotificationResponse(){}

	public NotificationResponse(
			List<NotificationCategory> categories,
			List<NotificationError> errors){
		this.categories = categories;
		this.errors = errors;
	}

	/**
	 * Write the instance data to a JSON data String.
	 *
	 * @return String, null if the data is invalid.
	 */
	public static String toJson(NotificationResponse request)
	{
		try
		{
			JSON json = JSONSerializer.toJSON(request.toMap());
			return json.toString(1);
		}
		catch(JSONException je)
		{
			je.printStackTrace();
			return null;
		}
	}

	/**
	 * Write the instance data to a JSON data file.
	 *
	 * @param data the JSON data string.
	 * @return NotificationRequest, null if the JSON data is invalid.
	 */
	public static NotificationResponse fromJson(String data)
	{
		NotificationResponse request = null;
		try
		{
			//create a map that is used to convert the JSON data back into a class object
			Map<String, Object> convertMap = new HashMap<String, Object>();
			convertMap.put("errors", NotificationError.class);
			convertMap.put("categories", NotificationCategory.class);
			convertMap.put("entries", NotificationEntry.class);
			convertMap.put("source", NotificationIdentifier.class);

			JSONObject json = JSONObject.fromObject(data);
			request = (NotificationResponse)JSONObject.toBean(json, NotificationResponse.class, convertMap);
		}
		catch(JSONException je)
		{
			je.printStackTrace();
		}

		return request;
	}
	
	public Map<String, Object> toMap()
	{
		Map<String, Object> map = new HashMap<String, Object>();

		for(int i = 0; i < categories.size(); i++)
			map.put("categories", categories);

		for(int j = 0; j < errors.size(); j++)
			map.put("errors", errors);

		return map;
	}

	public List<NotificationCategory> getCategories() {
		return categories;
	}

	public void setCategories(List<NotificationCategory> categories) {
		this.categories = categories;
	}

	public List<NotificationError> getErrors() {
		return errors;
	}

	public void setErrors(List<NotificationError> errors) {
		this.errors = errors;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer(
				"org.jasig.portlet.notice.serverresponse.NotificationRequest\n");

		for(NotificationCategory category : categories)
			buffer.append(category.toString());

		for(NotificationError error : errors)
			buffer.append(error.toString());

		return buffer.toString();
	}
}
