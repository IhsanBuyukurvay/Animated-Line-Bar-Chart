package sample;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLHandler extends DefaultHandler{

	private static final String TITLE = "title";
	private static final String XLABEL = "xlabel";
	private static final String RECORD = "record";
	private static final String FIELD = "field";
	private static final String NAME = "Name";
    private static final String COUNTRY = "Country";
    private static final String YEAR = "Year";
    private static final String VALUE = "Value";
    private static final String CATEGORY = "Category";

    private StringBuilder elementValue;
    private Data data;
    private String value;
    
	@Override
	public void startDocument() throws SAXException {
		data = new Data();
        data.recordList = new ArrayList<>();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		switch (qName) {
	        case TITLE:
	        	elementValue = new StringBuilder();
	            break;
	        case XLABEL:
	        	elementValue = new StringBuilder();
	            break;
	        case RECORD:
	        	data.recordList.add(new Record());
	            break;
	        case FIELD:
	        	elementValue = new StringBuilder();
				int len = attributes.getLength();
				
				for(int i = 0; i < len; i++) {
		          String sAttrName = attributes.getLocalName(i);
		          if(sAttrName.compareTo("name") == 0) {
		            String sVal = attributes.getValue(i);
		            switch (sVal) {
			          case NAME:
			        	value = NAME;
			        	break;
			          case COUNTRY:
			        	value = COUNTRY;
			        	break;
			          case YEAR:
			        	value = YEAR;
			        	break;
			          case VALUE:
			        	value = VALUE;
			        	break;
			          case CATEGORY:
			        	value = CATEGORY;
			        	break;
		            }
		          }
				}
				break;
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		switch (qName) {
			case TITLE:
				getData().setTitle(elementValue.toString());
	            break;
	        case XLABEL:
	        	getData().setXLabel(elementValue.toString());
	            break;
	        case FIELD:
	        	switch (value) {
			        case NAME:
			        	latestRecord().setName(elementValue.toString());
			        	break;
			        case COUNTRY:
			        	latestRecord().setCountry(elementValue.toString());
			        	break;
			        case YEAR:
			        	latestRecord().setYear(elementValue.toString());
			        	break;
			        case VALUE:
			        	latestRecord().setValue(elementValue.toString());
			        	break;
			        case CATEGORY:
			        	latestRecord().setCategory(elementValue.toString());
			        	break;
	        	}
	        	break;
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (elementValue == null) {
            elementValue = new StringBuilder();
        } else {
            elementValue.append(ch, start, length);
        }
	}
	
	private Record latestRecord() {
        List<Record> recordList = data.recordList;
        int latestRecordIndex = recordList.size() - 1;
        return recordList.get(latestRecordIndex);
    }
	
	public Data getData() {
        return data;
    }
}
