/*
 * PROJECT: TENDER FOOD APP (FINAL v3 - COMPLETE)
 * FEATURES:
 * 1. Real-time Bidding & Deal Feature
 * 2. "Buyer is King" Logic (Cheapest Price Auto-sort)
 * 3. QUICK TAGS: "Something sweet", "Simple", "A lot"
 * 4. BEST OFFER NOW: One-click auto deal (Point 3 in Task)
 * 5. Full English Interface
 */

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;

// ==========================================
// 1. MODEL (DATA)
// ==========================================
class Bid implements Comparable<Bid> {
    private String sellerName;
    private String description;
    private int price;

    public Bid(String sellerName, String description, int price) {
        this.sellerName = sellerName;
        this.description = description;
        this.price = price;
    }

    public String getSellerName() { return sellerName; }
    public int getPrice() { return price; }

    @Override
    public String toString() {
        return String.format("[Rp %,d]  %s  -  %s", price, sellerName, description);
    }

    // Sort Cheapest first
    @Override
    public int compareTo(Bid other) {
        return Integer.compare(this.price, other.price);
    }
}

// ==========================================
// 2. CONTROLLER & MEDIATOR
// ==========================================
interface AuctionMediator {
    void broadcastRequest(String itemWanted);
    void submitBid(Bid bid);
    void announceWinner(String winnerName);
}

class AuctionController implements AuctionMediator {
    private BuyerPanel buyer;
    private List<SellerPanel> sellers = new ArrayList<>();
    private boolean isTenderOpen = true;

    public void registerBuyer(BuyerPanel buyer) { this.buyer = buyer; }
    public void addSeller(SellerPanel seller) { this.sellers.add(seller); }

    @Override
    public void broadcastRequest(String itemWanted) {
        isTenderOpen = true; 
        buyer.resetStatus(); 
        for (SellerPanel seller : sellers) {
            seller.receiveNotification(itemWanted);
        }
    }

    @Override
    public void submitBid(Bid bid) {
        if (isTenderOpen) buyer.receiveBid(bid);
    }

    @Override
    public void announceWinner(String winnerName) {
        isTenderOpen = false;
        for (SellerPanel seller : sellers) {
            if (seller.getShopName().equals(winnerName)) seller.onWin();
            else seller.onLose(winnerName);
        }
        buyer.onDealClosed(winnerName);
    }
}

// ==========================================
// 3. VIEW (GUI PANELS)
// ==========================================

// --- BUYER PANEL (THE KING) ---
class BuyerPanel extends JPanel {
    private AuctionMediator mediator;
    private DefaultListModel<Bid> listModel;
    private ArrayList<Bid> allBids;
    private JButton dealBtn;
    private JButton bestOfferBtn; // NEW BUTTON
    private JLabel statusLabel;
    private JList<Bid> bidList;
    private JTextField requestField;

    public BuyerPanel(AuctionMediator mediator) {
        this.mediator = mediator;
        this.allBids = new ArrayList<>();
        
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(225, 245, 255)); 
        
