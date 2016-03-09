package com.gezhii.fitgroup.dto;

import com.gezhii.fitgroup.dto.basic.UserCustomerTask;

import java.io.Serializable;
import java.util.List;

/**
 * Created by fantasy on 15/12/11.
 */
public class UserCustomerTaskDTO implements Serializable{
    List<UserCustomerTask> customerTaskList;

    public List<UserCustomerTask> getCustomerTaskList() {
        return customerTaskList;
    }

    public void setCustomerTaskList(List<UserCustomerTask> customerTaskList) {
        this.customerTaskList = customerTaskList;
    }
}
