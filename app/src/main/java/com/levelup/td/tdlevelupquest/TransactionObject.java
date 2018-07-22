package com.levelup.td.tdlevelupquest;

import java.io.Serializable;

/**
 * Created by Mikeb on 7/22/2018.
 */

class TransactionObject implements Serializable{
    Double totalAmount;
    int numberOfTimes;
    String category;
    public TransactionObject(Double newAmount, String newCategory){
        this.totalAmount = newAmount;
        this.category = newCategory;
        numberOfTimes = 1;
    }
}