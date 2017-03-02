package com.pineapple.softgroup.DB.model;


import com.pineapple.softgroup.crypto.CryptoBase;

public class User {
    private int id;
    private String name;
    private String login;
    private String pass;

    public User(){
    }
    public User(String login){
        setLogin(login);
    }

    public User(int id, String name, String login, String pass){
        setID(id);
        setName(name);
        setLogin(login);
        setPass(pass);
    }

    public User(String name, String login, String pass){
        setName(name);
        setLogin(login);
        setPass(pass);
    }

    public User(String login, String pass){
        setLogin(login);
        setPass(pass);
    }

    public int getID(){
        return this.id;
    }

    public void setID(int id){
        this.id = id;
    }

    public String getLogin(){
        return this.login;
    }

    public void setLogin(String login){
        this.login = login;
    }

    public String getPass(){
        return CryptoBase.decrypt(this.pass);
    }

    public void setPass(String pass){
        this.pass = CryptoBase.encrypt(pass);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
