package market.jpmarket;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DBDriver {
	private final String pathDB = "jdbc:sqlite:C:\\Users\\manab\\sqlite-tools-win-x64-3490100\\jpmarket.db";
	
    private Connection connect() throws SQLException {
        return DriverManager.getConnection(pathDB);
    }
	
	// ADD method for fundamentals
	public void addFundamentals(Fundamentals fundamentals) {
	    String sql = "INSERT INTO fundamentals (stockCode, companyName, per, pbr, dividendYield, dividendRate, roe) "
	    		+ "VALUES (?, ?, ?, ?, ?, ?, ?)";
	    
	    try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setString(1, fundamentals.getStockCode());
	        pstmt.setString(2, fundamentals.getName());
	        pstmt.setDouble(3, fundamentals.getPER());
	        pstmt.setDouble(4, fundamentals.getPBR());
	        pstmt.setDouble(5, fundamentals.getDividendYeild());
	        pstmt.setDouble(6, fundamentals.getDividendRate());
	        pstmt.setDouble(7, fundamentals.getROE());
	        
	        pstmt.executeUpdate();
	        System.out.println("New row added to fundamentals.");
	    } catch (SQLException e) {
	        System.out.println("Error in addFundamentals: " + e.getMessage());
	    }
	}
	
	// ADD method for ohlcv
	public void addOhlcv(LocalDate date, OhlcvRecord ohlcv) {
	    String sql = "INSERT INTO ohlcv (date, stockCode, open, high, low, close, volume) "
	    		+ "VALUES (?, ?, ?, ?, ?, ?, ?)";
	    try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setString(1, date.toString());
	        pstmt.setString(2, ohlcv.getStockCode());
	        pstmt.setDouble(3, ohlcv.getOpen(date));
	        pstmt.setDouble(4, ohlcv.getHigh(date));
	        pstmt.setDouble(5, ohlcv.getLow(date));
	        pstmt.setDouble(6, ohlcv.getClose(date));
	        pstmt.setDouble(7, ohlcv.getVolume(date));
	        pstmt.executeUpdate();
	        
	        System.out.println("New row added to ohlcv.");
	    } catch (SQLException e) {
	        System.out.println("Error in addOhlcv: " + e.getMessage());
	    }
	}
	
	public void addExistOrder(Order order) {
	    String sql = "INSERT INTO existOrder (orderId, stockCode, side, quantity, orderPrice, orderDate, executionDate) "
	    		+ "VALUES (?, ?, ?, ?, ?, ?, ?)";

	    try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        // Set the values for the placeholders in the SQL query
	        pstmt.setString(1, order.getOrderId());
	        pstmt.setString(2, order.getStockCode());
	        pstmt.setString(3, order.getSide());
	        pstmt.setInt   (4, order.getQuantity());
	        pstmt.setDouble(5, order.getPrice());
	        pstmt.setString(6, order.getOrderDate().toString());

	        pstmt.executeUpdate();
	        
	        System.out.println("New row added to existOrder.");
	    } catch (SQLException e) {
	        System.out.println("Error inserting data into existOrder: " + e.getMessage());
	    }
	}
	
	// ADD method for holdingStock
	public void addHoldingStock(Holding holdingStock) {
	    String sql = "INSERT INTO holdingStock (stockCode, quantity, avgPrice) VALUES (?, ?, ?)";
	    try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setString(1, holdingStock.getStockCode());
	        pstmt.setInt   (2, holdingStock.getQuantity());
	        pstmt.setDouble(3, holdingStock.getAveragePrice());
	        pstmt.executeUpdate();
	        
	        System.out.println("New row added to holdingStock.");
	    } catch (SQLException e) {
	        System.out.println("Error in addHoldingStock: " + e.getMessage());
	    }
	}
	
	public void updateAveragePrice(String stockCode, Double avgPrice) {
		String sql = "UPDATE holdingStock set avgPrice = ? WHERE stockCode = ?";
		
	    try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setDouble(1, avgPrice);
	        pstmt.setString(2, stockCode);
	        pstmt.executeUpdate();
	        
	        System.out.println("Updated holdingStock.");
	    } catch (SQLException e) {
	        System.out.println("Error in updateAveragePrice: " + e.getMessage());
	    }
	}
	
	public void updateHoldQuantity(String stockCode, Integer quantity) {
		String sql = "UPDATE holdingStock set quantity = ? WHERE stockCode = ?";
		
	    try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setDouble(1, quantity);
	        pstmt.setString(2, stockCode);
	        pstmt.executeUpdate();
	        
	        System.out.println("Updated holdingStock.");
	    } catch (SQLException e) {
	        System.out.println("Error in updateAveragePrice: " + e.getMessage());
	    }
	}
	
	public void deleteExistOrder(String orderId) {
	    String sql = "DELETE FROM existOrder WHERE orderId = ?";
	    
	    try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setString(1, orderId);
	        pstmt.executeUpdate();
	        
	        System.out.println("Row deleted from existOrder.");
	    } catch (SQLException e) {
	        System.out.println("Error in deleteExistOrder: " + e.getMessage());
	    }
	}

	// DELETE method for holdingStock
	public void deleteHoldingStock(String stockCode) {
	    String sql = "DELETE FROM holdingStock WHERE stockCode = ?";
	    try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setString(1, stockCode);
	        pstmt.executeUpdate();
	        
	        System.out.println("Row deleted from holdingStock.");
	    } catch (SQLException e) {
	        System.out.println("Error in deleteHoldingStock: " + e.getMessage());
	    }
	}
	
	// GET method for ohlcv
	public Fundamentals getFundamentals(String stockCode) {
	    String sql = "SELECT * FROM fundamentals WHERE stockCode = ?";
	    try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setString(1, stockCode);
	        ResultSet rs = pstmt.executeQuery();

	        if (rs.next()) {
	        	Fundamentals fundamentals = new Fundamentals(rs.getString("stockCode"));
	        	fundamentals.setName(rs.getString("companyName"));
	        	fundamentals.setPER(rs.getDouble("per"));
	            fundamentals.setPBR(rs.getDouble("pbr"));
	            fundamentals.setDividendYield(rs.getDouble("dividendYield"));
	            fundamentals.setDividendRate(rs.getDouble("dividendRate"));
	            fundamentals.setROE(rs.getDouble("roe"));
	            
	            return fundamentals;
	        }
	    } catch (SQLException e) {
	        System.out.println("Error in getFundamentals: " + e.getMessage());
	    }
		return null;
	}
	
	// GET method for fundamentals
	public Ohlcv getOhlcv(LocalDate date, String stockCode) {
	    String sql = "SELECT * FROM ohlcv WHERE date = ? AND stockCode = ?";
	    try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setString(1, date.toString());
	        pstmt.setString(2, stockCode);
	        ResultSet rs = pstmt.executeQuery();
	        	        
	        if (rs.next()) {
	            return new Ohlcv(
	                rs.getString("stockCode"),
	                rs.getDouble("open"),
	                rs.getDouble("high"),
	                rs.getDouble("low"),
	                rs.getDouble("close"),
	                rs.getDouble("volume")
	            );
	        }
	    } catch (SQLException e) {
	        System.out.println("Error in getFundamentals: " + e.getMessage());
	    }
	    return null;
	}
		
	// GET method for existOrder
	public Order getExistOrder(String orderId) {
	    String sql = "SELECT * FROM existOrder WHERE orderId = ?";
	    try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setString(1, orderId);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return new Order(
	                rs.getString("orderId"),
	                rs.getString("stockCode"),
	                rs.getString("side"),
	                rs.getInt("quantity"),
	                rs.getDouble("orderPrice"),
	                LocalDate.parse(rs.getString("orderDate"))
	            );
	        }
	    } catch (SQLException e) {
	        System.out.println("Error in getExistOrder: " + e.getMessage());
	    }
	    return null;
	}

	// GET method for holdingStock
	public Holding getHoldingStock(String stockCode) {
	    String sql = "SELECT * FROM holdingStock WHERE stockCode = ?";
	    try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setString(1, stockCode);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return new Holding(
	                rs.getString("stockCode"),
	                rs.getInt("quantity"),
	                rs.getDouble("avgPrice")
	            );
	        }

	    } catch (SQLException e) {
	        System.out.println("Error in getHoldingStock: " + e.getMessage());
	    }
	    return null;
	}
	
	public List<Order> getAllExistOrders() {
	    String sql = "SELECT * FROM existOrder";
	    List<Order> orders = new ArrayList<>();

	    try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        ResultSet rs = pstmt.executeQuery();

	        while (rs.next()) {
	            orders.add(new Order(
	            	rs.getString("orderId"),
		            rs.getString("stockCode"),
		            rs.getString("side"),
		            rs.getInt("quantity"),
		            rs.getDouble("orderPrice"),
		            LocalDate.parse(rs.getString("orderDate"))
	            ));
	        }
	    } catch (SQLException e) {
	        System.out.println("Error in getAllHoldingStocks: " + e.getMessage());
	    }

	    return orders;
	}
	
	public List<Holding> getAllHoldingStocks() {
	    String sql = "SELECT * FROM holdingStock";
	    List<Holding> holdings = new ArrayList<>();

	    try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        ResultSet rs = pstmt.executeQuery();

	        while (rs.next()) {
	            holdings.add(new Holding(
	                rs.getString("stockCode"),
	                rs.getInt("quantity"),
	                rs.getDouble("avgPrice")
	            ));
	        }
	    } catch (SQLException e) {
	        System.out.println("Error in getAllHoldingStocks: " + e.getMessage());
	    }

	    return holdings;
	}
}
