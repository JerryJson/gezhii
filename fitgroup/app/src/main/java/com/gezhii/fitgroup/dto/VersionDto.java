package com.gezhii.fitgroup.dto;

import com.gezhii.fitgroup.tools.GsonHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Created by xianrui on 15/12/6.
 */
public class VersionDto extends BaseDto {

    private Version version;

    public void setVersion(Version version) {
        this.version = version;
    }

    public Version getVersion() {
        return version;
    }

    public static class Version {
        private int id;
        private String lastVersion;
        private String minimumVersion;
        private String apkUrl;
        private boolean hintUpdate;
        private String hintString;


        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getLastVersion() {
            return lastVersion;
        }

        public void setLastVersion(String lastVersion) {
            this.lastVersion = lastVersion;
        }

        public String getMinimumVersion() {
            return minimumVersion;
        }

        public void setMinimumVersion(String minimumVersion) {
            this.minimumVersion = minimumVersion;
        }

        public String getApkUrl() {
            return apkUrl;
        }

        public void setApkUrl(String apkUrl) {
            this.apkUrl = apkUrl;
        }

        public boolean isHintUpdate() {
            return hintUpdate;
        }

        public void setHintUpdate(boolean hintUpdate) {
            this.hintUpdate = hintUpdate;
        }

        public String getHintString() {
            return hintString;
        }

        public void setHintString(String hintString) {
            this.hintString = hintString;
        }
    }

    public static VersionDto parserJson(String jsonString) {
        Gson gson = GsonHelper.getInstance().getGson();
        return (VersionDto) gson.fromJson(jsonString, new TypeToken<VersionDto>() {
        }.getType());
    }
}
