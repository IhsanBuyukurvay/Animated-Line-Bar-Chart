package sample;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collector;
import static java.util.stream.Collectors.toList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.xml.sax.SAXException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.Scene;


public class Main extends Application {


	private int WINDOW_SIZE = 1;
	private Map<String, XYChart.Series<String, Number>> seriesMap = new HashMap<>();
	private int counter = 0;

	@Override
	public void start(Stage primaryStage) {


		try {
	        Data data = saxParser();
	        Map<String, List<Record>> recordMap = splitDataForCountries(data);
	        Set<String> countries = recordMap.keySet();

			Iterator<String> iter = countries.iterator();
			String firstCountry = iter.next();
	        int recordListSize = recordMap.get(firstCountry).size();


	        var root1 = new HBox();

			Scene scene1 = new Scene(root1,800,600);

			Button button1 = new Button("LineChart");
			button1.setAlignment(Pos.CENTER);
			button1.setPrefSize(400,600);
//			button1.setStyle("-fx-background-color: #708090 ; ");
			
			Button button2 = new Button("Barchart");
			button2.setAlignment(Pos.CENTER);
			button2.setPrefSize(400,600);
			//seçim ekranı rootu
			root1.getChildren().addAll(button1,button2);


			button1.setOnAction(e -> {
				LineChart lineChart = getlinescene(recordMap,data);
				var root = new HBox();
				Button button4 = new Button("Start");
				Button button6 = new Button("Pause");
				Button button9 = new Button("Restart");
				button4.setOnAction(j -> animateCharts(recordListSize,recordMap,10,lineChart,button6));
				button9.setOnAction(j -> {
					primaryStage.close();
					Platform.runLater( () -> new Main().start( new Stage() ) );
				});
				Scene scene = new Scene(root,800,600);
				root.getChildren().addAll(lineChart,button4,button6,button9);
				primaryStage.setScene(scene);
				animateCharts(recordListSize,recordMap,10,lineChart,button6);

			} );
			button2.setOnAction(e -> {
				BarChart barChart = getBarscene(recordMap,data);
				var root = new HBox();
				Button button5 = new Button("Start");
				Button button6 = new Button("Pause");
				Button button7 = new Button("Restart");
				button5.setOnAction(j -> animateCharts(recordListSize,recordMap,1,barChart,button6));
				button7.setOnAction(j -> {
					primaryStage.close();
					Platform.runLater( () -> new Main().start( new Stage() ) );
				});
				Scene scene = new Scene(root,800,600);
				root.getChildren().addAll(barChart,button5,button6,button7);
				primaryStage.setScene(scene);
				animateCharts(recordListSize,recordMap,1,barChart,button6);


			} );
			primaryStage.setScene(scene1);
			primaryStage.show();
				        

	       				        
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void animateCharts(int recordListSize,Map<String, List<Record>> recordMap,int windowsSize,XYChart xyChart, Button buton){
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		Runnable loop = new Runnable() {
			public void run() {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						if (counter != recordListSize) {
							addData(recordMap,windowsSize);
//							series.getData().sort(Comparator.comparingDouble(d -> d.getYValue().doubleValue()));

						} if (counter == recordListSize ){

							executor.shutdown();

						}
						if(buton != null){
							buton.setOnAction(f -> executor.shutdown() );
						}
					}
				});
			}
		};
		executor.scheduleAtFixedRate(loop, 0, 200, TimeUnit.MILLISECONDS);
	}

	public LineChart<String, Number> getlinescene(Map<String, List<Record>> recordMap, Data data) {

		CategoryAxis xAxis = new CategoryAxis();
		NumberAxis yAxis = new NumberAxis();
		xAxis.setLabel("Year");
		xAxis.setAnimated(false);
		yAxis.setLabel(data.getXLabel());
		yAxis.setAnimated(false);

		LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
		lineChart.setTitle(data.getTitle());
		lineChart.setAnimated(false);

		for(String country: recordMap.keySet()) {
			XYChart.Series<String, Number> series = new XYChart.Series<>();
			lineChart.getData().add(series);
			seriesMap.put(country, series);
		}

		return lineChart;

	}

	public BarChart<String, Number> getBarscene(Map<String, List<Record>> recordMap, Data data){


		CategoryAxis xAxis1 = new CategoryAxis();
		NumberAxis yAxis1 = new NumberAxis();
		xAxis1.setLabel("Year");
		xAxis1.setAnimated(false);
		yAxis1.setLabel(data.getXLabel());
		yAxis1.setAnimated(false);

		BarChart<String, Number> barChart = new BarChart<>(xAxis1, yAxis1);
		barChart.setTitle(data.getTitle());
		barChart.setAnimated(false);

		for(String country: recordMap.keySet()) {
			XYChart.Series<String, Number> series1 = new XYChart.Series<>();
			barChart.getData().add(series1);
			seriesMap.put(country, series1);

		}



		return barChart;
	}

	public static void main(String[] args) {
		launch(args);
	}
	
	public void addData(Map<String, List<Record>> recordMap,int windowSize) {


		for(String country: recordMap.keySet()) {
			String year = recordMap.get(country).get(counter).getYear();
	    	Integer value = Integer.valueOf(recordMap.get(country).get(counter).getValue());
	        seriesMap.get(country).getData().add(new XYChart.Data<>(year, value));
	        seriesMap.get(country).setName(country);


	        if (seriesMap.get(country).getData().size() > windowSize) {
	        	seriesMap.get(country).getData().remove(0);
			}
		}

	    counter++;
	}
	
	public Data saxParser() throws ParserConfigurationException, SAXException, IOException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();
		XMLHandler xmlHandler = new XMLHandler();
		saxParser.parse("C:\\Users\\User\\Desktop\\Grup15pro\\TextFile\\country_populations.xml", xmlHandler);
		Data result = xmlHandler.getData();
		return result;
	}
	
	public Map<String, List<Record>> splitDataForCountries(Data data) {
		Map<String, List<Record>> recordMap = new HashMap<>();
		String country = "";
		int firstIndex = 0;
		for(int i=0; i<data.getRecordList().size(); i++) {
			Record record = data.getRecordList().get(i);
			if (!country.equals(record.getCountry())) {
				
				if(i != 0) {
					List<Record> sublist = new ArrayList<>(data.getRecordList().subList(firstIndex, i));
					firstIndex = i;
					recordMap.put(country, sublist);
				}
				
				country = record.getCountry();
			}
		}
		return recordMap;
	}
	
	public Map<String, List<Record>> splitDataForCities(Data data) {
		Map<String, List<Record>> recordMap = new HashMap<>();
		List<Record> recorList = data.getRecordList();
		List<String> cities = new ArrayList<>();
		
		for (Record record : recorList) {
			if(!cities.contains(record.getName())) {
				cities.add(record.getName());
			}
		}
		
		for (String city : cities) {
			recordMap.put(city, data.getRecordList().stream().filter(element -> element.getName().equals(city)).collect(toList()));
		}
		
		return recordMap;
	}
	
	public Data readText() {
		Data data = new Data();
		data.recordList = new ArrayList<>();
		int counter = 0;
		
		try {
	      File myObj = new File("C:\\Users\\User\\Downloads\\city_populations.txt");
	      Scanner myReader = new Scanner(myObj);
	      while (myReader.hasNextLine()) {
	        String str = myReader.nextLine();
	        if(counter == 0) {
	        	data.setTitle(str);
	        } else if(counter == 1) {
	        	data.setXLabel(str);
	        } else {
	        	if (str.contains(",")) {
	        		String[] strArr = str.split(",");
	        		Record record = createRecord(strArr);
	        		data.recordList.add(record);
	        	}
	        }
	        
	        counter++;
	      }
	      myReader.close();
	    } catch (FileNotFoundException e) {
	      System.out.println("An error occurred.");
	      e.printStackTrace();
	    }
		
		return data;
	}
	
	public Record createRecord(String[] strArr) {
		Record record = new Record();
		record.setCategory(strArr[4]);
		record.setCountry(strArr[2]);
		record.setName(strArr[1]);
		record.setValue(strArr[3]);
		record.setYear(strArr[0]);
		return record;
	}
}
