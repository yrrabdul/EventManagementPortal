package com.eventmanagement.entities;

import java.time.LocalDate;

public class Employee {
    //attributes of employee
    private int id;
    private String name;
    private String phoneNumber;
    private String address;
    private String type;
    private float hourlyWage;
    private LocalDate dateOfBirth;

    //constructors


    public Employee(int id, String name, String phoneNumber, String address, String type, float hourlyWage, LocalDate dateOfBirth) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.type = type;
        this.hourlyWage = hourlyWage;
        this.dateOfBirth = dateOfBirth;
    }

    //default
    public Employee(){

    }

    //getter setter methods


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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public float getHourlyWage() {
        return hourlyWage;
    }

    public void setHourlyWage(float hourlyWage) {
        this.hourlyWage = hourlyWage;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }


    //toString method

    @Override
    public String toString() {
        return "Employee{" +
                "name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", address='" + address + '\'' +
                ", type='" + type + '\'' +
                ", hourlyWage=" + hourlyWage +
                ", dateOfBirth=" + dateOfBirth +
                '}';
    }
}
