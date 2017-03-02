package com.pineapple.softgroup.interfaces;



import com.pineapple.softgroup.DB.model.User;

import java.util.List;

public interface IDBHelper {
    void addContact(User user);
    User getContact(int id);
    List<User> getAllContacts();
    int getContactsCount();
    int updateContact(User user);
    void deleteContact(User user);
    void deleteAll();
}
