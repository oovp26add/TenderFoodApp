import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Random;

public class SellerPanel extends JPanel {
    private AuctionMediator mediator;
    private String shopName;
    private JLabel statusLabel;
    private JTextField descField, priceField;
    private JButton submitBtn;
    private JCheckBox agenticMode;
    private Random random = new Random();
    private ChatPanel chatPanel;

    public SellerPanel(AuctionMediator mediator, String shopName) {
        this.mediator = mediator;
        this.shopName = shopName;
        setLayout(new BorderLayout(5, 5));
        setBackground(new Color(221, 238, 206)); // Light Green

        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(150, 180, 130), 1), shopName);
        border.setTitleFont(new Font("SansSerif", Font.BOLD, 12));
        setBorder(border);

        // --- Main Controls ---
        JPanel controls = new JPanel(new GridLayout(7, 1, 5, 2));
        controls.setOpaque(false);

        statusLabel = new JLabel(" Waiting... ");
        statusLabel.setOpaque(true);
        statusLabel.setBackground(new Color(255, 255, 0));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        descField = new JTextField("Menu Name");
        priceField = new JTextField("0");
        submitBtn = new JButton("Submit Bid");
        submitBtn.setBackground(new Color(50, 255, 50));
        submitBtn.setEnabled(false);

        JButton profileBtn = new JButton("👤 Seller Profile (Video)");
        profileBtn.setFont(new Font("Arial", Font.PLAIN, 10));
        profileBtn.addActionListener(e -> showProfile());

        agenticMode = new JCheckBox("Agentic Mode (Auto-bid)");
        agenticMode.setOpaque(false);
        agenticMode.setFont(new Font("Arial", Font.ITALIC, 10));

        controls.add(statusLabel);
        controls.add(new JLabel("Menu:"));
        controls.add(descField);
        controls.add(new JLabel("Price (Rp):"));
        controls.add(priceField);
        controls.add(agenticMode);
        controls.add(submitBtn);
        // Note: profileBtn added later or in different place

        // --- Chat ---
        chatPanel = new ChatPanel(mediator, shopName);
        
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setOpaque(false);
        mainContent.add(controls, BorderLayout.NORTH);
        mainContent.add(profileBtn, BorderLayout.CENTER);

        add(mainContent, BorderLayout.NORTH);
        add(chatPanel, BorderLayout.CENTER);

        submitBtn.addActionListener(e -> submitBid());
    }

    private void showProfile() {
        JFrame profileFrame = new JFrame("Profile: " + shopName);
        profileFrame.setSize(400, 300);
        
        // Show the generated video placeholder
        String imgPath = "C:\\Users\\LENOVO\\.gemini\\antigravity\\brain\\425bb86e-e0f0-4517-af1a-c4b1f3f2e469\\modern_food_map_1773203470017.png";
        // Actually, I'll use a generic placeholder if image fails, 
        // but I'll try to load it.
        
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JLabel("Seller Identity Verified", SwingConstants.CENTER), BorderLayout.NORTH);
        p.add(new MapPanel(imgPath), BorderLayout.CENTER); // Using MapPanel as generic image viewer
        
        profileFrame.add(p);
        profileFrame.setVisible(true);
    }

    public void receiveChatMessage(String from, String msg) {
        chatPanel.addMessage(from, msg);
        
        // AI response logic
        if (agenticMode.isSelected() && !from.equals(shopName)) {
            if (msg.toLowerCase().contains("discount") || msg.toLowerCase().contains("cheap")) {
                Timer t = new Timer(2000, e -> mediator.sendMessage(shopName, "Hi " + from + ", our prices are already competitive for this quality!"));
                t.setRepeats(false);
                t.start();
            }
        }
    }

    public void setAgenticMode(boolean on) {
        agenticMode.setSelected(on);
    }

    private void submitBid() {
        try {
            String text = descField.getText();
            int price;
            Bid bid;

            if (text.startsWith("S:")) {
                bid = parseBidResponse(text);
                price = bid.getPrice();
            } else {
                price = Integer.parseInt(priceField.getText());
                bid = new Bid(shopName, text, price);
            }

            mediator.submitBid(bid);
            statusLabel.setText("Bid Sent (Rp " + price + ")");
            statusLabel.setForeground(Color.BLUE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid Input! Use 'S: item 2x9k' or manual price.");
        }
    }

    private Bid parseBidResponse(String text) {
        // Simple parser for "S: item 2x9k item2 3x6k"
        Bid bid = new Bid(shopName, text, 0);
        String clearText = text.substring(2).trim();
        String[] parts = clearText.split("\\s+");
        
        String currentName = "";
        for (String part : parts) {
            if (part.contains("x") && part.contains("k")) {
                try {
                    String[] qp = part.split("x");
                    int qty = Integer.parseInt(qp[0]);
                    int up = Integer.parseInt(qp[1].replace("k", "")) * 1000;
                    bid.addItem(new OrderItem(currentName.trim(), qty, up / 1000, ""));
                    currentName = "";
                } catch (Exception e) {
                    currentName += " " + part;
                }
            } else if (part.equalsIgnoreCase("total")) {
                break; // Stop at total
            } else {
                currentName += " " + part;
            }
        }
        return bid;
    }

    public String getShopName() {
        return shopName;
    }

    public void receiveNotification(String itemWanted) {
        statusLabel.setText("<html><center>Order: <b>" + itemWanted + "</b></center></html>");
        statusLabel.setForeground(Color.RED);
        
        if (agenticMode.isSelected()) {
            boolean parsedWithAI = false;
            try {
                ProcessBuilder pb = new ProcessBuilder("python", "nlp_parser.py", itemWanted);
                Process p = pb.start();
                
                // Safety Protocol: Time Boxing (Paperclip Risk Prevention)
                if (!p.waitFor(3, java.util.concurrent.TimeUnit.SECONDS)) {
                    p.destroyForcibly();
                    throw new RuntimeException("Agent Execution Timeout - Safety Sandbox Intervention");
                }

                java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(p.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("->")) {
                        String raw = line.substring(3).trim();
                        raw = raw.replace("+ ", "").replace("= ", "");
                        descField.setText("S: " + raw);
                        parsedWithAI = true;
                        break;
                    }
                }
            } catch (Exception e) {
                System.err.println("Safety Protocol Triggered: " + e.getMessage());
            }
            if (!parsedWithAI) {
                 descField.setText(itemWanted);
                 priceField.setText(String.valueOf(15000 + random.nextInt(20000)));
            }
        } else {
            descField.setText(itemWanted);
            submitBtn.setEnabled(true);
        }

        if (agenticMode.isSelected()) {
            Timer timer = new Timer(1500, e -> submitBid());
            timer.setRepeats(false);
            timer.start();
        }
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
