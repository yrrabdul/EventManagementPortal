package com.eventmanagement.entities;

public class StockItem {
    //attributes
    private int id;
    private String name;
    private String detail;
    private int quantity;
    private int unitPrice;

    //constructors


    public StockItem() {

    }

    public StockItem(int id, String name, String detail, int quantity, int unitPrice) {
        this.id = id;
        this.name = name;
        this.detail = detail;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    //getter setter method

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(int unitPrice) {
        this.unitPrice = unitPrice;
    }

    //toString

    @Override
    public String toString() {
        return "StockItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", detail='" + detail + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                '}';
    }
}
