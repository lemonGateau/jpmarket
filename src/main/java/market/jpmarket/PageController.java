package market.jpmarket;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

public class PageController {
	private DBDriver db = new DBDriver();
	private ArrayList<LocalDate> periods;
	private Holding assetYen = new Holding("Yen", Config.assetYen);
	
	public PageController() {
    	for (String stockCode: Config.stockCodes) {    		
    		OhlcvRecord ohlcvRecord = new OhlcvRecord(stockCode);
    		periods = ohlcvRecord.getDates();
    		
    		/*
    		db.addFundamentals(DataIO.readFundamentalsFile(stockCode));

    		for (LocalDate date: periods) {
    			db.addOhlcv(date, ohlcvRecord);
    		}
    		*/
    	}
   	}
	
	// 株価更新
	public LocalDate update(LocalDate currentDate) {
		// todo 日付更新
		LocalDate nextDate = next(currentDate);
		
		for (Order order: db.getAllExistOrders()) {
			
			if (confirmExecution(order, nextDate)) {
				db.deleteExistOrder(order.getOrderId());

				String stockCode = order.getStockCode();
				Integer quantity = order.getQuantity();
				Double orderPrice = order.getPrice();
				String side = order.getSide();
				
				Holding holdingStock = db.getHoldingStock(stockCode);
								
				if (side.equals("Bid")) {
					if (holdingStock == null) {
						db.addHoldingStock(new Holding(stockCode, quantity, orderPrice));
					} else {
						Holding stock = db.getHoldingStock(stockCode);
						Integer oldQuantity = stock.getQuantity();
						Double avgPrice = Holding.calcAveragePrice(oldQuantity, stock.getAveragePrice(), quantity, orderPrice);
						
						db.updateAveragePrice(stockCode, avgPrice);
						db.updateHoldQuantity(stockCode, oldQuantity+quantity);
					}
				}
			}
		}
		return nextDate;
	}
	
	public boolean confirmExecution(Order order, LocalDate date) {
		String stockCode = order.getStockCode();
		Double orderPrice = order.getPrice();
		
		Ohlcv ohlcv = db.getOhlcv(date, stockCode);
		
		if (order.getSide() == "Bid") {
			Double low = ohlcv.getLow();
			
			if (orderPrice >= low) {
				return true;
			}
		} else {
			Double high = ohlcv.getHigh();
			
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
        	Ohlcv ohlcv     = db.getOhlcv(date, stockCode);
        	Ohlcv ohlcvPrev = db.getOhlcv(previous(date), stockCode);
        	        	
        	String companyName = ohlcv.getStockCode();
			String close       = ohlcv.getClose().toString();
			String high        = ohlcv.getHigh().toString();
			String low         = ohlcv.getLow().toString();
			
			String prevDayRatio = OhlcvRecord.calcPreviousDayRatio(ohlcv.getClose(), ohlcvPrev.getClose()).toString();
			
			String[] row = {stockCode, companyName, close, high, low, prevDayRatio+"%"};
			stockTableModel.addRow(row);
		}
		
		return new JTable(stockTableModel);
	}
	
	public JTable createHoldingStockTable(LocalDate date) {
        final String[] COLUMNS = {"銘柄コード", "銘柄名", "現在値", "取得単価", "数量", "評価損益"};
        DefaultTableModel holdingStockTableModel = new DefaultTableModel(COLUMNS, 0);
        
        for (Holding holdingStock: db.getAllHoldingStocks()) {
        	String stockCode = holdingStock.getStockCode();
        	Integer quantity = holdingStock.getQuantity();
        	
        	if (quantity > 0) {
            	String companyName  = db.getFundamentals(stockCode).getName();
            	String currentPrice = db.getOhlcv(date, stockCode).getClose().toString();
            	String avgPrice     = holdingStock.getAveragePrice().toString();
            	String qty          = quantity.toString();
            	String profitLoss   = holdingStock.calcProfitLoss(Double.parseDouble(currentPrice)).toString();
            	
            	String[] values = {stockCode, companyName, currentPrice, avgPrice, qty, profitLoss};
            	
            	holdingStockTableModel.addRow(values);
        	}
        }
        return new JTable(holdingStockTableModel);
	}
	
