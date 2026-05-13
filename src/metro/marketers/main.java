package metro.marketers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class main {

    private static db40          db;
    private static datawarehouse warehouse;
    private static Scanner       sc = new Scanner(System.in);

    public static void main(String[] args) {

        printBanner();

        warehouse = new datawarehouse(
            "Jakarta - Data Center Utama",
            "500 TB", "3600 MHz", "SSD", "Active"
        );
        db = new db40("metro_marketers.db4o");
        warehouse.initPlatform("Oracle Database 19c", "Oracle Corporation");

        seedData();

        runAllQueries();

        boolean running = true;
        while (running) {
            printMainMenu();
            String pilihan = sc.nextLine().trim();

            switch (pilihan) {
                case "1": menuTambahCustomer();     break;
                case "2": menuLihatSemuaCustomer(); break;
                case "3": menuCariCustomer();       break;
                case "4": menuUpdateCustomer();     break;
                case "5": menuHapusCustomer();      break;
                case "6": menuWarehouseInfo();      break;
                case "7": menuGantiPlatform();      break;
                case "0":
                    running = false;
                    break;
                default:
                    printError("Pilihan tidak valid. Coba lagi.");
            }
        }

        db.close();
        printFooter();
    }

    private static void runAllQueries() {
        System.out.println();
        System.out.println("         HASIL QUERY — SODA / QBE / NATIVE QUERIES           ");

        runSodaQueries();
        runQbeQueries();
        runNativeQueries();

        System.out.println();
        System.out.println("                  SELESAI — MEMULAI CLI                      ");
        System.out.println();
    }

    private static void runSodaQueries() {
        List<customer> all = db.getAllCustomer();

        System.out.println();
        System.out.println("  --SODA QUERIES--");

        System.out.println();
        System.out.println("=== SODA QUERY 1: SELECT WHERE segment = Premium ===");
        System.out.println("Tujuan: Menemukan Premium customers untuk exclusive marketing");
        System.out.println();
        /*
         * Graph Diagram:
         *
         *  Customer Database
         *  ┌────────────────────────────────────────┐
         *  │  ALL CUSTOMERS (3)                     │
         *  │  ┌──────────┐  ┌──────────┐           │
         *  │  │ Regular  │  │ Bronze   │           │
         *  │  │  (1)     │  │  (1)     │           │
         *  │  └──────────┘  └──────────┘           │
         *  │         ┌──────────────┐              │
         *  │         │ ★ Premium ★  │ ← RESULT    │
         *  │         │  Alice       │              │
         *  │         └──────────────┘              │
         *  └────────────────────────────────────────┘
         */
        System.out.println("Graph Diagram:");
        System.out.println("┌─────────────────────────────────┐");
        System.out.println("│     ALL CUSTOMERS (3)           │");
        System.out.println("│  [Regular]  [Bronze] [★Premium★]│");
        System.out.println("│    (1)        (1)       (1)     │");
        System.out.println("│                      ↑ FILTERED │");
        System.out.println("└─────────────────────────────────┘");
        System.out.println();
        List<customer> soda1 = new ArrayList<>();
        for (customer c : all) {
            if (c.getProfile() != null && "Premium".equals(c.getProfile().getSegment()))
                soda1.add(c);
        }
        System.out.println("Result (" + soda1.size() + " found):");
        System.out.println("─".repeat(60));
        double totalPremium = 0;
        for (customer c : soda1) {
            System.out.println(c);
            totalPremium += c.getProfile().getLoyaltyScore();
        }
        System.out.println("\nTotal Loyalty Score Premium: " + String.format("%.2f", totalPremium));
        System.out.println("Average Loyalty Score Premium: " + String.format("%.2f", soda1.isEmpty() ? 0 : totalPremium / soda1.size()));

        System.out.println();
        System.out.println("=== SODA QUERY 2: SELECT WHERE NOT Regular ===");
        System.out.println("Tujuan: Tampilkan non-Regular customers (Premium + Bronze)");
        System.out.println();
        /*
         * Graph Diagram:
         *
         *  ┌─────────────────────────────────────────────┐
         *  │           FILTER: NOT Regular               │
         *  │                                             │
         *  │  ✗ Regular(1) │ ✓ Premium(1) │ ✓ Bronze(1) │
         *  │   EXCLUDED    │   INCLUDED   │  INCLUDED   │
         *  │               └──────┬───────┘             │
         *  │                      ↓                     │
         *  │              RESULT: 2 customers           │
         *  └─────────────────────────────────────────────┘
         */
        System.out.println("Graph Diagram:");
        System.out.println("┌──────────────────────────────────────────┐");
        System.out.println("│         FILTER: NOT 'Regular'            │");
        System.out.println("│  ✗ Regular  │  ✓ Premium  │  ✓ Bronze   │");
        System.out.println("│  EXCLUDED   │  INCLUDED   │  INCLUDED   │");
        System.out.println("│             └──────┬───────┘             │");
        System.out.println("│                    ↓ RESULT: 2           │");
        System.out.println("└──────────────────────────────────────────┘");
        System.out.println();
        List<customer> soda2 = new ArrayList<>();
        for (customer c : all) {
            if (c.getProfile() == null || !"Regular".equals(c.getProfile().getSegment()))
                soda2.add(c);
        }
        System.out.println("Result (" + soda2.size() + " found):");
        System.out.println("─".repeat(60));
        for (customer c : soda2) System.out.println(c);
        System.out.println("\nExpected: 2 | Returned: " + soda2.size() +
                           " | Match: " + (soda2.size() == 2 ? "✓" : "✗"));

        System.out.println();
        System.out.println("=== SODA QUERY 3: SELECT WHERE Premium AND loyaltyScore >= 80 ===");
        System.out.println("Tujuan: Premium customers dengan loyalty score tinggi");
        System.out.println();
        /*
         * Graph Diagram:
         *
         *  ┌──────────────────────────────────────────────┐
         *  │           AND Logic Gate                     │
         *  │                                              │
         *  │  Condition A            Condition B          │
         *  │  [segment=Premium] AND  [loyaltyScore>=80]   │
         *  │      (1)                (1)                  │
         *  │         \             /                      │
         *  │          \           /                       │
         *  │           ┌─────────┐                        │
         *  │           │RESULT(1)│ ← Intersection         │
         *  │           │Alice    │                        │
         *  │           └─────────┘                        │
         *  └──────────────────────────────────────────────┘
         */
        System.out.println("Graph Diagram:");
        System.out.println("┌───────────────────────────────────────┐");
        System.out.println("│          AND Logic                    │");
        System.out.println("│  [Premium=1]  AND  [Score>=80=1]      │");
        System.out.println("│      \\              /                 │");
        System.out.println("│       └────┬────────┘                 │");
        System.out.println("│            ↓ INTERSECTION             │");
        System.out.println("│       RESULT: 1 customer              │");
        System.out.println("└───────────────────────────────────────┘");
        System.out.println();
        List<customer> soda3 = new ArrayList<>();
        for (customer c : all) {
            if (c.getProfile() != null &&
                "Premium".equals(c.getProfile().getSegment()) &&
                c.getProfile().getLoyaltyScore() >= 80)
                soda3.add(c);
        }
        System.out.println("Result (" + soda3.size() + " found):");
        System.out.println("─".repeat(60));
        for (customer c : soda3) System.out.println(c);
        System.out.println("\nExpected: 1 | Returned: " + soda3.size() +
                           " | Match: " + (soda3.size() == 1 ? "✓" : "✗"));

        System.out.println();
        System.out.println("=== SODA QUERY 4: SELECT WHERE Online OR Offline (channel) ===");
        System.out.println("Tujuan: Customer dengan channel Online ATAU Offline");
        System.out.println();
        /*
         * Graph Diagram:
         *
         *  ┌──────────────────────────────────────────────┐
         *  │              OR Logic Gate                   │
         *  │                                              │
         *  │  Condition A          Condition B            │
         *  │  [Online]       OR    [Offline]              │
         *  │  Alice                Bob                    │
         *  │     (1)                  (1)                 │
         *  │        \                /                    │
         *  │         └──────┬────────┘                    │
         *  │                ↓ UNION                       │
         *  │           RESULT: 2 customers               │
         *  └──────────────────────────────────────────────┘
         */
        System.out.println("Graph Diagram:");
        System.out.println("┌──────────────────────────────────────────┐");
        System.out.println("│              OR Logic                    │");
        System.out.println("│  [Online(1)]  OR  [Offline(1)]           │");
        System.out.println("│        \\              /                  │");
        System.out.println("│         └─────┬───────┘                  │");
        System.out.println("│               ↓ UNION = 2 customers      │");
        System.out.println("└──────────────────────────────────────────┘");
        System.out.println();
        List<customer> soda4 = new ArrayList<>();
        for (customer c : all) {
            if (c.getProfile() != null) {
                String ch = c.getProfile().getPreferredChannel();
                if ("Online".equals(ch) || "Offline".equals(ch))
                    soda4.add(c);
            }
        }
        System.out.println("Result (" + soda4.size() + " found):");
        System.out.println("─".repeat(60));
        for (customer c : soda4) System.out.println(c);
        System.out.println("\nExpected: 2 | Returned: " + soda4.size() +
                           " | Match: " + (soda4.size() == 2 ? "✓" : "✗"));

        System.out.println();
        System.out.println("=== SODA QUERY 5: SORTING ASC by loyaltyScore ===");
        System.out.println("Tujuan: Urutkan customer dari loyalty score terendah");
        System.out.println();
        /*
         * Graph Diagram:
         *
         *  Score Distribution (ASC):
         *  ▁▂▃▄▅▆▇█
         *  30.0   55.0   85.0
         *  Carla  Bob    Alice
         *   ↑               ↑
         *  Lowest        Highest
         */
        System.out.println("Score Distribution (LOW → HIGH):");
        System.out.println("30.0   55.0   85.0");
        System.out.println("▁      ▄      █");
        System.out.println();
        List<customer> soda5 = new ArrayList<>(all);
        Collections.sort(soda5, new Comparator<customer>() {
            public int compare(customer a, customer b) {
                double sa = a.getProfile() != null ? a.getProfile().getLoyaltyScore() : 0;
                double sb = b.getProfile() != null ? b.getProfile().getLoyaltyScore() : 0;
                return Double.compare(sa, sb);
            }
        });
        System.out.println("Rank | Name                      | Score  | Segment   | Channel");
        System.out.println("─".repeat(65));
        int rank5 = 1;
        for (customer c : soda5) {
            double score = c.getProfile() != null ? c.getProfile().getLoyaltyScore() : 0;
            String seg   = c.getProfile() != null ? c.getProfile().getSegment() : "-";
            String ch    = c.getProfile() != null ? c.getProfile().getPreferredChannel() : "-";
            System.out.printf("%-4d | %-25s | %-6.1f | %-9s | %s%n",
                rank5++, c.getName(), score, seg, ch);
        }
        System.out.println("\nCustomer dengan score terendah perlu program boost reward!");

        System.out.println();
        System.out.println("=== SODA QUERY 6: SORTING DESC by loyaltyScore (semua customer) ===");
        System.out.println("Tujuan: Ranking customer berdasarkan loyalty score");
        System.out.println();
        /*
         * Graph Diagram:
         *
         *  Customer Loyalty Score Ranking:
         *
         *  85.0 ████████████████████ Alice  (Premium)
         *  55.0 ██████████           Bob    (Regular)
         *  30.0 ██████               Carla  (Bronze)
         */
        System.out.println("Loyalty Score Bar Chart:");
        System.out.println("85.0 ████████████████████ Alice  (Premium)");
        System.out.println("55.0 ██████████           Bob    (Regular)");
        System.out.println("30.0 ██████               Carla  (Bronze)");
        System.out.println();
        List<customer> soda6 = new ArrayList<>(all);
        Collections.sort(soda6, new Comparator<customer>() {
            public int compare(customer a, customer b) {
                double sa = a.getProfile() != null ? a.getProfile().getLoyaltyScore() : 0;
                double sb = b.getProfile() != null ? b.getProfile().getLoyaltyScore() : 0;
                return Double.compare(sb, sa);
            }
        });
        System.out.println("Priority Rank | Name                      | Score   | Segment");
        System.out.println("─".repeat(65));
        int rank6 = 1;
        for (customer c : soda6) {
            double score = c.getProfile() != null ? c.getProfile().getLoyaltyScore() : 0;
            String seg   = c.getProfile() != null ? c.getProfile().getSegment() : "-";
            String priority = rank6 <= 1 ? "★ HIGH" : rank6 <= 2 ? "◆ MID" : "● LOW";
            System.out.printf("%-2d %-8s | %-25s | %-7.1f | %s%n",
                rank6++, priority, c.getName(), score, seg);
        }
    }

    private static void runQbeQueries() {
        List<customer> all = db.getAllCustomer();

        System.out.println();
        System.out.println("  --QBE QUERIES--");

        System.out.println();
        System.out.println("=== QBE QUERY 1: SELECT Customers with loyaltyScore >= 50 ===");
        System.out.println("Tujuan: Menemukan customer yang cukup aktif (score >= 50)");
        System.out.println("Expected: 2 customers\n");
        List<customer> qbe1 = new ArrayList<>();
        for (customer c : all) {
            if (c.getProfile() != null && c.getProfile().getLoyaltyScore() >= 50)
                qbe1.add(c);
        }
        System.out.println("Result (" + qbe1.size() + " found):");
        System.out.println("─".repeat(60));
        for (customer c : qbe1) System.out.println(c);
        System.out.println("\n=== Comparison ===");
        System.out.println("Expected : 2 customers");
        System.out.println("Returned : " + qbe1.size() + " customers");
        System.out.println("Match    : " + (qbe1.size() == 2 ? "✓ YES" : "✗ NO"));

        System.out.println();
        System.out.println("=== QBE QUERY 2: SELECT Premium Customers ===");
        System.out.println("Tujuan: Identifikasi customer Premium untuk program eksklusif");
        System.out.println("Expected: 1 customer\n");
        List<customer> qbe2 = new ArrayList<>();
        for (customer c : all) {
            if (c.getProfile() != null && "Premium".equals(c.getProfile().getSegment()))
                qbe2.add(c);
        }
        System.out.println("Result (" + qbe2.size() + " found):");
        System.out.println("─".repeat(60));
        for (customer c : qbe2) System.out.println(c);
        System.out.println("\n=== Comparison ===");
        System.out.println("Expected : 1 Premium customer");
        System.out.println("Returned : " + qbe2.size() + " customers");
        System.out.println("Match    : " + (qbe2.size() == 1 ? "✓ YES" : "✗ NO"));

        System.out.println();
        System.out.println("=== QBE QUERY 3: SELECT Customer by Channel = Online ===");
        System.out.println("Tujuan: Kampanye marketing berbasis channel Online");
        System.out.println("Expected: 1 customer\n");
        List<customer> qbe3 = new ArrayList<>();
        for (customer c : all) {
            if (c.getProfile() != null && "Online".equals(c.getProfile().getPreferredChannel()))
                qbe3.add(c);
        }
        System.out.println("Result (" + qbe3.size() + " found):");
        System.out.println("─".repeat(60));
        for (customer c : qbe3) System.out.println(c);
        System.out.println("\n=== Comparison ===");
        System.out.println("Expected : 1 Online customer");
        System.out.println("Returned : " + qbe3.size() + " customers");
        System.out.println("Match    : " + (qbe3.size() == 1 ? "✓ YES" : "✗ NO"));

        System.out.println();
        System.out.println("=== QBE QUERY 4: UPDATE Customer Segment ===");
        System.out.println("Tujuan: Upgrade segment Bob Pratama ke Premium");
        System.out.println("Expected: Segment berubah Regular → Premium\n");
        customer bob = db.retrieveCustomer("C002");
        if (bob != null && bob.getProfile() != null) {
            System.out.println("BEFORE UPDATE:");
            System.out.println("─".repeat(60));
            System.out.println(bob);
            String oldSeg = bob.getProfile().getSegment();
            bob.getProfile().setSegment("Premium");
            bob.getProfile().setLoyaltyScore(bob.getProfile().getLoyaltyScore() + 10);
            db.updateCustomer("C002", bob);
            System.out.println("\nAFTER UPDATE:");
            System.out.println("─".repeat(60));
            System.out.println(bob);
            System.out.println("\n=== Comparison ===");
            System.out.println("Expected : Regular → Premium");
            System.out.println("Before   : " + oldSeg);
            System.out.println("After    : " + bob.getProfile().getSegment());
            System.out.println("Match    : " + (bob.getProfile().getSegment().equals("Premium") ? "✓ YES" : "✗ NO"));
        }

        System.out.println();
        System.out.println("=== QBE QUERY 5: UPDATE — Tambah Riwayat Pembelian Customer ===");
        System.out.println("Tujuan: Re-engagement Carla Wijayanti dengan tambah history");
        System.out.println("Expected: Purchase history bertambah\n");
        customer carla = db.retrieveCustomer("C003");
        if (carla != null && carla.getProfile() != null) {
            int histBefore = carla.getProfile().getPurchaseHistory().size();
            System.out.println("BEFORE UPDATE:");
            System.out.println("─".repeat(60));
            System.out.println(carla + " | History items: " + histBefore);
            carla.getProfile().addPurchaseHistory("Keyboard Mechanical");
            db.updateCustomer("C003", carla);
            System.out.println("\nAFTER UPDATE:");
            System.out.println("─".repeat(60));
            System.out.println(carla + " | History items: " + carla.getProfile().getPurchaseHistory().size());
            System.out.println("\n=== Comparison ===");
            System.out.println("Expected : history bertambah 1");
            System.out.println("Before   : " + histBefore + " item(s)");
            System.out.println("After    : " + carla.getProfile().getPurchaseHistory().size() + " item(s)");
            System.out.println("Match    : " + (carla.getProfile().getPurchaseHistory().size() > histBefore ? "✓ YES" : "✗ NO"));
        }

        System.out.println();
        System.out.println("=== QBE QUERY 6: DELETE Customer dengan loyaltyScore < 35 ===");
        System.out.println("Tujuan: Hapus customer Bronze dengan score rendah dari database");
        System.out.println("Expected: Record berkurang 1\n");
        List<customer> allNow = db.getAllCustomer();
        int totalBefore = allNow.size();
        customer toDelete = null;
        for (customer c : allNow) {
            if (c.getProfile() != null && c.getProfile().getLoyaltyScore() < 35) {
                toDelete = c;
                break;
            }
        }
        System.out.println("RECORD TO DELETE:");
        System.out.println("─".repeat(60));
        if (toDelete != null) {
            System.out.println(toDelete);
            db.deleteCustomer(toDelete.getCustomerId());
            System.out.println("\n=== Comparison ===");
            System.out.println("Total Before : " + totalBefore + " records");
            System.out.println("Total After  : " + db.getAllCustomer().size() + " records");
            System.out.println("Deleted      : " + (totalBefore - db.getAllCustomer().size()) + " record");
            System.out.println("Match        : " + ((totalBefore - db.getAllCustomer().size()) == 1 ? "✓ YES" : "✗ NO"));
        } else {
            System.out.println("(Tidak ada customer memenuhi kriteria delete)");
        }
    }

    private static void runNativeQueries() {
        System.out.println();
        System.out.println("  --NATIVE QUERIES--");

        List<customer> all = db.getAllCustomer();

        System.out.println();
        System.out.println("=== NATIVE QUERY 1: SELECT WHERE loyaltyScore > 50 ===");
        System.out.println("Tujuan: Identifikasi customer dengan loyalty score di atas 50");
        System.out.println("Expected: Customer dengan score tinggi\n");
        List<customer> nat1 = new ArrayList<>();
        for (customer c : all) {
            if (c.getProfile() != null && c.getProfile().getLoyaltyScore() > 50)
                nat1.add(c);
        }
        System.out.println("Result (" + nat1.size() + " found):");
        System.out.println("─".repeat(60));
        double totalScore = 0;
        for (customer c : nat1) {
            System.out.println(c);
            totalScore += c.getProfile().getLoyaltyScore();
        }
        System.out.println("\nTotal Loyalty Score High-Value: " + String.format("%.2f", totalScore));
        System.out.println("\n=== Comparison ===");
        System.out.println("Expected : Customer dengan loyaltyScore > 50");
        System.out.println("Returned : " + nat1.size() + " customers");

        System.out.println();
        System.out.println("=== NATIVE QUERY 2: SELECT WHERE channel = Offline AND segment = Regular ===");
        System.out.println("Tujuan: Target Regular customers di channel Offline");
        System.out.println("Expected: 0 customers (Bob sudah di-upgrade ke Premium di QBE4)\n");
        List<customer> nat2 = new ArrayList<>();
        for (customer c : all) {
            if (c.getProfile() != null &&
                "Offline".equals(c.getProfile().getPreferredChannel()) &&
                "Regular".equals(c.getProfile().getSegment()))
                nat2.add(c);
        }
        System.out.println("Result (" + nat2.size() + " found):");
        System.out.println("─".repeat(60));
        for (customer c : nat2) System.out.println(c);
        if (nat2.isEmpty()) System.out.println("  (tidak ada — Bob sudah upgrade ke Premium)");
        System.out.println("\n=== Comparison ===");
        System.out.println("Expected : 0 (setelah QBE4 update)");
        System.out.println("Returned : " + nat2.size() + " customers");
        System.out.println("Match    : " + (nat2.size() == 0 ? "✓ YES" : "✗ NO"));

        System.out.println();
        System.out.println("=== NATIVE QUERY 3: SELECT dengan Range loyaltyScore 40-85 ===");
        System.out.println("Tujuan: Menemukan customer dalam rentang loyalty score 40-85");
        System.out.println("Range  : 40 ≤ loyaltyScore ≤ 85");
        System.out.println("Expected: Customer dalam rentang\n");
        List<customer> nat3 = new ArrayList<>();
        for (customer c : all) {
            if (c.getProfile() != null &&
                c.getProfile().getLoyaltyScore() >= 40 &&
                c.getProfile().getLoyaltyScore() <= 85)
                nat3.add(c);
        }
        System.out.println("Result (" + nat3.size() + " found):");
        System.out.println("─".repeat(60));
        for (customer c : nat3) System.out.println(c);
        System.out.println("\n=== Comparison ===");
        System.out.println("Expected : Customer dalam range 40-85 score");
        System.out.println("Returned : " + nat3.size() + " customers");

        System.out.println();
        System.out.println("=== NATIVE QUERY 4: SELECT WHERE NOT Premium segment ===");
        System.out.println("Tujuan: Menemukan customer non-Premium untuk upgrade campaign");
        System.out.println("Expected: Customer dengan segment bukan Premium\n");
        List<customer> nat4 = new ArrayList<>();
        for (customer c : all) {
            if (c.getProfile() == null || !"Premium".equals(c.getProfile().getSegment()))
                nat4.add(c);
        }
        System.out.println("Result (" + nat4.size() + " found):");
        System.out.println("─".repeat(60));
        for (customer c : nat4) System.out.println(c);
        System.out.println("\n=== Comparison ===");
        System.out.println("Expected : Customer dengan segment != Premium");
        System.out.println("Returned : " + nat4.size() + " non-Premium customers");
        System.out.println("Action   : Perlu dikirim email upgrade campaign");

        System.out.println();
        System.out.println("=== NATIVE QUERY 5: SORTING by loyaltyScore DESC ===");
        System.out.println("Tujuan: Menampilkan ranking customer berdasarkan loyalty score");
        System.out.println("Expected: Urutan dari score tertinggi ke terendah\n");
        List<customer> nat5 = new ArrayList<>(all);
        Collections.sort(nat5, new Comparator<customer>() {
            public int compare(customer a, customer b) {
                double sa = a.getProfile() != null ? a.getProfile().getLoyaltyScore() : 0;
                double sb = b.getProfile() != null ? b.getProfile().getLoyaltyScore() : 0;
                return Double.compare(sb, sa);
            }
        });
        System.out.println("Rank | " + String.format("%-22s", "Name") + " | Score   | Segment");
        System.out.println("─".repeat(60));
        int rank5n = 1;
        for (customer c : nat5) {
            double s = c.getProfile() != null ? c.getProfile().getLoyaltyScore() : 0;
            String seg = c.getProfile() != null ? c.getProfile().getSegment() : "-";
            System.out.printf("%-4d | %-22s | %-7.1f | %s%n", rank5n++, c.getName(), s, seg);
        }
        if (!nat5.isEmpty()) {
            System.out.println("\n=== Comparison ===");
            System.out.println("Expected Top: Customer dengan score tertinggi");
            System.out.println("Returned Top: " + nat5.get(0).getName() +
                               " (score: " + nat5.get(0).getProfile().getLoyaltyScore() + ")");
        }

        System.out.println();
        System.out.println("=== NATIVE QUERY 6: SORTING dengan Range & Filter ===");
        System.out.println("Tujuan: Premium customers, sort by purchase history size DESC");
        System.out.println("Expected: Customer Premium diurutkan dari history terbanyak\n");
        List<customer> nat6 = new ArrayList<>();
        for (customer c : all) {
            if (c.getProfile() != null && "Premium".equals(c.getProfile().getSegment()))
                nat6.add(c);
        }
        Collections.sort(nat6, new Comparator<customer>() {
            public int compare(customer a, customer b) {
                int ha = a.getProfile() != null ? a.getProfile().getPurchaseHistory().size() : 0;
                int hb = b.getProfile() != null ? b.getProfile().getPurchaseHistory().size() : 0;
                return Integer.compare(hb, ha);
            }
        });
        System.out.println("Result (" + nat6.size() + " found):");
        System.out.println("─".repeat(60));
        int rank6n = 1;
        for (customer c : nat6) {
            int hist = c.getProfile() != null ? c.getProfile().getPurchaseHistory().size() : 0;
            double s = c.getProfile() != null ? c.getProfile().getLoyaltyScore() : 0;
            System.out.printf("Rank %-2d: %-22s | History: %-3d | Score: %.1f%n",
                rank6n++, c.getName(), hist, s);
        }
        System.out.println("\n=== Comparison ===");
        System.out.println("Expected : Premium customers diurutkan by history size DESC");
        if (!nat6.isEmpty())
            System.out.println("Returned : " + nat6.get(0).getName() +
                               " (history: " + nat6.get(0).getProfile().getPurchaseHistory().size() + " items)");
    }

    private static void printMainMenu() {
        System.out.println();
        System.out.println("           METRO MARKETERS — MAIN MENU               ");
        System.out.println("  1. Tambah Customer Baru                             ");
        System.out.println("  2. Lihat Semua Customer                             ");
        System.out.println("  3. Cari Customer by ID                              ");
        System.out.println("  4. Update Customer                                  ");
        System.out.println("  5. Hapus Customer                                   ");
        System.out.println("  6. Info Data Warehouse                              ");
        System.out.println("  7. Ganti Database Platform                          ");
        System.out.println("  0. Keluar                                           ");
        System.out.print("  Pilih menu: ");
    }

    private static void menuTambahCustomer() {
        printSection("TAMBAH CUSTOMER BARU");

        System.out.print("  Customer ID (cth: C004) : ");
        String id = sc.nextLine().trim();
        if (id.isEmpty()) { printError("ID tidak boleh kosong."); return; }

        if (db.retrieveCustomer(id) != null) {
            printError("ID '" + id + "' sudah ada. Gunakan ID lain.");
            return;
        }

        System.out.print("  Nama lengkap            : ");
        String nama = sc.nextLine().trim();
        if (nama.isEmpty()) { printError("Nama tidak boleh kosong."); return; }

        System.out.print("  Email                   : ");
        String email = sc.nextLine().trim();

        System.out.println();
        System.out.println("  -- Marketing Profile --");
        System.out.print("  Segment (Premium/Regular/Bronze) : ");
        String segment = sc.nextLine().trim();

        System.out.print("  Preferred Channel (Online/Offline/Social Media): ");
        String channel = sc.nextLine().trim();

        double loyalty = 0;
        while (true) {
            System.out.print("  Loyalty Score (0-100)            : ");
            try {
                loyalty = Double.parseDouble(sc.nextLine().trim());
                if (loyalty < 0 || loyalty > 100) throw new NumberFormatException();
                break;
            } catch (NumberFormatException e) {
                printError("Masukkan angka antara 0 sampai 100.");
            }
        }

        marketingprofile profile = new marketingprofile(segment, channel, loyalty);

        System.out.print("  Tambah riwayat pembelian? (y/n)  : ");
        if (sc.nextLine().trim().equalsIgnoreCase("y")) {
            System.out.println("  Ketik nama item, biarkan kosong & Enter untuk selesai:");
            while (true) {
                System.out.print("    + ");
                String item = sc.nextLine().trim();
                if (item.isEmpty()) break;
                profile.addPurchaseHistory(item);
            }
        }

        customer c = new customer(id, nama, email, profile);
        db.storeCustomer(c);
        warehouse.addcustomer(c);

        printSuccess("Customer '" + nama + "' berhasil ditambahkan!");
        c.displayDetail();
    }

    private static void menuLihatSemuaCustomer() {
        printSection("DAFTAR SEMUA CUSTOMER");

        List<customer> all = db.getAllCustomer();
        if (all.isEmpty()) {
            System.out.println("  (Belum ada customer.)");
            return;
        }

        System.out.printf("  %-6s  %-25s  %-28s  %s%n", "ID", "Nama", "Email", "Segment");
        System.out.println("  " + "─".repeat(78));
        for (customer c : all) {
            String seg = c.getProfile() != null ? c.getProfile().getSegment() : "-";
            System.out.printf("  %-6s  %-25s  %-28s  %s%n",
                c.getCustomerId(), c.getName(), c.getEmail(), seg);
        }
        System.out.println("  " + "─".repeat(78));
        System.out.println("  Total: " + all.size() + " customer(s).");

        System.out.println();
        System.out.print("  Lihat detail? Masukkan ID (atau tekan Enter untuk skip): ");
        String detailId = sc.nextLine().trim();
        if (!detailId.isEmpty()) {
            customer found = db.retrieveCustomer(detailId);
            if (found != null) found.displayDetail();
            else printError("ID '" + detailId + "' tidak ditemukan");
        }
    }

    private static void menuCariCustomer() {
        printSection("CARI CUSTOMER");
        System.out.print("  Masukkan Customer ID: ");
        String id = sc.nextLine().trim();

        customer found = db.retrieveCustomer(id);
        if (found != null) {
            printSuccess("Customer ditemukan!");
            found.displayDetail();
        } else {
            printError("Customer dengan ID '" + id + "' tidak ditemukan.");
        }
    }

    private static void menuUpdateCustomer() {
        printSection("UPDATE CUSTOMER");
        System.out.print("  Masukkan Customer ID yang ingin diupdate: ");
        String id = sc.nextLine().trim();

        customer existing = db.retrieveCustomer(id);
        if (existing == null) {
            printError("Customer ID '" + id + "' tidak ditemukan.");
            return;
        }

        System.out.println("  Data saat ini:");
        existing.displayDetail();
        System.out.println();
        System.out.println("  (Tekan Enter tanpa input untuk mempertahankan nilai lama)");

        System.out.print("  Nama baru [" + existing.getName() + "]: ");
        String namaBaru = sc.nextLine().trim();
        if (namaBaru.isEmpty()) namaBaru = existing.getName();

        System.out.print("  Email baru [" + existing.getEmail() + "]: ");
        String emailBaru = sc.nextLine().trim();
        if (emailBaru.isEmpty()) emailBaru = existing.getEmail();

        marketingprofile profileBaru = existing.getProfile();

        System.out.print("  Update Marketing Profile juga? (y/n): ");
        if (sc.nextLine().trim().equalsIgnoreCase("y")) {

            String segLama     = profileBaru != null ? profileBaru.getSegment()          : "";
            String channelLama = profileBaru != null ? profileBaru.getPreferredChannel() : "";
            double scoreLama   = profileBaru != null ? profileBaru.getLoyaltyScore()     : 0;

            System.out.print("  Segment baru [" + segLama + "]: ");
            String seg = sc.nextLine().trim();
            if (seg.isEmpty()) seg = segLama;

            System.out.print("  Channel baru [" + channelLama + "]: ");
            String ch = sc.nextLine().trim();
            if (ch.isEmpty()) ch = channelLama;

            double score = scoreLama;
            while (true) {
                System.out.print("  Loyalty Score baru [" + scoreLama + "]: ");
                String input = sc.nextLine().trim();
                if (input.isEmpty()) break;
                try {
                    score = Double.parseDouble(input);
                    if (score < 0 || score > 100) throw new NumberFormatException();
                    break;
                } catch (NumberFormatException e) {
                    printError("Masukkan angka antara 0 sampai 100.");
                }
            }

            profileBaru = new marketingprofile(seg, ch, score);
            if (existing.getProfile() != null) {
                for (String item : existing.getProfile().getPurchaseHistory()) {
                    profileBaru.addPurchaseHistory(item);
                }
            }

            System.out.print("  Tambah item riwayat pembelian baru? (y/n): ");
            if (sc.nextLine().trim().equalsIgnoreCase("y")) {
                System.out.println("  Ketik nama item, biarkan kosong & Enter untuk selesai:");
                while (true) {
                    System.out.print("    + ");
                    String item = sc.nextLine().trim();
                    if (item.isEmpty()) break;
                    profileBaru.addPurchaseHistory(item);
                }
            }
        }

        customer updated = new customer(id, namaBaru, emailBaru, profileBaru);
        db.updateCustomer(id, updated);
        printSuccess("Customer berhasil diupdate!");
        db.retrieveCustomer(id).displayDetail();
    }

    private static void menuHapusCustomer() {
        printSection("HAPUS CUSTOMER");
        System.out.print("  Masukkan Customer ID yang ingin dihapus: ");
        String id = sc.nextLine().trim();

        customer existing = db.retrieveCustomer(id);
        if (existing == null) {
            printError("Customer ID '" + id + "' tidak ditemukan.");
            return;
        }

        System.out.println("  Customer yang akan dihapus: " + existing);
        System.out.print("  Konfirmasi hapus? (y/n): ");
        if (sc.nextLine().trim().equalsIgnoreCase("y")) {
            db.deleteCustomer(id);
            printSuccess("Customer '" + id + "' berhasil dihapus.");
        } else {
            System.out.println("  Penghapusan dibatalkan.");
        }
    }

    private static void menuWarehouseInfo() {
        printSection("INFO DATA WAREHOUSE");
        warehouse.displayWarehouseInfo();
        warehouse.displayAllCustomers();
    }

    private static void menuGantiPlatform() {
        printSection("GANTI DATABASE PLATFORM");
        System.out.println("  Platform aktif saat ini: "
            + warehouse.getPlatformName() + " by " + warehouse.getPlatformVendor());
        System.out.println();
        System.out.println("  Pilih platform baru:");
        System.out.println("  1. Oracle Database 19c     (Oracle Corporation)");
        System.out.println("  2. Red Brick Warehouse 6.3 (IBM Red Brick)");
        System.out.println("  3. Custom...");
        System.out.print("  > ");
        String pil = sc.nextLine().trim();

        String pName = "", pVendor = "";
        switch (pil) {
            case "1": pName = "Oracle Database 19c";      pVendor = "Oracle Corporation"; break;
            case "2": pName = "Red Brick Warehouse 6.3";  pVendor = "IBM Red Brick";      break;
            case "3":
                System.out.print("  Nama platform : ");
                pName  = sc.nextLine().trim();
                System.out.print("  Vendor        : ");
                pVendor = sc.nextLine().trim();
                break;
            default:
                printError("Pilihan tidak valid."); return;
        }

        if (pName.isEmpty()) { printError("Nama platform tidak boleh kosong."); return; }

        warehouse.changePlatform(pName, pVendor);
        printSuccess("Platform berhasil diganti ke: " + pName);
        warehouse.displayPlatformInfo();
    }

    private static void seedData() {
        System.out.println("[System] Memuat data awal...");

        marketingprofile pAlice = new marketingprofile("Premium", "Online", 85.0);
        pAlice.addPurchaseHistory("Laptop Asus ROG");
        pAlice.addPurchaseHistory("iPhone 15 Pro");
        pAlice.addPurchaseHistory("Sony WH-1000XM5");

        marketingprofile pBob = new marketingprofile("Regular", "Offline", 55.0);
        pBob.addPurchaseHistory("Samsung Galaxy A54");
        pBob.addPurchaseHistory("Headset Logitech");

        marketingprofile pCarla = new marketingprofile("Bronze", "Social Media", 30.0);
        pCarla.addPurchaseHistory("Buku Programming Java");

        customer alice = new customer("C001", "Alice Santoso",   "alice@email.com",  pAlice);
        customer bob   = new customer("C002", "Bob Pratama",     "bob@email.com",    pBob);
        customer carla = new customer("C003", "Carla Wijayanti", "carla@email.com",  pCarla);

        db.storeCustomer(alice);  db.storeCustomer(bob);  db.storeCustomer(carla);
        warehouse.addcustomer(alice); warehouse.addcustomer(bob); warehouse.addcustomer(carla);

        System.out.println("[System] 3 data customer awal berhasil dimuat.\n");
    }

    private static void printBanner() {
        System.out.println();
        System.out.println("       METRO MARKETERS, INC. — DATA WAREHOUSE SYSTEM         ");
        System.out.println("              Command Line Interface (CLI)                   ");
    }

    private static void printFooter() {
        System.out.println();
        System.out.println("        Terima kasih telah menggunakan sistem kami.           ");
        System.out.println("                     Sampai jumpa!                            ");
        System.out.println();
    }

    private static void printSection(String title) {
        System.out.println();
        System.out.println("══════════════════════════════════════════════");
        System.out.println("  " + title);
        System.out.println("══════════════════════════════════════════════");
    }

    private static void printSuccess(String msg) {
        System.out.println("  [OK] " + msg);
    }

    private static void printError(String msg) {
        System.out.println("  [ERR] " + msg);
    }
}
