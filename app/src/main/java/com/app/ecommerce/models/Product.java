package com.app.ecommerce.models;

public class Product {

    /*
    "product_id": "85",
        "product_name": "1111111111",
        "category_id": "5",
        "product_price": "2000",
        "product_status": "Available",
        "product_image": "1686750867_S01E03.jpg",
        "product_description": "<p>sfsdfsfsfsd</p>\r\n",
        "product_quantity": "22",
        "user_phone": "+998915005050",
        "state": "used",
        "category_name": "Датчики ",
        "currency_id": "158",
        "tax": "0",
        "currency_code": "UZS",
        "currency_name": "Uzbekistani sum"
        */

    private long product_id;
    private String product_name;
    private String category_id;
    private String category_name;
    private double product_price;
    private String product_status;
    private String product_image;
    private String product_description;
    private String currency_code;
    private double tax;
    private int product_quantity;
    private String user_phone;
    private String state;
    private String currency_id;
    private String currency_name;

    public long getProduct_id() {
        return product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public String getCategory_id() {
        return category_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public double getProduct_price() {
        return product_price;
    }

    public String getProduct_status() {
        return product_status;
    }

    public String getProduct_image() {
        return product_image;
    }

    public String getProduct_description() {
        return product_description;
    }

    public String getCurrency_code() {
        return currency_code;
    }

    public double getTax() {
        return tax;
    }

    public int getProduct_quantity() {
        return product_quantity;
    }

    public String getUser_phone() {
        return user_phone;
    }

    public String getState() {
        return state;
    }

    public String getCurrency_id() {
        return currency_id;
    }

    public String getCurrency_name() {
        return currency_name;
    }

    public void setProduct_id(long product_id) {
        this.product_id = product_id;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }
}