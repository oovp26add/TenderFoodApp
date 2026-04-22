import javax.swing.*;
import java.awt.*;

public class TenderSystemApp {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            AuctionController controller = new AuctionController();

            // --- BUYER WINDOW ---
            JFrame buyerFrame = new JFrame("Tender Food - Buyer");
            buyerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            BuyerPanel buyer = new BuyerPanel(controller);
            controller.registerBuyer(buyer);
            buyerFrame.add(buyer);
            buyerFrame.setSize(500, 600);
            buyerFrame.setLocation(100, 100);
            buyerFrame.setVisible(true);

            // --- SELLER DASHBOARD ---
            JFrame sellerDashboard = new JFrame("Tender Food - Seller Dashboard");
            sellerDashboard.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            JPanel sellerContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
            sellerContainer.setBackground(new Color(221, 238, 206)); // Light Green
            
            String[] shopNames = { "RM Sederhana", "Warteg Kharisma", "Warung Bu Siti", "Dapur Mama", "OpenClaw (AI)", "ZeroClaw (AI)", "AgentZero (AI)" };
            
            for (String name : shopNames) {
                SellerPanel seller = new SellerPanel(controller, name);
                if (name.contains("(AI)")) {
                    seller.setAgenticMode(true);
                }
                controller.addSeller(seller);
                sellerContainer.add(seller);
                seller.setPreferredSize(new Dimension(300, 450));
            }

            JScrollPane scrollPane = new JScrollPane(sellerContainer);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);
            
            sellerDashboard.add(scrollPane);
            sellerDashboard.setSize(1000, 520);
            sellerDashboard.setLocation(620, 100);
            sellerDashboard.setVisible(true);
        });
    }
}