        TitledBorder border = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(0, 102, 204), 2), 
            "BUYER (The King) - Request Your Meal"
        );
        border.setTitleFont(new Font("Arial", Font.BOLD, 14));
        border.setTitleColor(new Color(0, 51, 102));
        setBorder(border);

        // -- TOP AREA: Input + Quick Tags --
        JPanel topContainer = new JPanel(new BorderLayout(5, 5));
        topContainer.setOpaque(false);

        // Input Row
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setOpaque(false);
        requestField = new JTextField();
        JButton broadcastBtn = new JButton("START TENDER");
        broadcastBtn.setFont(new Font("Arial", Font.BOLD, 12));
        broadcastBtn.setForeground(Color.BLACK);
        
        inputPanel.add(new JLabel("I want: "), BorderLayout.WEST);
        inputPanel.add(requestField, BorderLayout.CENTER);
        inputPanel.add(broadcastBtn, BorderLayout.EAST);

        // Quick Tags Row
        JPanel tagsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        tagsPanel.setOpaque(false);
        tagsPanel.add(new JLabel("Quick Add: "));
        addTagButton(tagsPanel, "+ Something Sweet");
        addTagButton(tagsPanel, "+ A Lot");
        addTagButton(tagsPanel, "+ Simple");
        addTagButton(tagsPanel, "+ Last Longer");

        topContainer.add(inputPanel, BorderLayout.NORTH);
        topContainer.add(tagsPanel, BorderLayout.CENTER);

        // -- CENTER AREA --
        listModel = new DefaultListModel<>();
        bidList = new JList<>(listModel);
        bidList.setFont(new Font("Monospaced", Font.BOLD, 13));

        // -- BOTTOM AREA (ACTIONS) --
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        
        statusLabel = new JLabel("Waiting for offers...");
        statusLabel.setForeground(Color.GRAY);
        
        // Manual Deal Button
        dealBtn = new JButton("Select Manually");
        dealBtn.setBackground(new Color(100, 149, 237)); // Cornflower Blue
        dealBtn.setForeground(Color.WHITE);
        dealBtn.setEnabled(false);

        // NEW: BEST OFFER NOW BUTTON (POINT 3 IN TASK)
        bestOfferBtn = new JButton("⚡ GET BEST OFFER NOW!");
        bestOfferBtn.setBackground(new Color(255, 69, 0)); // Red Orange (Urgent/Hot)
        bestOfferBtn.setForeground(Color.WHITE);
        bestOfferBtn.setFont(new Font("Arial", Font.BOLD, 12));
        bestOfferBtn.setEnabled(false); // Disabled until bids arrive

        bottomPanel.add(statusLabel);
        bottomPanel.add(dealBtn);
        bottomPanel.add(bestOfferBtn);

        add(topContainer, BorderLayout.NORTH);
        add(new JScrollPane(bidList), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // -- ACTIONS --
        broadcastBtn.addActionListener(e -> startTender());
        requestField.addActionListener(e -> startTender());

        // Manual Deal Action
        dealBtn.addActionListener(e -> {
            Bid selected = bidList.getSelectedValue();
            if (selected != null) confirmDeal(selected);
        });

        // BEST OFFER NOW Action (The new feature)
        bestOfferBtn.addActionListener(e -> {
            if (!allBids.isEmpty()) {
                // Because list is sorted, index 0 is ALWAYS the best (cheapest)
                Bid bestBid = allBids.get(0); 
                confirmDeal(bestBid);
            }
        });

        bidList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && bidList.getSelectedValue() != null) dealBtn.setEnabled(true);
        });
    }

    private void addTagButton(JPanel panel, String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.PLAIN, 10));
        btn.setMargin(new Insets(2, 5, 2, 5));
        btn.setBackground(Color.WHITE);
        btn.addActionListener(e -> {
            String current = requestField.getText();
            String tag = text.replace("+ ", "");
            if (current.isEmpty()) requestField.setText(tag);
            else requestField.setText(current + ", " + tag);
        });
        panel.add(btn);
    }

    private void startTender() {
        String item = requestField.getText();
        if (!item.isEmpty()) {
            allBids.clear();
            listModel.clear();
            dealBtn.setEnabled(false);
            bestOfferBtn.setEnabled(false);
            statusLabel.setText("Broadcasting... Waiting for sellers...");
            mediator.broadcastRequest(item);
        }
    }

    private void confirmDeal(Bid bid) {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Accept Best Offer from: " + bid.getSellerName() + "\nPrice: Rp " + bid.getPrice(),
            "Confirm Transaction", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) mediator.announceWinner(bid.getSellerName());
    }

    public void receiveBid(Bid bid) {
        allBids.add(bid);
        Collections.sort(allBids);
        listModel.clear();
        for (Bid b : allBids) listModel.addElement(b);
        statusLabel.setText(allBids.size() + " offers received.");
        
        // Enable "Best Offer Now" as soon as we have at least 1 bid
        if (!allBids.isEmpty()) bestOfferBtn.setEnabled(true);
    }

    public void onDealClosed(String winnerName) {
        statusLabel.setText("DEAL CLOSED! Winner: " + winnerName);
        statusLabel.setForeground(new Color(0, 100, 0));
        dealBtn.setEnabled(false);
        bestOfferBtn.setEnabled(false);
        JOptionPane.showMessageDialog(this, "Order Finalized!\nWinner: " + winnerName);
    }
    
    public void resetStatus() {
        statusLabel.setText("Waiting for offers...");
        statusLabel.setForeground(Color.GRAY);
        dealBtn.setEnabled(false);
        bestOfferBtn.setEnabled(false);
    }
}

