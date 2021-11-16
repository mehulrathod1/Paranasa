package com.ni.parnasa.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class JobInvoiceDetail implements Serializable {

    @SerializedName("job_booking_id")
    private String jobBookingId;

    @SerializedName("basic_rate")
    private String basicRate;

    @SerializedName("working_hour")
    private String workingHour;

    @SerializedName("working_hour_rate")
    private String workingHourRate;

    @SerializedName("parts_name")
    private String partsName;

    @SerializedName("parts_rate")
    private String partsRate;

    @SerializedName("tax")
    private String tax;

    @SerializedName("tax_rate")
    private String taxRate;

    @SerializedName("discount")
    private String discount;

    @SerializedName("total")
    private String total;

    @SerializedName("grand_total")
    private String grandTotal;

    @SerializedName("payment_method")
    private String paymentMethod;

    @SerializedName("payment_status")
    private String paymentStatus;

    public String getJobBookingId() {
        return jobBookingId;
    }

    public String getBasicRate() {
        return basicRate;
    }

    public String getWorkingHour() {
        return workingHour;
    }

    public String getWorkingHourRate() {
        return workingHourRate;
    }

    public String getPartsName() {
        return partsName;
    }

    public String getPartsRate() {
        return partsRate;
    }

    public String getTax() {
        return tax;
    }

    public String getTaxRate() {
        return taxRate;
    }

    public String getDiscount() {
        return discount;
    }

    public String getTotal() {
        return total;
    }

    public String getGrandTotal() {
        return grandTotal;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    @Override
    public String toString() {
        return "JobInvoiceDetail{" +
                "jobBookingId='" + jobBookingId + '\'' +
                ", basicRate='" + basicRate + '\'' +
                ", workingHour='" + workingHour + '\'' +
                ", workingHourRate='" + workingHourRate + '\'' +
                ", partsName='" + partsName + '\'' +
                ", partsRate='" + partsRate + '\'' +
                ", tax='" + tax + '\'' +
                ", taxRate='" + taxRate + '\'' +
                ", discount='" + discount + '\'' +
                ", total='" + total + '\'' +
                ", grandTotal='" + grandTotal + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", paymentStatus='" + paymentStatus + '\'' +
                '}';
    }
}