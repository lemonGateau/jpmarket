package market.jpmarket;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.category.DefaultCategoryDataset;

public class PageController {
	private Map<String, OhlcvRecord> ohlcvMap = new LinkedHashMap<>();
	private Map<String, Fundamentals> fundamentalsMap = new LinkedHashMap<>();
	private List<Order> orders = new ArrayList<>();
	private Map<String, Holding> holdingStocks = new LinkedHashMap<>();
	
	public PageController() {		
		holdingStocks.put("Yen", new Holding("Yen", Config.assetYen));

    	for (String stockCode: Config.stockCodes) {
    		ohlcvMap.put(stockCode, new OhlcvRecord(stockCode));
    		fundamentalsMap.put(stockCode, DataIO.readFundamentalsFile(stockCode));
    		// holdingStocks.put(stockCode, new Holding(stockCode));
    	}
	}
	
	// 株価更新
	public LocalDate update(LocalDate currentDate) {
		// todo 日付更新
		for (String stockCode: ohlcvMap.keySet()) {
			currentDate = ohlcvMap.get(stockCode).next(currentDate);
			break;
		}
		
		Iterator<Order> iterator = orders.iterator();
		while (iterator.hasNext()) {
			Order order = iterator.next();
			
			if (confirmExecution(order, currentDate)) {
				iterator.remove();

				String stockCode = order.getStockCode();
				Integer quantity = order.getQuantity();
				Double orderPrice = order.getPrice();
				String side = order.getSide();
				
				Holding holdingStock = holdingStocks.get(stockCode);
				
				System.out.println(holdingStock);
				if (side == "Bid") {
					if (holdingStock == null) {
						holdingStocks.put(stockCode, new Holding(stockCode, quantity, orderPrice));
					} else {
						// この実行順
						holdingStock.updateAveragePrice(quantity, orderPrice);
						holdingStock.addQuantity(quantity);
					}
				}
			}
		}
		return currentDate;
	}
	
	public boolean confirmExecution(Order order, LocalDate date) {
		String stockCode = order.getStockCode();
		Double orderPrice = order.getPrice();
		
		if (order.getSide() == "Bid") {
			Double low = ohlcvMap.get(stockCode).getLow(date);
			
			if (orderPrice >= low) {
				return true;
			}
		} else {
			Double high = ohlcvMap.get(stockCode).getHigh(date);
			
			if (orderPrice <= high) {
				return true;
			}
		}
		return false;
	}
	
	public JTable createStockTable(LocalDate date) {
        final String[] COLUMNS = {"銘柄コード", "銘柄名", "現在値", "高値", "安値", "前日比"};

        DefaultTableModel stockTableModel = new DefaultTableModel(COLUMNS, 0);
        
        for (String stockCode: Config.stockCodes) {
        	String companyName = fundamentalsMap.get(stockCode).getName();
			String close = ohlcvMap.get(stockCode).getClose(date).toString();
			String high  = ohlcvMap.get(stockCode).getHigh(date).toString();
			String low   = ohlcvMap.get(stockCode).getLow(date).toString();
			
			String prevDayRatio = ohlcvMap.get(stockCode).calcPreviousDayRatio(date).toString();
			
			String[] row = {stockCode, companyName, close, high, low, prevDayRatio+"%"};
			stockTableModel.addRow(row);
		}
		
		return new JTable(stockTableModel);
	}
	
	public JTable createHoldingStockTable(LocalDate date) {
        final String[] COLUMNS = {"銘柄コード", "銘柄名", "現在値", "取得単価", "数量", "評価損益"};
        DefaultTableModel holdingStockTableModel = new DefaultTableModel(COLUMNS, 0);
        
        for (Holding holdingStock: holdingStocks.values()) {
        	String stockCode = holdingStock.getStockCode();
        	
        	if (stockCode == "Yen") {
        		continue;
        	}
        	
        	Integer quantity = holdingStock.getQuantity();
        	if (quantity > 0) {
            	String companyName = fundamentalsMap.get(stockCode).getName();
            	String currentPrice = ohlcvMap.get(stockCode).getClose(date).toString();
            	String avgPrice = holdingStock.getAveragePrice().toString();
            	String qty = quantity.toString();
            	String profitLoss = holdingStock.calcProfitLoss(Double.parseDouble(currentPrice)).toString();
            	
            	String[] values = {stockCode, companyName, currentPrice, avgPrice, qty, profitLoss};
            	
            	holdingStockTableModel.addRow(values);
        	}
        }
        return new JTable(holdingStockTableModel);
	}
	
	public JTable createExistOrderTable(LocalDate date) {
        final String[] COLUMNS = {"注文ID", "銘柄コード", "銘柄名", "サイド", "現在値", "注文単価", "数量", "注文日時"};
        DefaultTableModel existOrderTableModel = new DefaultTableModel(COLUMNS, 0);
        
        for (Order order: orders) {
        	String orderId      = order.getOrderId();
        	String stockCode    = order.getStockCode();
        	String companyName  = fundamentalsMap.get(stockCode).getName();
        	String side         = order.getSide();
        	String currentPrice = ohlcvMap.get(stockCode).getClose(date).toString();
        	String orderPrice   = order.getPrice().toString();
        	String quantity     = order.getQuantity().toString();
        	String orderDate    = order.getOrderDate().toString();
        	
        	String[] values = {orderId, stockCode, companyName, side, currentPrice, orderPrice, quantity, orderDate};
        	
        	existOrderTableModel.addRow(values);
        }
        
        return new JTable(existOrderTableModel);
	}
	
