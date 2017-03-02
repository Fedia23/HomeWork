package com.pineapple.softgroup.interfaces;

import android.view.View;

import com.pineapple.softgroup.DB.model.Contacts;

public interface IFruitImgButton {
    void fruitDelete(int position, View v);
    void edit(int position, Contacts contacts, View v);
}