	public JTable createExistOrderTable(LocalDate date) {
        final String[] COLUMNS = {"注文ID", "銘柄コード", "銘柄名", "サイド", "現在値", "注文単価", "数量", "注文日時"};
        DefaultTableModel existOrderTableModel = new DefaultTableModel(COLUMNS, 0);
        
        for (Order order: db.getAllExistOrders()) {
        	String orderId      = order.getOrderId();
        	String stockCode    = order.getStockCode();
        	String companyName  = db.getFundamentals(stockCode).getName();
        	String side         = order.getSide();
        	String currentPrice = db.getOhlcv(date, stockCode).getClose().toString();
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
        
        Ohlcv ohlcv = db.getOhlcv(date, stockCode);
        
		String close = ohlcv.getClose().toString();
		String open  = ohlcv.getOpen().toString();
		String high  = ohlcv.getHigh().toString();
		String low   = ohlcv.getLow().toString();
						
		String[] values = {stockCode, close, open, high, low};
        technicalsTableModel.addRow(values);
        
		return new JTable(technicalsTableModel);
	}
	
	public JTable createFundamentalsTable(String stockCode) {
        final String[] COLUMNS = {"PER", "PBR", "配当利回り", "配当", "ROE"};
        DefaultTableModel fundamentalsTableModel = new DefaultTableModel(COLUMNS, 0);
        
        System.out.println(stockCode);
        Fundamentals fundamentals = db.getFundamentals(stockCode);
        
        String per           = fundamentals.getPER().toString();
        String pbr           = fundamentals.getPBR().toString();
        String dividendYield = fundamentals.getDividendYeild().toString();
        String dividendRate  = fundamentals.getDividendRate().toString();
        String roe           = fundamentals.getROE().toString();

        String[] values = {per, pbr, dividendYield, dividendRate, roe};
        fundamentalsTableModel.addRow(values);
        
        return new JTable(fundamentalsTableModel);
	}
	
    public JFreeChart createCloseChart(String stockCode, LocalDate currentDate) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        Double min = Double.MAX_VALUE;
        Double max = Double.MIN_VALUE;
        
        LocalDate date = skipHolidays(currentDate.minusDays(30));
        for (; date.isBefore(currentDate); date=next(date)) {
            Double close = db.getOhlcv(date, stockCode).getClose();

            dataset.addValue(close, "Close", date.format(DateTimeFormatter.ofPattern("M/d")));
            
            min = Math.min(min, close);
            max = Math.max(max, close);
        }

        // 折れ線チャートを作成
        JFreeChart chart = ChartFactory.createLineChart(
                stockCode, // チャートタイトル
                "Date",    // X軸ラベル
                "Close",   // Y軸ラベル
                dataset    // データセット
        );
        
        /*
        CategoryPlot plot = chart.getCategoryPlot();
        NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();

        // Set the range with some padding
        yAxis.setLowerBound(min*0.9); // 10% below min
        yAxis.setUpperBound(max*1.1); // 10% above max
        */
        
        return chart;
    }

    public void bidOrder(String stockCode, Integer quantity, Double price, LocalDate date) {
    	if (stockCode == null || quantity == null || price == null) {
    		return;
    	}
    	
    	if (quantity <= 0 || price <= 0) {
    		return;
    	}
    	
    	Integer yen = assetYen.getQuantity();
    	Integer purchasePrice = (int) Math.round(quantity*price);
    	
    	if (yen >= purchasePrice) {
    		assetYen.subtractQuantity(purchasePrice);
    		db.addExistOrder(new Order(stockCode, "Bid", quantity, price, date));
    	}
    }

    public void askOrder(String stockCode, Integer quantity, Double price, LocalDate date) {
    	if (stockCode == null || quantity == null || price == null) {
    		return;
    	}
    	
    	if (quantity <= 0 || price <= 0) {
    		return;
    	}
    	 
    	Integer holdingQuantity = db.getHoldingStock(stockCode).getQuantity();
    	if (holdingQuantity >= quantity) {
    		db.updateHoldQuantity(stockCode, holdingQuantity-quantity);
        	db.addExistOrder(new Order(stockCode, "Ask", quantity, price, date));
    	}
    }
    
    public void cancelOrder(String orderId) {
    	Order order = db.getExistOrder(orderId);
    			
    	String stockCode = order.getStockCode();
   		Integer quantity = order.getQuantity();
    			
    	if (order.getSide() == "Bid") {
    		Integer purchasePrice = (int) Math.round(quantity*order.getPrice());
    		assetYen.addQuantity(purchasePrice);
    	} else {
    		Holding stock = db.getHoldingStock(stockCode);
    		Integer holdingQuantity = stock.getQuantity();
    		
    		db.updateHoldQuantity(stockCode, holdingQuantity-quantity);
    	}
    	db.deleteExistOrder(orderId);
    }
    
    public LocalDate next(LocalDate currentDate) {
    	return periods.get(periods.indexOf(currentDate) + 1);
    }
    
    public LocalDate previous(LocalDate currentDate) {
    	Integer i = periods.indexOf(currentDate);
    	
    	if (i == 0) {
    		return currentDate;
    	}
    	
    	return periods.get(i - 1);
    }
    
    public LocalDate skipHolidays(LocalDate currentDate) {
    	if (periods.contains(currentDate)) {
    		return currentDate;
    	}
    	return next(currentDate);
    }
}