	public JTable createTechnicalsTable(String stockCode, LocalDate date) {
        final String[] COLUMNS = {"銘柄コード", "現在値", "始値", "高値", "安値"};
        DefaultTableModel technicalsTableModel = new DefaultTableModel(COLUMNS, 0);
        
		String close = ohlcvMap.get(stockCode).getClose(date).toString();
		String open  = ohlcvMap.get(stockCode).getOpen(date).toString();
		String high  = ohlcvMap.get(stockCode).getHigh(date).toString();
		String low   = ohlcvMap.get(stockCode).getLow(date).toString();
						
		String[] values = {stockCode, close, open, high, low};
        technicalsTableModel.addRow(values);
        
		return new JTable(technicalsTableModel);
	}
	
	public JTable createFundamentalsTable(String stockCode) {
        final String[] COLUMNS = {"PER", "PBR", "配当利回り", "配当", "ROE"};
        DefaultTableModel fundamentalsTableModel = new DefaultTableModel(COLUMNS, 0);
        
        String per           = fundamentalsMap.get(stockCode).getPER().toString();
        String pbr           = fundamentalsMap.get(stockCode).getPBR().toString();
        String dividendYield = fundamentalsMap.get(stockCode).getDividendYeild().toString();
        String dividendRate  = fundamentalsMap.get(stockCode).getDividendRate().toString();
        String roe           = fundamentalsMap.get(stockCode).getROE().toString();

        String[] values = {per, pbr, dividendYield, dividendRate, roe};
        fundamentalsTableModel.addRow(values);
        
        return new JTable(fundamentalsTableModel);
	}
	
    public JFreeChart createCloseChart(String stockCode, LocalDate currentDate) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        Double min = Double.MAX_VALUE;
        Double max = Double.MIN_VALUE;
        
        LocalDate date = currentDate.minusDays(30);
        while (date.isBefore(currentDate)) {
            Double close = ohlcvMap.get(stockCode).getClose(date);

            dataset.addValue(close, "Close", date.format(DateTimeFormatter.ofPattern("M/d")));
            
            min = Math.min(min, close);
            max = Math.max(max, close);
            
            date = ohlcvMap.get(stockCode).next(date);
        }

        // 折れ線チャートを作成
        JFreeChart chart = ChartFactory.createLineChart(
                stockCode, // チャートタイトル
                "Date",    // X軸ラベル
                "Close",   // Y軸ラベル
                dataset    // データセット
        );
        
        CategoryPlot plot = chart.getCategoryPlot();
        NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();

        // Set the range with some padding
        yAxis.setLowerBound(min*0.9); // 10% below min
        yAxis.setUpperBound(max*1.1); // 10% above max
        
        return chart;
    }

    public void bidOrder(String stockCode, Integer quantity, Double price, LocalDate date) {
    	if (stockCode == null || quantity == null || price == null) {
    		return;
    	}
    	
    	if (quantity <= 0 || price <= 0) {
    		return;
    	}
    	
    	Integer assetYen = holdingStocks.get("Yen").getQuantity();
    	Integer purchasePrice = (int) Math.round(quantity*price);
    	
    	System.out.println(assetYen);
    	System.out.println(purchasePrice);
    	
    	if (assetYen >= purchasePrice) {
    		holdingStocks.get("Yen").subtractQuantity(purchasePrice);
        	orders.add(new Order(stockCode, "Bid", quantity, price, date));
    	}
    }

    public void askOrder(String stockCode, Integer quantity, Double price, LocalDate date) {
    	if (stockCode == null || quantity == null || price == null) {
    		return;
    	}
    	
    	if (quantity <= 0 || price <= 0) {
    		return;
    	}
    	 
    	Integer holdingQuantity = holdingStocks.get(stockCode).getQuantity();
    	if (holdingQuantity >= quantity) {
    		holdingStocks.get(stockCode).subtractQuantity(quantity);
        	orders.add(new Order(stockCode, "Ask", quantity, price, date));
    	}
    }
    
    public void cancelOrder(String orderId) {
    	for (Order order: orders) {
    		if (order.getOrderId() == orderId) {
    			orders.remove(order);
    			
    			String stockCode = order.getStockCode();
    			Integer quantity = order.getQuantity();
    			
    			if (order.getSide() == "Bid") {
    				Integer purchasePrice = (int) Math.round(quantity*order.getPrice());
    				holdingStocks.get("Yen").addQuantity(purchasePrice);
    			} else {
    				holdingStocks.get(stockCode).addQuantity(quantity);
    			}
    			
    		}
    	}
    }
}
