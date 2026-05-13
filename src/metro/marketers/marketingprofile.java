package metro.marketers;

import java.util.List;
import java.util.ArrayList;

public class marketingprofile {
    private String segment;
    private List<String> purchaseHistory;
    private String preferredChannel;
    private double loyaltyScore;

    public marketingprofile(String segment, String preferredChannel, double loyaltyScore) {
        this.segment          = segment;
        this.purchaseHistory  = new ArrayList<>();
        this.preferredChannel = preferredChannel;
        this.loyaltyScore     = loyaltyScore;
    }

    // Getters
    public String getSegment()              { return segment; }
    public String getPreferredChannel()     { return preferredChannel; }
    public double getLoyaltyScore()         { return loyaltyScore; }
    public List<String> getPurchaseHistory(){ return purchaseHistory; }

    // Setters
    public void setSegment(String segment)                  { this.segment = segment; }
    public void setPreferredChannel(String preferredChannel){ this.preferredChannel = preferredChannel; }
    public void setLoyaltyScore(double loyaltyScore)        { this.loyaltyScore = loyaltyScore; }

    // Tambah riwayat pembelian
    public void addPurchaseHistory(String item) {
        purchaseHistory.add(item);
    }

    // Hitung loyalty score dari history
    public String loyaltyScore(int item) {
        if (loyaltyScore >= 80) return "Platinum";
        else if (loyaltyScore >= 60) return "Gold";
        else if (loyaltyScore >= 40) return "Silver";
        else return "Bronze";
    }

    // Tampilkan info profile
    public void displayProfile() {
        System.out.println("\n  --- Marketing Profile ---");
        System.out.println("  Segment         : " + segment);
        System.out.println("  Preferred Channel: " + preferredChannel);
        System.out.printf ("  Loyalty Score   : %.1f (%s)%n", loyaltyScore, loyaltyScore(0));
        System.out.println("  Purchase History:");
        if (purchaseHistory.isEmpty()) {
            System.out.println("    (no history)");
        } else {
            for (int i = 0; i < purchaseHistory.size(); i++) {
                System.out.println("    " + (i + 1) + ". " + purchaseHistory.get(i));
            }
        }
    }

    @Override
    public String toString() {
        return String.format("MarketingProfile[segment=%s, channel=%s, loyalty=%.1f]",
                segment, preferredChannel, loyaltyScore);
    }
}