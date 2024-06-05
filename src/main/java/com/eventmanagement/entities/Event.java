package com.eventmanagement.entities;

import java.sql.Time;
import java.time.LocalDate;
import java.util.Date;

public class Event {
    //attributes of event
    private int id;
    private String name;
    private String type;
    private String venue;
    private LocalDate date;
    private int expense;
    private int priceDecided;
    private Time startTime;
    private Time endTime;
    private Customer customer;
    private String venueAddress;

    //constructor

    public Event() {
    }

    public Event(String name, String venue, String type, LocalDate date, int expense, int priceDecided, Time startTime, Time endTime, Customer customer) {
        this.name = name;
        this.venue = venue;
        this.date = date;
        this.expense = expense;
        this.priceDecided = priceDecided;
        this.startTime = startTime;
        this.endTime = endTime;
        this.customer = customer;
        this.type = type;
    }

    public Event(int id, String name, String type, String venue, String venueAddress, LocalDate date, int expense, int priceDecided, Time startTime, Time endTime, Customer customer) {
        this.id = id;
        this.name = name;
        this.venue = venue;
        this.date = date;
        this.expense = expense;
        this.priceDecided = priceDecided;
        this.startTime = startTime;
        this.endTime = endTime;
        this.customer = customer;
        this.venueAddress = venueAddress;
        this.type = type;
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

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getExpense() {
        return expense;
    }

    public void setExpense(int expense) {
        this.expense = expense;
    }

    public int getPriceDecided() {
        return priceDecided;
    }

    public void setPriceDecided(int priceDecided) {
        this.priceDecided = priceDecided;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVenueAddress() {
        return venueAddress;
    }

    public void setVenueAddress(String venueAddress) {
        this.venueAddress = venueAddress;
    }
    //toString method

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", venue='" + venue + '\'' +
                ", date=" + date +
                ", expense=" + expense +
                ", priceDecided=" + priceDecided +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
