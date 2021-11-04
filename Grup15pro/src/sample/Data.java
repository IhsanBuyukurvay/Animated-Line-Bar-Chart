package sample;

import java.util.List;

public class Data {
	private String title;
	private String XLabel;
	public List<Record> recordList;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getXLabel() {
		return XLabel;
	}
	public void setXLabel(String xLabel) {
		XLabel = xLabel;
	}
	public List<Record> getRecordList() {
		return recordList;
	}
	public void setRecordList(List<Record> recordList) {
		this.recordList = recordList;
	}
}
