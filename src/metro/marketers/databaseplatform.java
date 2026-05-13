package metro.marketers;

public class databaseplatform {

    private String platformName;  
    private String vendor;        

    public databaseplatform(String platformName, String vendor) {
        this.platformName = platformName;
        this.vendor       = vendor;
    }

    public String getPlatformName() { return platformName; }
    public String getVendor()       { return vendor; }

    public void setPlatformName(String platformName) { this.platformName = platformName; }
    public void setVendor(String vendor)             { this.vendor = vendor; }

    public void displayInfo() {
        System.out.println("\n  --- Database Platform ---");
        System.out.println("  Platform Name : " + platformName);
        System.out.println("  Vendor        : " + vendor);
    }

    @Override
    public String toString() {
        return String.format("databaseplatform[name=%s, vendor=%s]", platformName, vendor);
    }
}
