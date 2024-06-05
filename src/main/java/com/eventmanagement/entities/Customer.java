package com.eventmanagement.entities;

public class Customer {
    //attributes of customer
    private int id;
    private String Fname;
    private String Lname;
    private String number;
    private String address;


    //constructor
    public Customer(){

    }


    public Customer(String Fname, String Lname, String number, String address) {
        this.Fname = Fname;
        this.Lname = Lname;
        this.number = number;
        this.address = address;
    }

    public Customer(int id, String Fname, String Lname, String number, String address) {
        this.id = id;
        this.Fname = Fname;
        this.Lname = Lname;
        this.number = number;
        this.address = address;
    }

    public Customer(String Fname, String Lname){
        this.Fname = Fname;
        this.Lname = Lname;
    }

    //getter setter methods

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFname() {
        return Fname;
    }

    public void setFname(String fname) {
        this.Fname = fname;
    }

    public String getLname() {
        return Lname;
    }

    public void setLname(String lname) {
        this.Lname = lname;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    //toString method

    @Override
    public String toString() {
        return Fname +" "+ Lname;
    }
}
