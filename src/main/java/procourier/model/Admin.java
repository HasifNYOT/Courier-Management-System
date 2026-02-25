package procourier.model;

import java.util.Date;

public class Admin extends Person {
    private String password, profileImage;
    private Date dob;

    public Admin() {}

    public Admin(int id, String name, String email, String phone, String password, Date dob, String profileImage) {
        super(id, name, email, phone);
        this.password = password;
        this.dob = dob;
        this.profileImage = profileImage;
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

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    @Override
    public String toString() {
        return "Admin{" +
                super.toString() +
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
