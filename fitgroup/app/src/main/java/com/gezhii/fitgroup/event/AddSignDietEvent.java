package com.gezhii.fitgroup.event;

import com.gezhii.fitgroup.dto.db.DBDiet;

/**
 * Created by xianrui on 15/11/2.
 */
public class AddSignDietEvent {
    DBDiet dbDiet;

    public AddSignDietEvent(DBDiet dbDiet) {
        this.dbDiet = dbDiet;
    }

    public DBDiet getDbDiet() {
        return dbDiet;
    }
}
