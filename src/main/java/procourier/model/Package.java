package procourier.model;

public class Package {
    private int packageId;
    private Customer customer;
    private double weight;
    private String dimension;
    private String description;
    private String receivername;
    private String dropoff;
    private String packageStatus;
    private Payment payment; // Add this field

    public Package() {}

    public Package(int packageId, double weight, String dimension, String description, String receivername, String dropoff, Customer customer, String packageStatus, Payment payment) {
        this.packageId = packageId;
        this.weight = weight;
        this.dimension = dimension;
        this.description = description;
        this.receivername = receivername;
        this.dropoff = dropoff;
        this.customer = customer;
        this.packageStatus = packageStatus;
        this.payment = payment;
    }

    public int getPackageId() {
        return packageId;
    }

    public void setPackageId(int packageId) {
        this.packageId = packageId;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        if (weight < 0) {
            throw new IllegalArgumentException("Weight cannot be negative.");
        }
        this.weight = weight;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getReceivername() {
        return receivername;
    }

    public void setReceivername(String receivername) {
        this.receivername = receivername;
    }
    
    public String getDropoff() {
        return dropoff;
    }

    public void setDropoff(String dropoff) {
        this.dropoff = dropoff;
    }
    
    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    
    public String getPackageStatus() {
        return packageStatus;
    }

    public void setPackageStatus(String packageStatus) {
        this.packageStatus = packageStatus;
    }
    
    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    @Override
    public String toString() {
        return "Package{" +
                "packageId=" + packageId +
                ", weight=" + weight +
                ", dimension='" + dimension + '\'' +
                ", description='" + description + '\'' +
                ", receivername='" + receivername + '\'' +
                ", dropoff='" + dropoff + '\'' +
                ", customerId=" + (customer != null ? customer.getId() : "null") +
                ", packageStatus='" + packageStatus + '\'' +
                '}';
    }

}
