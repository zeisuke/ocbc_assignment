package com.cwleong.ocbc_assignment.utils;

public class PredicateUtil {

    public static boolean  getPredicateContainsString(String key, String value){
        return value.toUpperCase().contains(key.toUpperCase());
    }
}
