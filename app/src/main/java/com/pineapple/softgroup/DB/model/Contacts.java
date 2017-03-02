package com.pineapple.softgroup.DB.model;

import java.util.ArrayList;

public class Contacts {

    private int id;
    private String name;
    private String number;

    public Contacts() {
    }

    public Contacts(String name, String number) {
        setName(name);
        setNumber(number);
    }

    public Contacts(int id, String name, String number) {
        setId(id);
        setName(name);
        setNumber(number);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }

    public static int findById(ArrayList<Contacts> list, int id){
        for (int i = 0; i != list.size(); i++){
            if(list.get(i).getId() == id){
                return i;
            }
        }
        return -1;
    }
}
