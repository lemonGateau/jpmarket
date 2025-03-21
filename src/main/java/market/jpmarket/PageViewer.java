package market.jpmarket;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

public class PageViewer {
	private static PageController pageController = new PageController();
    private static LocalDate currentDate;

    private static final Integer PAGEWIDTH  = 1250;
    private static final Integer PAGEHEIGHT = 800;
	
    public static void main(String[] args) {
        currentDate = LocalDate.now().minusDays(300);
        currentDate = pageController.skipHolidays(currentDate);
        
        SwingUtilities.invokeLater(() -> {
        	PageViewer.generateMainPage(currentDate);
        });
    }
    
    public static void generateMainPage(LocalDate date) {
        JFrame frame = new JFrame(date.toString());
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(PAGEWIDTH, PAGEHEIGHT);
        frame.setJMenuBar(createMenuBar(date));

        JPanel mainPanel = new JPanel(new BorderLayout());
        frame.add(mainPanel);
                
        // 登録銘柄テーブル
        JTable stockTable = pageController.createStockTable(date);
        
        stockTable.setRowHeight(calcTableHeight(stockTable.getRowCount()));
        
        stockTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = stockTable.rowAtPoint(e.getPoint());
                int column = stockTable.columnAtPoint(e.getPoint());
                if (row >= 0 && column == 0) { // Check if the clicked column is "銘柄コード"
                    String stockCode = (String) stockTable.getValueAt(row, column);
                    
                    generateStockInfoPage(stockCode, date);
                }
            }
        });

        // 日付更新
        JButton button = new JButton("日付を進める");
        JLabel dateLabel = new JLabel("現在の日付: " + currentDate);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	currentDate = pageController.update(currentDate);
            	generateMainPage(currentDate);
            }
        });

        mainPanel.add(button, BorderLayout.WEST);
        
        JScrollPane StockScrollPane = new JScrollPane(stockTable);
        mainPanel.add(StockScrollPane, BorderLayout.NORTH);

        // フレームの表示
        frame.setVisible(true);
    }

    private static void generateAccountPage(LocalDate date) {
        JFrame frame = new JFrame("株取引シミュレーション");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(PAGEWIDTH, PAGEHEIGHT);
        frame.setJMenuBar(createMenuBar(date));

        JPanel mainPanel = new JPanel(new BorderLayout());
        frame.add(mainPanel);
        
        
        // 保有銘柄テーブル
        JTable holdingStockTable = pageController.createHoldingStockTable(date);

        JScrollPane holdingStockScrollPane = new JScrollPane(holdingStockTable);
        mainPanel.add(holdingStockScrollPane, BorderLayout.NORTH);

        // フレームの表示
        frame.setVisible(true);
    }
    
    private static void generateExistOrderPage(LocalDate date) {
        JFrame frame = new JFrame("株取引シミュレーション");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(PAGEWIDTH, PAGEHEIGHT);
        frame.setJMenuBar(createMenuBar(date));

        JPanel mainPanel = new JPanel(new BorderLayout());
        frame.add(mainPanel);
        
        
        // 現注文テーブル
        JTable existOrderTable = pageController.createExistOrderTable(date);
        
        existOrderTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = existOrderTable.rowAtPoint(e.getPoint());
                int column = existOrderTable.columnAtPoint(e.getPoint());
                if (row >= 0 && column == 0) {
                    String orderId = (String) existOrderTable.getValueAt(row, column);
                    
                    openCancelDialog(orderId);
                }
            }
        });
        JScrollPane currentOrderScrollPane = new JScrollPane(existOrderTable);
        mainPanel.add(currentOrderScrollPane, BorderLayout.NORTH);

        // フレームの表示
        frame.setVisible(true);
    }
    
    private static void generateStockInfoPage(String stockCode, LocalDate date) {
        JFrame frame = new JFrame(date.toString());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(PAGEWIDTH, PAGEHEIGHT);
        frame.setJMenuBar(createMenuBar(date));

        JPanel mainPanel = new JPanel(new BorderLayout());
        frame.add(mainPanel);
        
        // ポートフォリオグラフ
        JFreeChart portfolioChart = pageController.createCloseChart(stockCode, date);
        ChartPanel portfolioChartPanel = new ChartPanel(portfolioChart);
        portfolioChartPanel.setPreferredSize(new Dimension(400, 300));
        mainPanel.add(portfolioChartPanel, BorderLayout.NORTH);
        
        // テクニカルズテーブル
        JTable technicalsTable = pageController.createTechnicalsTable(stockCode, date);
        technicalsTable.setRowHeight(calcTableHeight(10));

        JScrollPane technicalsScrollPane = new JScrollPane(technicalsTable);
        mainPanel.add(technicalsScrollPane, BorderLayout.WEST);

        // ファンダメンタルズテーブル
        JTable fundamentalsTable = pageController.createFundamentalsTable(stockCode);
        fundamentalsTable.setRowHeight(calcTableHeight(10));

        JScrollPane stockDetailScrollPane = new JScrollPane(fundamentalsTable);
        mainPanel.add(stockDetailScrollPane, BorderLayout.EAST);
        
        // 売買ボタン
        JButton orderButton = new JButton("注文");
        orderButton.addActionListener(e -> openOrderDialog(stockCode, date));

        mainPanel.add(orderButton, BorderLayout.CENTER);
        
        // フレームの表示
        frame.setVisible(true);
    }
        
    // Todo メニューごとにメソッド分割？
    private static JMenuBar createMenuBar(LocalDate date) {
        // メニューバーの作成
        JMenuBar menuBar = new JMenuBar();

        // 口座管理メニュー
        JMenu accountMenu = new JMenu("口座管理");
        menuBar.add(accountMenu);

        JMenuItem holdingStockItem = new JMenuItem("保有銘柄");
        holdingStockItem.addActionListener(e -> generateAccountPage(date));
        accountMenu.add(holdingStockItem);
        
        // 注文一覧メニュー
        JMenu currentOrderMenu = new JMenu("注文照会");
        menuBar.add(currentOrderMenu);

        JMenuItem currentOrderItem = new JMenuItem("注文一覧");
        currentOrderItem.addActionListener(e -> generateExistOrderPage(date));
        currentOrderMenu.add(currentOrderItem);
        
        return menuBar;
    }

    private static Integer calcTableHeight(Integer numRows) {
    	return PAGEHEIGHT / 2 / (numRows + 1);
    }
    
    private static void openOrderDialog(String stockCode, LocalDate date) {
        JDialog orderDialog = new JDialog();
        
        orderDialog.setTitle("注文");
        orderDialog.setSize(300, 200);
        orderDialog.setLocationRelativeTo(null);
        
        JPanel panel = new JPanel(new GridLayout(4, 2));
        
        String[] sides = {"Bid", "Ask"};
        JComboBox sideBox = new JComboBox(sides);
        Integer[] quantities = {100, 200, 300, 400, 500, 600, 700, 800, 900, 1000};
        JComboBox quantityBox = new JComboBox(quantities);
        
        JTextField priceField    = new JTextField();

        panel.add(new JLabel("サイド:"));
        panel.add(sideBox);
        panel.add(new JLabel("数量:"));
        panel.add(quantityBox);
        panel.add(new JLabel("価格:"));
        panel.add(priceField);
        
        JPanel buttonPanel = new JPanel();
        JButton orderButton = new JButton("注文");
        
        orderButton.addActionListener(e -> {
            // 買い注文処理を実装
        	String side  = sideBox.getSelectedItem().toString();
            Integer quantity = Integer.parseInt(quantityBox.getSelectedItem().toString());
            Double price = Double.parseDouble(priceField.getText());
                        
            if (side == "Bid") {
            	pageController.bidOrder(stockCode, quantity, price, date);
            } else {
            	pageController.askOrder(stockCode, quantity, price, date);
            }
            
            // 注文処理の後、ダイアログを閉じる
            orderDialog.dispose();
        });
        
        buttonPanel.add(orderButton);
        panel.add(buttonPanel);
        orderDialog.add(panel);
        orderDialog.setVisible(true);
    }
    
    private static void openCancelDialog(String orderId) {
        JDialog cancelDialog = new JDialog();
        
        cancelDialog.setTitle("注文");
        cancelDialog.setSize(300, 200);
        cancelDialog.setLocationRelativeTo(null);
        
        JPanel panel = new JPanel(new GridLayout(4, 2));
        
        JPanel buttonPanel = new JPanel();
        JButton cancelButton = new JButton("キャンセル");
        
        cancelButton.addActionListener(e -> {
        	pageController.cancelOrder(orderId);

            // 注文処理の後、ダイアログを閉じる
            cancelDialog.dispose();
        });
        
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel);
        cancelDialog.add(panel);
        cancelDialog.setVisible(true);
    }
}



/*
// 利益・損失のグラフを追加
profitLossDataset = new DefaultCategoryDataset();
JFreeChart profitLossChart = createProfitLossChart(profitLossDataset);
ChartPanel profitLossChartPanel = new ChartPanel(profitLossChart);
profitLossChartPanel.setPreferredSize(new Dimension(400, 300));
mainPanel.add(profitLossChartPanel, BorderLayout.EAST);
*/

