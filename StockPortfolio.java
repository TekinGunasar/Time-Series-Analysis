import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.JSONException;

public class StockPortfolio {

	public static ArrayList<String> symbols = new ArrayList<String>();
	
	public static void addStock(String symbol) throws IOException, JSONException {
		double[] nextPrices = Stock.getNextPrices(5, symbol);
		double[][] previousPrices = Stock.getLastPricesFor(symbol);
		File symbolFile = new File("bin/symbols/" + symbol + ".txt");
		File previousPricesFile = new File("bin/symbols/" + symbol + "prev.txt");
		symbolFile.createNewFile();
		previousPricesFile.createNewFile();
		FileWriter fw = null;
		BufferedWriter bw = null;
		
		FileWriter fw2 = null;
		BufferedWriter bw2 = null;
        try {
            fw = new FileWriter(symbolFile); 
            bw = new BufferedWriter(fw); 
            
            fw2 = new FileWriter(previousPricesFile);
            bw2 = new BufferedWriter(fw2);

            for (double price: nextPrices) {
                bw.write(Double.toString(price));        
                bw.newLine();          
            }
            
            for(int i = 0;i<previousPrices.length;i++) {
            	bw2.write(Double.toString(previousPrices[i][0]));
            	bw2.newLine();
            }
            
        } catch (IOException e) {
            e.printStackTrace();

        } finally {

            try {
                bw.close();
                fw.close();
                bw2.close();
                fw2.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
		symbols.add(symbol);
	}
	
	public static void removeStock(String symbol) {
		for(int i = 0;i<symbols.size();i++) {
			if(symbols.get(i).equals(symbol)) {
				symbols.remove(symbol);
			}
		}
	}
		
	public static boolean isInPortfolio(String symbol) {
		boolean found = false;
		for(int i = 0;i<symbols.size();i++) {
			if(symbols.get(i).contentEquals(symbol)) {
				found = true;
				break;
			}
		}
		return found;
	}
	
	public static void save() throws IOException {
		
		File symbolsFile = new File("bin/symbols.txt");
        BufferedWriter bw = null;
        FileWriter fw = null;

        try {
            fw = new FileWriter(symbolsFile); 
            bw = new BufferedWriter(fw); 

            for (String symbol: symbols) {
                bw.write(symbol);        
                bw.newLine();          
            }
 
        } catch (IOException e) {
            e.printStackTrace();

        } finally {

            try {
                bw.close();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
