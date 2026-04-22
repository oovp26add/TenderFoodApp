import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.util.List;

public class FrequentlyAskedPanel extends JPanel {
    private JComboBox<String> freqBox;
    private JTextField requestField;

    public FrequentlyAskedPanel(JTextField requestField, Runnable onUpdate) {
        this.requestField = requestField;
        
        setLayout(new BorderLayout(5, 5));
        setOpaque(false);
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(150, 180, 130)), "Quick Selection"));

        freqBox = new JComboBox<>(new String[] { "--- Frequently Asked ---" });
        refresh();
        
        freqBox.addActionListener(e -> {
            String selected = (String) freqBox.getSelectedItem();
            if (selected != null && !selected.startsWith("---")) {
                this.requestField.setText(selected);
            }
        });

        add(new JLabel("Presets: "), BorderLayout.WEST);
        add(freqBox, BorderLayout.CENTER);
    }

    public void refresh() {
        freqBox.removeAllItems();
        freqBox.addItem("--- Frequently Asked ---");
        List<String> topRequests = DatabaseManager.getTopRequests(5);
        for (String item : topRequests) {
            freqBox.addItem(item);
        }
    }
}
