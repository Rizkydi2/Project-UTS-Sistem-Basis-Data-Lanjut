package metro.marketers;

import java.util.ArrayList;
import java.util.List;

public class db40 {

    private String          dbFilePath;
    private List<customer>  container;

    public db40(String dbFilePath) {
        this.dbFilePath = dbFilePath;
        this.container  = new ArrayList<>();
        System.out.println("[Db4oManager] Database dibuka: " + dbFilePath);
    }

    public String getDbFilePath() {
        return dbFilePath;
    }

    public int size() {
        return container.size();
    }

    public void storeCustomer(customer c) {
        if (c == null) {
            System.out.println("[Db4oManager] ERROR: Customer null.");
            return;
        }
        for (customer existing : container) {
            if (existing.getCustomerId().equals(c.getCustomerId())) {
                System.out.println("[Db4oManager] ID sudah ada: " + c.getCustomerId());
                return;
            }
        }
        container.add(c);
        System.out.println("[Db4oManager] Customer disimpan: " + c);
    }

    public void updateCustomer(String customerId, customer updated) {
        for (int i = 0; i < container.size(); i++) {
            customer c = container.get(i);
            if (c.getCustomerId().equals(customerId)) {
                c.setName(updated.getName());
                c.setEmail(updated.getEmail());
                if (updated.getProfile() != null) {
                    c.setProfile(updated.getProfile());
                }
                System.out.println("[Db4oManager] Customer diupdate " + customerId + ": " + container.get(i));
                return;
            }
        }
        System.out.println("[Db4oManager] Customer tidak ditemukan untuk update: " + customerId);
    }

    public customer retrieveCustomer(String customerId) {
        for (customer c : container) {
            if (c.getCustomerId().equals(customerId)) {
                return c;
            }
        }
        System.out.println("[Db4oManager] Customer berhasil dibuat: " + customerId);
        return null;
    }

    public void deleteCustomer(String customerId) {
        for (int i = 0; i < container.size(); i++) {
            if (container.get(i).getCustomerId().equals(customerId)) {
                System.out.println("[Db4oManager] Customer dihapus: " + container.get(i));
                container.remove(i);
                return;
            }
        }
        System.out.println("[Db4oManager] Customer tidak ditemukan untuk dihapus: " + customerId);
    }

    public List<customer> getAllCustomer() {
        return new ArrayList<>(container);
    }

    public void displayAll() {
        System.out.println("  (kosong)");
        for (int i = 0; i < container.size(); i++) {
            System.out.println("  " + i + ". " + container.get(i));
        }
        System.out.println("[Db4oManager] Total: " + container.size() + " record(s)");
    }

    public void close() {
        System.out.println("[Db4oManager] Database ditutup: " + dbFilePath);
    }

    @Override
    public String toString() {
        return String.format("Db4oManager[file=%s, records=%d]", dbFilePath, container.size());
    }
}