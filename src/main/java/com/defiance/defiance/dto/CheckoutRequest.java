package com.defiance.defiance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public class CheckoutRequest {
    @JsonProperty("full_name")
    @NotBlank
    private String fullName;

    @NotBlank
    private String phone;

    @JsonProperty("address_line")
    @NotBlank
    private String addressLine;

    @NotBlank
    private String barangay;

    @NotBlank
    private String city;

    @NotBlank
    private String province;

    @JsonProperty("postal_code")
    @NotBlank
    private String postalCode;

    @JsonProperty("payment_method")
    private String paymentMethod;

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddressLine() { return addressLine; }
    public void setAddressLine(String addressLine) { this.addressLine = addressLine; }
    public String getBarangay() { return barangay; }
    public void setBarangay(String barangay) { this.barangay = barangay; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
}
