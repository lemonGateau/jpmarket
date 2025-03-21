package market.jpmarket;

public class Holding {
    private String stockCode;
    private Integer quantity;      
    private Double avgPrice;

    public Holding(String stockCode) {
    	this.stockCode    = stockCode;
    	this.quantity     = 0;
    	this.avgPrice     = null;
    }
    
    public Holding(String stockCode, Integer quantity) {
    	this.stockCode    = stockCode;
    	this.quantity     = quantity;
    	this.avgPrice     = null;
    }
    
    public Holding(String stockCode, Integer quantity, Double avgPrice) {
        this.stockCode = stockCode;
        this.quantity = quantity;
        this.avgPrice = avgPrice;
    }

    public Double calcProfitLoss(Double currentPrice) {
        return (double) Math.round((currentPrice - avgPrice) * quantity); // 評価損益を計算
    }

    public void addQuantity(Integer quantity) {
    	this.quantity += quantity;
    }
    
    public void subtractQuantity(Integer quantity) {
    	this.quantity -= quantity;
    }
        
    // setter
    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public void setQuantity(Integer quantity) {
    	this.quantity = quantity;
    }

    public void setAveragePrice(Double avgPrice) {
    	this.avgPrice = avgPrice;
    }
    
    // getter
    public String getStockCode() {
        return stockCode;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Double getAveragePrice() {
        return avgPrice;
    }
    
    public static Double calcAveragePrice(Integer qty, Double avgPrice, Integer addQty, Double orderPrice) {
    	Double newPrice = (qty * avgPrice + addQty * orderPrice) / (qty + addQty);

    	return (double) Math.round(newPrice);
    }
}
