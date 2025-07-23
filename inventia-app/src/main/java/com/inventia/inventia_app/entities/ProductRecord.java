package com.inventia.inventia_app.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * ProductRecords
 */
@Entity(name = "producto_venta")
public class ProductRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("record_id")
    @Column(name = "record_id")
    private Integer recordId;

    @JsonProperty("city_id")
    private Integer cityId;

    @JsonProperty("store_id")
    private Integer storeId;

    @JsonProperty("management_group_id")
    private Integer managementGroupId;

    @JsonProperty("first_category_id")
    private Integer firstCategoryId;

    @JsonProperty("second_category_id")
    private Integer secondCategoryId;

    @JsonProperty("third_category_id")
    private Integer thirdCategoryId;

    @JsonProperty("product_id")
    private Integer productId;

    @JsonProperty("dt")
    private String dt;

    @JsonProperty("sale_amount")
    private Double saleAmount;

    @JsonProperty("hours_sale")
    private List<Double> hoursSale;

    @JsonProperty("stock_hour6_22_cnt")
    private Integer stockHour6_22Cnt;

    @JsonProperty("hours_stock_status")
    private List<Integer> hoursStockStatus;

    @JsonProperty("discount")
    private Double discount;

    @JsonProperty("holiday_flag")
    private Integer holidayFlag;

    @JsonProperty("activity_flag")
    private Integer activityFlag;

    @JsonProperty("precpt")
    private Double precpt;

    @JsonProperty("avg_temperature")
    private Double avgTemperature;

    @JsonProperty("avg_humidity")
    private Double avgHumidity;

    @JsonProperty("avg_wind_level")
    private Double avgWindLevel;

    public ProductRecord(
            Integer city_id,
            Integer store_id,
            Integer management_group_id,
            Integer first_category_id,
            Integer second_category_id,
            Integer third_category_id,
            Integer product_id,
            String dt,
            Double sale_amount,
            List<Double> hours_sale,
            Integer stock_hour6_22_cnt,
            List<Integer> hours_stock_status,
            Double discount,
            Integer holiday_flag,
            Integer activity_flag,
            Double precpt,
            Double avg_temperature,
            Double avg_humidity,
            Double avg_wind_level) {
        this.cityId = city_id;
        this.storeId = store_id;
        this.managementGroupId = management_group_id;
        this.firstCategoryId = first_category_id;
        this.secondCategoryId = second_category_id;
        this.thirdCategoryId = third_category_id;
        this.productId = product_id;
        this.dt = dt;
        this.saleAmount = sale_amount;
        this.hoursSale = hours_sale;
        this.stockHour6_22Cnt = stock_hour6_22_cnt;
        this.hoursStockStatus = hours_stock_status;
        this.discount = discount;
        this.holidayFlag = holiday_flag;
        this.activityFlag = activity_flag;
        this.precpt = precpt;
        this.avgTemperature = avg_temperature;
        this.avgHumidity = avg_humidity;
        this.avgWindLevel = avg_wind_level;
    }

    public Integer getRecordId() {
        return recordId;
    }

    public void setRecordId(Integer recordId) {
        this.recordId = recordId;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public Integer getManagementGroupId() {
        return managementGroupId;
    }

    public void setManagementGroupId(Integer managementGroupId) {
        this.managementGroupId = managementGroupId;
    }

    public Integer getFirstCategoryId() {
        return firstCategoryId;
    }

    public void setFirstCategoryId(Integer firstCategoryId) {
        this.firstCategoryId = firstCategoryId;
    }

    public Integer getSecondCategoryId() {
        return secondCategoryId;
    }

    public void setSecondCategoryId(Integer secondCategoryId) {
        this.secondCategoryId = secondCategoryId;
    }

    public Integer getThirdCategoryId() {
        return thirdCategoryId;
    }

    public void setThirdCategoryId(Integer thirdCategoryId) {
        this.thirdCategoryId = thirdCategoryId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getDt() {
        return dt;
    }

    public void setDt(String dt) {
        this.dt = dt;
    }

    public Double getSaleAmount() {
        return saleAmount;
    }

    public void setSaleAmount(Double saleAmount) {
        this.saleAmount = saleAmount;
    }

    public List<Double> getHoursSale() {
        return hoursSale;
    }

    public void setHoursSale(List<Double> hoursSale) {
        this.hoursSale = hoursSale;
    }

    public Integer getStockHour6_22Cnt() {
        return stockHour6_22Cnt;
    }

    public void setStockHour6_22Cnt(Integer stockHour6_22Cnt) {
        this.stockHour6_22Cnt = stockHour6_22Cnt;
    }

    public List<Integer> getHoursStockStatus() {
        return hoursStockStatus;
    }

    public void setHoursStockStatus(List<Integer> hoursStockStatus) {
        this.hoursStockStatus = hoursStockStatus;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public Integer getHolidayFlag() {
        return holidayFlag;
    }

    public void setHolidayFlag(Integer holidayFlag) {
        this.holidayFlag = holidayFlag;
    }

    public Integer getActivityFlag() {
        return activityFlag;
    }

    public void setActivityFlag(Integer activityFlag) {
        this.activityFlag = activityFlag;
    }

    public Double getPrecpt() {
        return precpt;
    }

    public void setPrecpt(Double precpt) {
        this.precpt = precpt;
    }

    public Double getAvgTemperature() {
        return avgTemperature;
    }

    public void setAvgTemperature(Double avgTemperature) {
        this.avgTemperature = avgTemperature;
    }

    public Double getAvgHumidity() {
        return avgHumidity;
    }

    public void setAvgHumidity(Double avgHumidity) {
        this.avgHumidity = avgHumidity;
    }

    public Double getAvgWindLevel() {
        return avgWindLevel;
    }

    public void setAvgWindLevel(Double avgWindLevel) {
        this.avgWindLevel = avgWindLevel;
    }

    @Override
    public String toString() {
        return "ProductRecord{"
                + "city_id='" + cityId + '\''
                + ", store_id='" + storeId + '\''
                + ", management_group_id='" + managementGroupId + '\''
                + ", first_category_id='" + firstCategoryId + '\''
                + ", second_category_id='" + secondCategoryId + '\''
                + ", third_category_id='" + thirdCategoryId + '\''
                + ", product_id='" + productId + '\''
                + ", dt='" + dt + '\''
                + ", sale_amount='" + saleAmount + '\''
                + ", hours_sale=" + hoursSale
                + ", stock_hour6_22_cnt='" + stockHour6_22Cnt + '\''
                + ", hours_stock_status=" + hoursStockStatus
                + ", discount='" + discount + '\''
                + ", holiday_flag='" + holidayFlag + '\''
                + ", activity_flag='" + activityFlag + '\''
                + ", precpt='" + precpt + '\''
                + ", avg_temperature='" + avgTemperature + '\''
                + ", avg_humidity='" + avgHumidity + '\''
                + ", avg_wind_level='" + avgWindLevel + '\''
                + '}';
    }
}
