public class OrderItem {
    private String name;
    private int quantity;
    private int unitPrice;
    private String extra; // e.g. "210cc"

    public OrderItem(String name, int quantity, int unitPrice, String extra) {
        this.name = name;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.extra = extra;
    }

    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public int getUnitPrice() { return unitPrice; }
    public String getExtra() { return extra; }
    public int getTotal() { return quantity * unitPrice; }

    @Override
    public String toString() {
        String s = quantity + "x" + name + " (" + unitPrice + "k)";
        if (extra != null && !extra.isEmpty()) s += " " + extra;
        return s;
    }
}
