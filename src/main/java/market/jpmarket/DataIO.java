package market.jpmarket;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class DataIO {
	private static final String FOLDERPATH = ".\\csv\\";

	public static LinkedHashMap<LocalDate, Ohlcv> readOhlcvFile(String stockCode) {
        LinkedHashMap<LocalDate, Ohlcv> ohlcvMap = new LinkedHashMap<>();
        String filePath = toFilePath(stockCode, "ohlcv.csv");

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
        	String[] keys = br.readLine().split(",");
        	String line;

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
            	Map<String, Double> map = new HashMap<>();

            	for (int i=1; i<keys.length; i++) {
            		map.put(keys[i], Double.parseDouble(values[i]));
            	}
                
                LocalDate localDate = LocalDate.parse(values[0], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                
                Ohlcv ohlcv = new Ohlcv(stockCode);
                ohlcv.setOpen(map.get("Open"));
                ohlcv.setHigh(map.get("High"));
                ohlcv.setLow(map.get("Low"));
                ohlcv.setClose(map.get("Close"));
                ohlcv.setVolume(map.get("Volume"));

                ohlcvMap.put(localDate, ohlcv);
                }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ohlcvMap;
	}
	
	public static Fundamentals readFundamentalsFile(String stockCode) {
		Fundamentals fundamentals = new Fundamentals(stockCode);
        String filePath = toFilePath(stockCode, "fundamentals.csv");

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {        	
        	String[] keys   = br.readLine().split(",");
            String[] values = br.readLine().split(",");
            
        	Map<String, Double> map = new HashMap<>();

            for(int i=1; i<keys.length; i++) {
            	map.put(keys[i], Double.parseDouble(values[i]));
            }
            
            fundamentals.setName(values[0]);
            fundamentals.setPER(map.get("trailingPE"));
            fundamentals.setPBR(map.get("priceToBook"));
            fundamentals.setDividendYield(map.get("dividendYield"));
            fundamentals.setDividendRate(map.get("dividendRate"));
            fundamentals.setROE(map.get("returnOnEquity"));
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fundamentals;
	}
	
	private static String toFilePath(String stockCode, String fileType) {
        return FOLDERPATH + stockCode + "_" + fileType;	
	}
}
