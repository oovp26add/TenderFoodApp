import javax.swing.*;
import java.awt.*;

public class MapPanel extends JPanel {
    private Image mapImage;

    public MapPanel(String imagePath) {
        try {
            ImageIcon icon = new ImageIcon(imagePath);
            mapImage = icon.getImage();
        } catch (Exception e) {
            System.err.println("Could not load map image: " + e.getMessage());
        }
        
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createTitledBorder("Delivery Map"));
        
        JLabel mapLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (mapImage != null) {
                    g.drawImage(mapImage, 0, 0, getWidth(), getHeight(), this);
                } else {
                    g.setColor(Color.WHITE);
                    g.fillRect(0, 0, getWidth(), getHeight());
                    g.setColor(Color.GRAY);
                    g.drawString("Map Unavailable", getWidth()/2 - 40, getHeight()/2);
                }
            }
        };
        add(mapLabel, BorderLayout.CENTER);
    }
}
