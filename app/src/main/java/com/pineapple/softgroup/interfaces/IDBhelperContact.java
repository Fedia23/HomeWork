package com.pineapple.softgroup.interfaces;


import com.pineapple.softgroup.DB.model.Contacts;

import java.util.List;

public interface IDBhelperContact {
    void addContact(Contacts contacts);
    Contacts getContact(int id);
    List<Contacts> getAllContacts();
    public int getContactsCount();
    int updateContatct(Contacts contacts);
    void deleteConatct(Contacts contacts);
    public void deleteAll();
}
