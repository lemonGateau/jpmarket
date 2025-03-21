package market.jpmarket;

import java.time.LocalDate;

public class Order {
	private static Integer orderCount = 100000;
    private String orderId;
    private String stockCode;
    private String side;
    private Integer quantity;
    private Double price;
    private LocalDate orderDate;
    private LocalDate executionDate; 

    public Order(String stockCode, String side, Integer quantity, Double price, LocalDate orderDate) {
        this.orderId = generateOrderId(stockCode, orderDate);
        this.stockCode = stockCode;
        this.side = side;
        this.quantity = quantity;
        this.price = price;
        this.orderDate = orderDate;
        this.executionDate = null;
    }
    
    public Order(String orderId, String stockCode, String side, Integer quantity, Double price, LocalDate orderDate) {
        this.orderId = orderId;
        this.stockCode = stockCode;
        this.side = side;
        this.quantity = quantity;
        this.price = price;
        this.orderDate = orderDate;
    }

    private String generateOrderId(String stockCode, LocalDate orderDate) {
    	String orderId = orderDate.toString() + stockCode + orderCount.toString();
    	orderCount++;
    	
    	return orderId;
    	
    }
    
    // setter
    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public void setQuantity(Integer quantity) {
    	this.quantity = quantity;
    }

    public void setPrice(Double price) {
    	this.price = price;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public void setExecutionDate(LocalDate executionDate) {
        this.executionDate = executionDate;
    }
    
    // getter
    public String getOrderId() {
        return orderId;
    }

    public String getStockCode() {
        return stockCode;
    }

    public String getSide() {
        return side;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Double getPrice() {
        return price;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public LocalDate getExecutionDate() {
        return executionDate;
    }
}
