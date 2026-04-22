import java.util.*;

public interface AuctionMediator {
    void broadcastRequest(String itemWanted);
    void submitBid(Bid bid);
    void announceWinner(String winnerName);
    void sendMessage(String from, String msg);
}

class AuctionController implements AuctionMediator {
    private BuyerPanel buyer;
    private List<SellerPanel> sellers = new ArrayList<>();
    private boolean isTenderOpen = true;

    public void registerBuyer(BuyerPanel buyer) {
        this.buyer = buyer;
    }

    public void addSeller(SellerPanel seller) {
        this.sellers.add(seller);
    }

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
        if (isTenderOpen)
            buyer.receiveBid(bid);
    }

    @Override
    public void announceWinner(String winnerName) {
        isTenderOpen = false;
        for (SellerPanel seller : sellers) {
            if (seller.getShopName().equals(winnerName))
                seller.onWin();
            else
                seller.onLose(winnerName);
        }
        buyer.onDealClosed(winnerName);
    }

    @Override
    public void sendMessage(String from, String msg) {
        buyer.receiveChatMessage(from, msg);
        for (SellerPanel seller : sellers) {
            seller.receiveChatMessage(from, msg);
        }
    }
}
