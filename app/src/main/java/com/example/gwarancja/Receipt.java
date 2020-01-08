package com.example.gwarancja;

public class Receipt {
    private String product;
    private String imgUrl;
    private String date;
    private int years;
    private String endDate;
    private String pushId;

    public Receipt() {
        //empty constructor needed
    }

    public Receipt(String product,String imgUrl, String date, int years, String endDate) {
        this.product = product;
        this.imgUrl = imgUrl;
        this.date = date;
        this.years = years;
        this.endDate = endDate;
    }

    public String getProduct() {
        return product;
    }

    public int getYears() {
        return years;
    }

    public String getDate() {
        return date;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getImageUrl() {
        return imgUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imgUrl = imageUrl;
    }

}

