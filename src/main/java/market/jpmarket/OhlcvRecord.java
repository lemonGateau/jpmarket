package market.jpmarket;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class OhlcvRecord {
	private String stockCode;
	private LinkedHashMap<LocalDate, Ohlcv> ohlcvMap;
	
	public OhlcvRecord(String stockCode) {
		this.stockCode = stockCode;

		ohlcvMap = DataIO.readOhlcvFile(stockCode);
	}
	
	public static Double calcPreviousDayRatio(Double close, Double prevClose) {		
		if (close == 0.) {
			return -100.;
		}
				
		return Math.round(((close - prevClose) / close) * 100 * 10) / 10.;
	}
	
	// getter
	public ArrayList<LocalDate> getDates() {
		return new ArrayList<LocalDate>(ohlcvMap.keySet());
	}
	
	public String getStockCode() {
		return stockCode;
	}
	
	public Double getOpen(LocalDate date) {
		date = skipHolidays(date);
		
		if (date == null) {
			return null;
		}
		
		return ohlcvMap.get(date).getOpen();
	}
	
	public Double getHigh(LocalDate date) {
		date = skipHolidays(date);
		
		if (date == null) {
			return null;
		}
		
		return ohlcvMap.get(date).getOpen();
	}
	
	public Double getLow(LocalDate date) {
		date = skipHolidays(date);
		
		if (date == null) {
			return null;
		}
		
		return ohlcvMap.get(date).getLow();
	}
	
	public Double getClose(LocalDate date) {
		date = skipHolidays(date);
		
		if (date == null) {
			return null;
		}
		
		return ohlcvMap.get(date).getClose();
	}
	
	public Double getVolume(LocalDate date) {
		date = skipHolidays(date);
		
		if (date == null) {
			return null;
		}
		
		return ohlcvMap.get(date).getVolume();
	}
		
	private LocalDate skipHolidays(LocalDate date) {
		if (ohlcvMap.containsKey(date)) {
			return date;
		}
		
		return next(date);
	}
	
	private LocalDate goBackHolidays(LocalDate date) {
		if (ohlcvMap.containsKey(date)) {
			return date;
		}
		return previous(date);
	}
	
	public LocalDate next(LocalDate date) {
		while (isWithinPeriod(date)) {
			date = date.plusDays(1);

			if (ohlcvMap.containsKey(date)) {
				return date;
			}
		}
		
		return null;
	}
	
	public LocalDate previous(LocalDate date) {
		while (isWithinPeriod(date)) {
			date = date.minusDays(1);

			if (ohlcvMap.containsKey(date)) {
				return date;
			}
		}
		
		return null;
	}
	
	private boolean isWithinPeriod(LocalDate date) {
		return !(Config.firstDate.isAfter(date) || Config.lastDate.isBefore(date));
	}
}
