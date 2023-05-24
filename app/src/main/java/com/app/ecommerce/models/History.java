package com.app.ecommerce.models;

public class History {

    private String id;
    private String code;
    private String order_list;
    private String order_total;
    private String date_time;

    public History(String id, String code, String order_list, String order_total, String date_time) {
        this.id = id;
        this.code = code;
        this.order_list = order_list;
        this.order_total = order_total;
        this.date_time = date_time;
    }
}
