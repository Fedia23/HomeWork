package com.pineapple.softgroup.crypto;

import android.util.Base64;

    public class CryptoBase {

        private CryptoBase(){}

        public static String encrypt(String s){

            return Base64.encodeToString(s.getBytes(), Base64.DEFAULT);
        }

        public static String decrypt(String s){

            return new String(Base64.decode(s, Base64.DEFAULT));
        }
    }
