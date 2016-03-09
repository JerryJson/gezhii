package com.gezhii.fitgroup.event;

import com.gezhii.fitgroup.dto.db.DBSport;

/**
 * Created by xianrui on 15/11/2.
 */
public class AddSignSportEvent {
    DBSport dbSport;


    public AddSignSportEvent(DBSport dbSport) {
        this.dbSport = dbSport;
    }

    public DBSport getDbSport() {
        return dbSport;
    }

}
