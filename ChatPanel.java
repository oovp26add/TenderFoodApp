import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ChatPanel extends JPanel {
    private JTextArea chatArea;
    private JTextField inputField;
    private AuctionMediator mediator;
    private String senderName;

    public ChatPanel(AuctionMediator mediator, String senderName) {
        this.mediator = mediator;
        this.senderName = senderName;

        setLayout(new BorderLayout(5, 5));
        setOpaque(false);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setBackground(new Color(245, 255, 240));

        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Live Chat"));

        inputField = new JTextField();
        JButton sendBtn = new JButton("Send");
        sendBtn.setBackground(new Color(150, 180, 130));

        JPanel bottomRow = new JPanel(new BorderLayout(5, 0));
        bottomRow.setOpaque(false);
        bottomRow.add(inputField, BorderLayout.CENTER);
        bottomRow.add(sendBtn, BorderLayout.EAST);

        add(scrollPane, BorderLayout.CENTER);
        add(bottomRow, BorderLayout.SOUTH);

        sendBtn.addActionListener(e -> sendMsg());
        inputField.addActionListener(e -> sendMsg());
    }

    private void sendMsg() {
        String text = inputField.getText().trim();
        if (!text.isEmpty()) {
            mediator.sendMessage(senderName, text);
            inputField.setText("");
        }
    }

    public void addMessage(String from, String msg) {
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        chatArea.append(String.format("[%s] %s: %s\n", time, from, msg));
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }
}
