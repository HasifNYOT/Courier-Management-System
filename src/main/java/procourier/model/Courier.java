package procourier.model;

import java.util.Date;

public class Courier extends Person {
    private String ic, profileImage;
    private String password;
    private String license;
    private String status;
    private Date dob;
    private int deliveryLimit, currentActiveDelivery;

    public Courier() {}

    public Courier(int id, String name, String ic, String email, String phone, String password, String license, String status, Date dob, String profileImage, int deliveryLimit, int currentActiveDelivery) {
        super(id, name, email, phone);
        this.ic = ic;
        this.password = password;
        this.license = license;
        this.status = status;
        this.dob = dob;
        this.profileImage = profileImage;
        this.deliveryLimit = deliveryLimit;
        this.currentActiveDelivery = currentActiveDelivery;
    }

    public String getIc() {
        return ic;
    }

    public void setIc(String ic) {
        if (!ic.matches("^\\d{12}$")) {
            throw new IllegalArgumentException("IC must be 12 digits long.");
        }
        this.ic = ic;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long.");
        }
        this.password = password;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }
    
    public int getDeliveryLimit() {
        return deliveryLimit;
    }

    public void setDeliveryLimit(int deliveryLimit) {
        this.deliveryLimit = deliveryLimit;
    }
    
    public int getCurrentActiveDelivery() {
        return currentActiveDelivery;
    }

    public void setCurrentActiveDelivery(int currentActiveDelivery) {
        this.currentActiveDelivery = currentActiveDelivery;
    }

    @Override
    public String toString() {
        return "Courier{" +
                super.toString() +
                ", ic='" + ic + '\'' +
                ", license='" + license + '\'' +
                ", status='" + status + '\'' +
                ", dob=" + dob + '\'' +
                ", dob=" + deliveryLimit + '\'' +
                ", dob=" + currentActiveDelivery +
                '}';
    }
    
    public String getProfileImage() {
        return profileImage;
    }
    
    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
    
}
