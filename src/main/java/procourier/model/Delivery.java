package procourier.model;

import java.util.Date;

public class Delivery {
    private int deliveryId;
    private Courier courier;
    private Customer customer;
    private String status;
    private Date date;
    private String pickup;
    private Package pAckage;
    private Payment payment;
    private String dropoff; // Represents the Dropoff_location from the package table

    public Delivery() {}

    public Delivery(int deliveryId, Courier courier, Customer customer, String status, Date date, String pickup, Package pAckage, Payment payment, String dropoff) {
        this.deliveryId = deliveryId;
        this.courier = courier;
        this.customer = customer;
        this.status = status;
        this.date = date;
        this.pickup = pickup;
        this.pAckage = pAckage;
        this.payment = payment;
        this.dropoff = dropoff;
    }

    public int getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(int deliveryId) {
        this.deliveryId = deliveryId;
    }

    public Courier getCourier() {
        return courier;
    }

    public void setCourier(Courier courier) {
        this.courier = courier;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getPickup() {
        return pickup;
    }

    public void setPickup(String pickup) {
        this.pickup = pickup;
    }

    public Package getPackage() {
        return pAckage;
    }

    public void setPackage(Package pAckage) {
        this.pAckage = pAckage;
    }
    
    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }
    
    public String getDropoff() {
        return dropoff;
    }

    public void setDropoff(String dropoff) {
        this.dropoff = dropoff;
    }

    @Override
    public String toString() {
        return "Delivery{" +
                "deliveryId=" + deliveryId +
                ", courier=" + (courier != null ? courier.getId() : "null") +
                ", customer=" + (customer != null ? customer.getId() : "null") +
                ", status='" + status + '\'' +
                ", date=" + date +
                ", pickup='" + pickup + '\'' +
                ", package='" + (pAckage != null ? pAckage.getPackageId() : "null") + + '\'' +
                ", package='" + (payment != null ? payment.getPaymentId() : "null") + + '\'' +
                ", dropoff='" + dropoff + '\'' +
                '}';
    }
}
