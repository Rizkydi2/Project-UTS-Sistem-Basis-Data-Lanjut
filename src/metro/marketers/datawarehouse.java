package metro.marketers;

import java.util.ArrayList;
import java.util.List;

public class datawarehouse {

    private String           location;
    private String           capacity;
    private String           processingPower;
    private String           storageType;
    private String           backupStatus;
    private databaseplatform dbPlatform;      
    private List<customer>   customers;

    public datawarehouse(String location, String capacity,
                         String processingPower, String storageType,
                         String backupStatus) {
        this.location        = location;
        this.capacity        = capacity;
        this.processingPower = processingPower;
        this.storageType     = storageType;
        this.backupStatus    = backupStatus;
        this.dbPlatform      = null;   
        this.customers       = new ArrayList<>();
    }

    public void initPlatform(String platformName, String vendor) {
        this.dbPlatform = new databaseplatform(platformName, vendor);
        System.out.println("[DataWarehouse] Platform diinisialisasi: " + dbPlatform);
    }

    public void changePlatform(String platformName, String vendor) {
        System.out.println("[DataWarehouse] Platform lama: " +
                (dbPlatform != null ? dbPlatform.getPlatformName() : "none"));
        this.dbPlatform = new databaseplatform(platformName, vendor);
        System.out.println("[DataWarehouse] Platform baru : " + dbPlatform);
    }

    public String getPlatformName() {
        return dbPlatform != null ? dbPlatform.getPlatformName() : "Belum ada platform";
    }

    public String getPlatformVendor() {
        return dbPlatform != null ? dbPlatform.getVendor() : "Belum ada vendor";
    }

    public void displayPlatformInfo() {
        if (dbPlatform == null) {
            System.out.println("[DataWarehouse] Belum ada platform yang diset.");
            return;
        }
        dbPlatform.displayInfo();
    }

    public String         getlocation()        { return location; }
    public String         getcapacityGB()      { return capacity; }
    public String         getProcessingPower() { return processingPower; }
    public String         getStorageType()     { return storageType; }
    public String         getBackupStatus()    { return backupStatus; }
    public List<customer> getCustomers()       { return customers; }

    public databaseplatform getDbPlatform()    { return dbPlatform; }

    public void setLocation(String location)              { this.location = location; }
    public void setCapacity(String capacity)              { this.capacity = capacity; }
    public void setProcessingPower(String processingPower){ this.processingPower = processingPower; }
    public void setStorageType(String storageType)        { this.storageType = storageType; }
    public void setBackupStatus(String backupStatus)      { this.backupStatus = backupStatus; }

    public void removeCustomer(String customerId) {
        customers.removeIf(c -> c.getCustomerId().equals(customerId));
    }

    public void addcustomer(customer c) {
        if (c == null) {
            System.out.println("[DataWarehouse] ERROR: Customer null.");
            return;
        }
        customers.add(c);
        System.out.println("[DataWarehouse] Customer ditambahkan: " + c.getName());
    }

    public void displayAllCustomers() {
        System.out.println(" Customer List [" + location + "] ");
        if (customers.isEmpty()) {
            System.out.println("  (tidak ada customer)");
        } else {
            for (int i = 0; i < customers.size(); i++) {
                System.out.println("  [" + (i + 1) + "] " + customers.get(i));
            }
        }
    }

    public void displayWarehouseInfo() {
        System.out.println("           DATA WAREHOUSE INFO                    ");
        System.out.println("  Location        : " + location);
        System.out.println("  Capacity        : " + capacity);
        System.out.println("  Processing Power: " + processingPower);
        System.out.println("  Storage Type    : " + storageType);
        System.out.println("  Backup Status   : " + backupStatus);
        System.out.println("  Total Customers : " + customers.size());
        displayPlatformInfo();
    }

    @Override
    public String toString() {
        return String.format("DataWarehouse[location=%s, capacity=%s, platform=%s, customers=%d]",
                location, capacity, getPlatformName(), customers.size());
    }
}
