package metro.marketers;

public class customer {
    private String customerId;
    private String name;
    private String email;
    private marketingprofile profile; 

    public customer(String customerId, String name, String email, marketingprofile profile) {
        this.customerId = customerId;
        this.name       = name;
        this.email      = email;
        this.profile    = profile;
    }

    public String getCustomerId()        { return customerId; }
    public String getName()              { return name; }
    public String getEmail()             { return email; }
    public marketingprofile getProfile() { return profile; }

    public void setName(String name)               { this.name = name; }
    public void setEmail(String email)             { this.email = email; }
    public void setProfile(marketingprofile p)     { this.profile = p; }

    public String toString() {
        return String.format("[%s] %s | %s", customerId, name, email);
    }

    public void displayDetail() {
        System.out.println("         CUSTOMER DETAIL              ");
        System.out.println("  ID    : " + customerId);
        System.out.println("  Name  : " + name);
        System.out.println("  Email : " + email);
        if (profile != null) {
            profile.displayProfile();
        } else {
            System.out.println("  Profile: (no profile)");
        }
    }
}