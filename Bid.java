import java.util.*;

public class Bid implements Comparable<Bid> {
    private String sellerName;
    private String description;
    private int price;
    private List<OrderItem> items = new ArrayList<>();

    public Bid(String sellerName, String description, int price) {
        this.sellerName = sellerName;
        this.description = description;
        this.price = price;
    }

    public void addItem(OrderItem item) {
        this.items.add(item);
    }

    public String getSellerName() {
        return sellerName;
    }

    public int getPrice() {
        if (!items.isEmpty()) {
            return items.stream().mapToInt(OrderItem::getTotal).sum();
        }
        return price;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        if (items.isEmpty()) {
            return String.format("[Rp %,d]  %s  -  %s", price, sellerName, description);
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("[Rp %,d] %s: ", getPrice(), sellerName));
            for (int i = 0; i < items.size(); i++) {
                sb.append(items.get(i).toString());
                if (i < items.size() - 1) sb.append(", ");
            }
            return sb.toString();
        }
    }

    @Override
    public int compareTo(Bid other) {
        return Integer.compare(this.getPrice(), other.getPrice());
    }
}