// --- SELLER PANEL ---
class SellerPanel extends JPanel {
    private AuctionMediator mediator;
    private String shopName;
    private JLabel statusLabel;
    private JTextField descField, priceField;
    private JButton submitBtn;

    public SellerPanel(AuctionMediator mediator, String shopName) {
        this.mediator = mediator;
        this.shopName = shopName;
        setLayout(new GridLayout(5, 1, 5, 5));
        setBackground(Color.WHITE);
        
        TitledBorder border = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1), shopName);
        border.setTitleFont(new Font("SansSerif", Font.BOLD, 12));
        setBorder(border);
        
        statusLabel = new JLabel("Waiting...");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        descField = new JTextField("Menu Name");
        priceField = new JTextField("0");
        submitBtn = new JButton("Submit Bid");
        submitBtn.setEnabled(false);

        add(statusLabel);
        add(new JLabel("Menu:"));
        add(descField);
        add(new JLabel("Price (Rp):"));
        add(priceField);
        add(submitBtn);

        submitBtn.addActionListener(e -> {
            try {
                int price = Integer.parseInt(priceField.getText());
                mediator.submitBid(new Bid(shopName, descField.getText(), price));
                statusLabel.setText("Bid Sent!");
                statusLabel.setForeground(Color.BLUE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid Price!");
            }
        });
    }

    public String getShopName() { return shopName; }

    public void receiveNotification(String itemWanted) {
        statusLabel.setText("<html><center>Order: <b>" + itemWanted + "</b></center></html>");
        statusLabel.setForeground(Color.RED);
        descField.setText(itemWanted); 
        submitBtn.setEnabled(true);
    }

    public void onWin() {
        setBackground(new Color(200, 255, 200));
        statusLabel.setText("<html><center><b>WINNER!</b></center></html>");
        statusLabel.setForeground(new Color(0, 100, 0));
        submitBtn.setEnabled(false);
    }

    public void onLose(String winnerName) {
        setBackground(new Color(240, 240, 240));
        statusLabel.setText("<html><center>Closed.</center></html>");
        statusLabel.setForeground(Color.GRAY);
        submitBtn.setEnabled(false);
    }
}

// ==========================================
// 4. MAIN CLASS
// ==========================================
public class TenderSystemApp {
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            AuctionController controller = new AuctionController();
            JFrame frame = new JFrame("Tender Food - Real-time Auction");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new GridLayout(2, 1));

            BuyerPanel buyer = new BuyerPanel(controller);
            controller.registerBuyer(buyer);
            frame.add(buyer);

            JPanel sellersArea = new JPanel(new GridLayout(1, 3, 10, 0));
            sellersArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            sellersArea.setBackground(Color.WHITE);
            
            SellerPanel s1 = new SellerPanel(controller, "RM Sederhana");
            SellerPanel s2 = new SellerPanel(controller, "Warteg Kharisma");
            SellerPanel s3 = new SellerPanel(controller, "RM Pagi Sore");

            controller.addSeller(s1);
            controller.addSeller(s2);
            controller.addSeller(s3);

            sellersArea.add(s1);
            sellersArea.add(s2);
            sellersArea.add(s3);
            
            frame.add(sellersArea);
            frame.setSize(950, 750); // Sedikit lebih tinggi untuk muat tombol baru
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}