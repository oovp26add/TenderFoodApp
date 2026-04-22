import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BuyerPanel extends JPanel {
    private AuctionMediator mediator;
    private DefaultListModel<Bid> listModel;
    private ArrayList<Bid> allBids;
    private JButton dealBtn;
    private JButton bestOfferBtn;
    private JButton resetBtn;
    private JLabel statusLabel;
    private JList<Bid> bidList;
    private JTextField requestField;
    private JTextField addressField;
    private FrequentlyAskedPanel freqPanel;
    private ChatPanel chatPanel;
    private JTextArea historyArea;

    public BuyerPanel(AuctionMediator mediator) {
        this.mediator = mediator;
        this.allBids = new ArrayList<>();

        setLayout(new BorderLayout());
        setBackground(new Color(221, 238, 206)); // Light Green

        JTabbedPane tabs = new JTabbedPane();
        
        // --- Tab 1: Dashboard ---
        JPanel dashboard = new JPanel(new BorderLayout(10, 10));
        dashboard.setOpaque(false);
        setupDashboard(dashboard);
        tabs.addTab("🛒 Dashboard", dashboard);

        // --- Tab 2: Map ---
        String mapPath = "C:\\Users\\LENOVO\\.gemini\\antigravity\\brain\\425bb86e-e0f0-4517-af1a-c4b1f3f2e469\\modern_food_map_1773203470017.png";
        tabs.addTab("🗺️ Delivery Map", new MapPanel(mapPath));

        // --- Tab 3: Chat ---
        chatPanel = new ChatPanel(mediator, "The King (Buyer)");
        tabs.addTab("💬 Chat", chatPanel);

        // --- Tab 4: History ---
        historyArea = new JTextArea();
        historyArea.setEditable(false);
        historyArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        refreshHistory();
        tabs.addTab("📜 History", new JScrollPane(historyArea));

        add(tabs, BorderLayout.CENTER);
    }

    private void setupDashboard(JPanel p) {
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(150, 180, 130), 2),
                "BUYER DASHBOARD");
        border.setTitleFont(new Font("Arial", Font.BOLD, 14));
        p.setBorder(border);

        // -- TOP AREA --
        JPanel topContainer = new JPanel(new GridLayout(3, 1, 5, 2));
        topContainer.setOpaque(false);

        JPanel inputRow = new JPanel(new BorderLayout(5, 5));
        inputRow.setOpaque(false);
        requestField = new JTextField();
        JButton broadcastBtn = new JButton("START TENDER");
        broadcastBtn.setFont(new Font("Arial", Font.BOLD, 12));
        
        inputRow.add(new JLabel("Request: "), BorderLayout.WEST);
        inputRow.add(requestField, BorderLayout.CENTER);
        inputRow.add(broadcastBtn, BorderLayout.EAST);

        JPanel addressRow = new JPanel(new BorderLayout(5, 5));
        addressRow.setOpaque(false);
        addressField = new JTextField("My Home");
        addressRow.add(new JLabel("Address: "), BorderLayout.WEST);
        addressRow.add(addressField, BorderLayout.CENTER);

        freqPanel = new FrequentlyAskedPanel(requestField, this::updateFrequencyList);
        
        topContainer.add(inputRow);
        topContainer.add(addressRow);
        topContainer.add(freqPanel);

        // -- CENTER AREA --
        listModel = new DefaultListModel<>();
        bidList = new JList<>(listModel);
        bidList.setFont(new Font("Monospaced", Font.BOLD, 13));

        // -- BOTTOM AREA --
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);

        statusLabel = new JLabel(" Ready. ");
        statusLabel.setOpaque(true);
        statusLabel.setBackground(new Color(255, 255, 0)); 
        statusLabel.setForeground(Color.BLACK);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 12));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);

        dealBtn = new JButton("Manual Select");
        dealBtn.setBackground(new Color(50, 255, 50));
        dealBtn.setEnabled(false);

        bestOfferBtn = new JButton("⚡ BEST OFFER");
        bestOfferBtn.setBackground(new Color(255, 255, 0));
        bestOfferBtn.setEnabled(false);

        resetBtn = new JButton("NEW TENDER");
        resetBtn.setBackground(new Color(255, 0, 0));
        resetBtn.setForeground(Color.WHITE);
        resetBtn.setVisible(false);

        actions.add(dealBtn);
        actions.add(bestOfferBtn);
        actions.add(resetBtn);

        bottomPanel.add(statusLabel, BorderLayout.WEST);
        bottomPanel.add(actions, BorderLayout.EAST);

        p.add(topContainer, BorderLayout.NORTH);
        p.add(new JScrollPane(bidList), BorderLayout.CENTER);
        p.add(bottomPanel, BorderLayout.SOUTH);

        // -- ACTIONS --
        broadcastBtn.addActionListener(e -> startTender());
        requestField.addActionListener(e -> startTender());
        dealBtn.addActionListener(e -> {
            Bid selected = bidList.getSelectedValue();
            if (selected != null) confirmDeal(selected);
        });
        bestOfferBtn.addActionListener(e -> {
            if (!allBids.isEmpty()) confirmDeal(allBids.get(0));
        });
        bidList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) dealBtn.setEnabled(bidList.getSelectedValue() != null);
        });
        resetBtn.addActionListener(e -> resetTender());
    }

    private void resetTender() {
        requestField.setText("");
        allBids.clear();
        listModel.clear();
        statusLabel.setText("Ready for new tender.");
        statusLabel.setBackground(new Color(255, 255, 0));
        resetBtn.setVisible(false);
        dealBtn.setEnabled(false);
        bestOfferBtn.setEnabled(false);
    }

    private void startTender() {
        String item = requestField.getText();
        if (!item.isEmpty()) {
            allBids.clear();
            listModel.clear();
            dealBtn.setEnabled(false);
            bestOfferBtn.setEnabled(false);
            statusLabel.setText("Broadcasting request...");
            mediator.broadcastRequest(item);
        }
    }

    private void confirmDeal(Bid bid) {
        // --- PAYMENT FLOW ---
        statusLabel.setText("💳 Processing Payment...");
        statusLabel.setBackground(Color.ORANGE);
        
        Timer timer = new Timer(2000, e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Payment Successful! Finalize order from " + bid.getSellerName() + " for Rp " + bid.getPrice() + "?",
                "Final Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                DatabaseManager.saveOrder(requestField.getText(), bid.getSellerName(), bid.getPrice(), addressField.getText());
                mediator.announceWinner(bid.getSellerName());
                updateFrequencyList();
                refreshHistory();
            } else {
                statusLabel.setText("Payment Cancelled.");
                statusLabel.setBackground(new Color(255, 255, 0));
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    public void updateFrequencyList() {
        freqPanel.refresh();
    }

    public void refreshHistory() {
        if (historyArea == null) return;
        historyArea.setText("");
        List<String> orders = DatabaseManager.getOrdersHistory();
        for (String order : orders) {
            historyArea.append(order.replace("|", "  |  ") + "\n");
        }
    }

    public void receiveBid(Bid bid) {
        allBids.add(bid);
        Collections.sort(allBids);
        listModel.clear();
        for (Bid b : allBids) listModel.addElement(b);
        statusLabel.setText(allBids.size() + " offers received.");
        bestOfferBtn.setEnabled(true);
    }

    public void receiveChatMessage(String from, String msg) {
        chatPanel.addMessage(from, msg);
    }

    public void onDealClosed(String winnerName) {
        statusLabel.setText("<html>DEAL CLOSED with " + winnerName + ".<br>Click <b>NEW TENDER</b> to start again!</html>");
        statusLabel.setBackground(new Color(50, 255, 50));
        dealBtn.setEnabled(false);
        bestOfferBtn.setEnabled(false);
        resetBtn.setVisible(true);
        JOptionPane.showMessageDialog(this, "Order Finalized for " + winnerName + "\n\nInfo: You can now click 'NEW TENDER' to start a search for your next meal!");
    }
    
    public void resetStatus() {
        statusLabel.setText("Waiting for offers...");
        statusLabel.setBackground(new Color(255, 255, 0));
        dealBtn.setEnabled(false);
        bestOfferBtn.setEnabled(false);
        resetBtn.setVisible(false);
    }
}
