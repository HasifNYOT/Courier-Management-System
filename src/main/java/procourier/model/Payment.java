package procourier.model;

import java.util.Date;

public class Payment {
    private int paymentId;
    private Package pAckage;
    private double amount;
    private String method;
    private Date time;
    private String status;
    private String receipt;

    public Payment() {}

    public Payment(int paymentId, Package pAckage, double amount, String method, Date time, String status, String receipt) {
        this.paymentId = paymentId;
        this.pAckage = pAckage;
        this.amount = amount;
        this.method = method;
        this.time = time;
        this.status = status;
        this.receipt = receipt;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public Package getPackage() {
        return pAckage;
    }

    public void setPackage(Package pAckage) {
        this.pAckage = pAckage;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative.");
        }
        this.amount = amount;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReceipt() {
        return receipt;
    }

    public void setReceipt(String receipt) {
        this.receipt = receipt;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "paymentId=" + paymentId +
                ", pAckage=" + (pAckage != null ? pAckage.getPackageId() : "null") +
                ", amount=" + amount +
                ", method='" + method + '\'' +
                ", time=" + time +
                ", status='" + status + '\'' +
                ", receipt='" + receipt + '\'' +
                '}';
    }
}
