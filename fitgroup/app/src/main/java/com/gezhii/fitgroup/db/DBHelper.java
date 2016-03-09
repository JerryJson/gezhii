package com.gezhii.fitgroup.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gezhii.fitgroup.MyApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;

/**
 * Created by xianrui on 15/9/16.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_FILE_NAME = "fitgroup_sports.sqlite";
    private static final int DB_VERSION = 1;
    private static final SimpleDateFormat DATE_FORMAT_y_m_d = new SimpleDateFormat("yyyy.MM.dd");
    private static final SimpleDateFormat DATE_FORMAT_y_m_d_h_m_s = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

    private Context mContext;
    private SQLiteDatabase db;

    public static String getDataBasePath() {
        String path = DB_FILE_NAME;
        File filepath = new File(MyApplication.getApplication().getExternalFilesDir(null).toString());
        if (!filepath.exists()) {
            filepath.mkdirs();
        }
        path = filepath.getAbsolutePath() + File.separator + DB_FILE_NAME;
        File dbfile = new File(path);
        if (!dbfile.exists()) {
            copyDBFromAssets(path);
        }

        return path;
    }

    private static void copyDBFromAssets(String path) {
        InputStream myInput;
        try {
            OutputStream myOutput = new FileOutputStream(path);
            myInput = MyApplication.getApplication().getAssets().open("fitgroup_sports.sqlite");
            byte[] buffer = new byte[1024];
            int length = myInput.read(buffer);
            while (length > 0) {
                myOutput.write(buffer, 0, length);
                length = myInput.read(buffer);
            }

            myOutput.flush();
            myInput.close();
            myOutput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static class DBHelperHolder {
        public static DBHelper sington = new DBHelper(MyApplication.getApplication());
    }

    public static DBHelper getInstance() {
        return DBHelperHolder.sington;
    }

    public static void resetDB() {
        DBHelperHolder.sington = new DBHelper(MyApplication.getApplication());
    }

    private DBHelper(Context context) {
        super(context, getDataBasePath(), null, DB_VERSION);
        this.mContext = context;
        setDb(getWritableDatabase());
        System.out.println(getDataBasePath());

    }

    public SQLiteDatabase getDb() {
        return db;
    }

    public void setDb(SQLiteDatabase db) {
        this.db = db;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public interface Tables {
        String T_DIETS = "diets";
        String T_SPORT_CATEGORY = "sport_category";
        String T_SPORTS = "sports";
    }

    public interface Columns {

        interface DietsColumn {
            String _ID = "id";
            String _NAME = "name";
            String _ICON = "icon";
            String _DES = "des";
        }

        interface SportCategoryColumn {
            String _ID = "id";
            String _IMG = "img";
            String _ICON = "icon";
            String _NAME = "name";
        }

        interface SportsColumn {
            String _ID = "id";
            String _NAME = "name";
            String _CATEGORY_ID = "category_id";
            String _PARAMETERS = "parameters";
            String _DURATION="duration";
            String _COUNT="count";
            String _GROUP_COUNT="group_count";
            String _DISTANCE="distance";
            String _WEIGHT="weight";
            String _TAG="tag";
        }
    }

}
