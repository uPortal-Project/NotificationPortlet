package org.jasig.portlet.notice;

import java.util.ArrayList;
import java.util.List;

public class NotificationData {
	
	private String serviceKey;
    private List<String> headerRow = new ArrayList<String>();
    private List<List<Object>> dataRows  = new ArrayList<List<Object>>();
    
    
	/**
	 * @return the serviceKey
	 */
	public String getServiceKey() {
		return serviceKey;
	}
	/**
	 * @param serviceKey the serviceKey to set
	 */
	public void setServiceKey(String serviceKey) {
		this.serviceKey = serviceKey;
	}
	/**
	 * @return the headerRow
	 */
	public List<String> getHeaderRow() {
		return headerRow;
	}
	/**
	 * @param headerRow the headerRow to set
	 */
	public void setHeaderRow(List<String> headerRow) {
		this.headerRow = headerRow;
	}
	
	public void addColumnToHeaderRow(String title) {
		this.headerRow.add(title);
	}
	
	
	/**
	 * @return the dataRows
	 */
	public List<List<Object>> getDataRows() {
		return dataRows;
	}
	/**
	 * @param dataRows the dataRows to set
	 */
	public void setDataRows(List<List<Object>> dataRows) {
		this.dataRows = dataRows;
	}
	public void addDataRow(List<Object> dataRow) {
		this.dataRows.add(dataRow);
	}
    
    
    
}