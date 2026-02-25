package procourier.model;

import java.util.Date;

public class Customer extends Person {
    private String password, address, profileImage;
    private Date dob;

    public Customer() {}

    public Customer(int id, String name, String email, String phone, String password, String address, Date dob, String profileImage) {
        super(id, name, email, phone);
        this.password = password;
        this.address = address;
        this.dob = dob;
        this.profileImage= profileImage;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    @Override
    public String toString() {
        return "Customer{" +
                super.toString() +
                ", address='" + address + '\'' +
                ", dob=" + dob +
                '}';
    }
    
    public String getProfileImage() {
        return profileImage;
    }
    
    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
