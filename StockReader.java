import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class StockReader {
	
		public static final String[] infoList = {"low","high","open","volume"};
	
		public static String getURLString(String symbol) {
			return "https://sandbox.iexapis.com/stable/stock/" + symbol + "/batch?types=quote,news,chart&range=1m&last=10&token=Tsk_db4493dc79614614a66ce7b5b62201d2";
		}
		
		public static URL getAPIUrl(String stockName) throws MalformedURLException {
			return new URL(getURLString(stockName));
		}
		
		public static String getJsonString(String stockName) throws IOException {
			String jsonString = "";
			URL apiURL = getAPIUrl(stockName);
			URLConnection conn = apiURL.openConnection();
			Scanner apiCallScanner = new Scanner(apiURL.openStream());
			while(apiCallScanner.hasNext()) {
				jsonString += apiCallScanner.nextLine();
			}
			return jsonString;
		}
		
		public static JSONArray getJSONArray(String stockName) throws IOException, JSONException {
			String jsonString = getJsonString(stockName);
			JSONObject jsonObject = new JSONObject(jsonString);
			JSONArray chart = jsonObject.getJSONArray("chart");
			return chart;
		}
							
	public static double[][] getPrices(String stockName) throws IOException, JSONException {
		String jsonString = getJsonString(stockName);
			JSONArray chart = getJSONArray(stockName);
			double[][] prices = new double[chart.length()][1];
			for(int i = 0;i<chart.length();i++) {
				JSONObject currentObject = chart.getJSONObject(i);
				prices[i][0] = currentObject.getDouble("close");
			}
			return prices;
		}
	
	public static double[][] getInfoAbout(String stockName, String info) throws IOException, JSONException {
		String jsonString = getJsonString(stockName);
		JSONArray chart = getJSONArray(stockName);
		double[][] infoList = new double[chart.length()][1];
		for(int i = 0;i<chart.length();i++) {
			JSONObject currentObject = chart.getJSONObject(i);
				infoList[i][0] = currentObject.getDouble(info);
			}
			return infoList;
		}
	
	public static ArrayList<double[][]> getAllInfo(String stockName) throws IOException, JSONException {
		ArrayList<double[][]> allInfo = new ArrayList<>();
		String jsonString = getJsonString(stockName);
		JSONArray chart = getJSONArray(stockName);
		for(int i = 0;i<infoList.length;i++) {
			double[][] currentList = new double[chart.length()][1];
			String currentInfoNeeded = infoList[i];
			for(int j = 0;j<chart.length();j++) {
				JSONObject currentObject = chart.getJSONObject(j);
				double currentInfo = currentObject.getDouble(currentInfoNeeded);
				currentList[j][0] = currentInfo;
			}
			allInfo.add(currentList);
			currentList = new double[chart.length()][1];
		}
		return allInfo;
	}
			
	public static double getMaxPrice(String stockName) throws IOException, JSONException {
			double[][] prices = getPrices(stockName);
		double max = prices[0][0];
			for(int i = 0;i<prices.length;i++) {
				if(prices[i][0] > max) {
					max = prices[i][0];
				}
			}
			return max;
		}
		
	public static String getCompanyName(String symbol) throws IOException, JSONException {
		String jsonString = getJsonString(symbol);
		JSONObject jsonObject = new JSONObject(jsonString);
		return jsonObject.getJSONObject("quote").getString("companyName");
	}
	
	public static boolean isValidSymbol(String symbol) {
		boolean symbolExists = true;
		try {
			getJsonString(symbol);
		} catch (IOException e1) {
			symbolExists = false;
		}
		return symbolExists;
	}
}
