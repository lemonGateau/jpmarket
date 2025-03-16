package market.jpmarket;

public class Fundamentals {
	private String stockCode;
	private String name;

	// Fundamentals
	private Double per;
	private Double pbr;
	private Double dividendYield;
	private Double dividendRate;
	private Double roe;
	
	public Fundamentals(String stockCode) {
		this.stockCode = stockCode;
		this.name = null;
		this.per  = null;
		this.pbr  = null;
		this.dividendYield = null;
		this.dividendRate  = null;
		this.roe  = null;
	}

	// setter
	public void setName(String name) {
		this.name = name;
	}
	
	public void setPER(Double per) {
		this.per = per;
	}
	
	public void setPBR(Double pbr) {
		this.pbr = pbr;
	}
	
	public void setDividendYield(Double dividendYield) {
		this.dividendYield = dividendYield;
	}
	
	public void setDividendRate(Double dividendRate) {
		this.dividendRate = dividendRate;
	}
	
	public void setROE(Double roe) {
		this.roe = roe;
	}
	
	// getter
	public String getName() {
		return name;
	}
	
	public Double getPER() {
		return per;
	}
	
	public Double getPBR() {
		return pbr;
	}
	
	public Double getDividendYeild() {
		return dividendYield;
	}
	
	public Double getDividendRate() {
		return dividendRate;
	}
	
	public Double getROE() {
		return roe;
	}
}
