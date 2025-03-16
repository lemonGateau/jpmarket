package market.jpmarket;

public class Ohlcv {
	private String stockCode;

	private Double open;
	private Double high;
	private Double low;
	private Double close;
	private Double volume;

	public Ohlcv(String stockCode) {
		this.stockCode   = stockCode;
		this.open        = null;
		this.high        = null;
		this.low         = null;
		this.close       = null;
		this.volume      = null;
	}
	
	public Ohlcv(String stockCode, Double open, Double high, Double low, Double close, Double volume) {
		this.stockCode = stockCode;
		this.open      = open;
		this.high      = high;
		this.low       = low;
		this.close     = close;
		this.volume    = volume;
	}

	// setter
	public void setOpen(Double open) {
		this.open = open;
	}

	public void setHigh(Double high) {
		this.high = high;
	}
	
	public void setLow(Double low) {
		this.low = low;
	}
	
	public void setClose(Double close) {
		this.close = close;
	}
	
	public void setVolume(Double volume) {
		this.volume = volume;
	}

	//getter
	public Double getOpen() {
		return open;
	}
	
	public Double getHigh() {
		return high;
	}
	
	public Double getLow() {
		return low;
	}
	
	public Double getClose() {
		return close;
	}
	
	public Double getVolume() {
		return volume;
	}
}