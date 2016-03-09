package com.gezhii.fitgroup.model;

import android.text.TextUtils;

import com.gezhii.fitgroup.dto.SigninInfoDto;
import com.gezhii.fitgroup.dto.basic.SigninInfoContent;
import com.gezhii.fitgroup.dto.basic.SigninInfoDiet;
import com.gezhii.fitgroup.dto.basic.SigninInfoSport;
import com.gezhii.fitgroup.dto.db.DBDiet;
import com.gezhii.fitgroup.dto.db.DBSport;
import com.gezhii.fitgroup.tools.DataKeeperHelper;
import com.gezhii.fitgroup.tools.GsonHelper;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xianrui on 15/11/11.
 */
public class SignInfoModel {

    public static final String TAG_SIGN_INFO_MODEL = "TAG_SIGN_INFO_MODEL";

    public static final int TYPE_SPORT = 1;
    public static final int TYPE_DIET = 2;
    public static final int TYPE_CONTENT = 3;


    ArrayList<Object> mSignItemList;

    private static class SignInfoModelHolder {
        public final static SignInfoModel sington = new SignInfoModel();
    }

    public static SignInfoModel getInstance() {
        return SignInfoModelHolder.sington;
    }

    public SignInfoModel() {
        String signItemListString = DataKeeperHelper.getInstance().getDataKeeper().get(TAG_SIGN_INFO_MODEL, "");
        if (!TextUtils.isEmpty(signItemListString)) {
            mSignItemList = (ArrayList<Object>) keepListToNormalList((List<DATA_KEEP_DTO>) GsonHelper.getInstance()
                    .getGson().fromJson(signItemListString, new TypeToken<List<DATA_KEEP_DTO>>() {
                    }.getType()));
        } else {
            mSignItemList = new ArrayList<>();
            for (DBSport dbSport : SportDBModel.getInstance().getDBSportList()) {
                if (dbSport.tag > 20) {
                    dbSport.isSelect = true;
                    mSignItemList.add(dbSport);
                }
            }
            clearListData();
        }
    }

    public void clear() {
        mSignItemList = new ArrayList<>();
        DataKeeperHelper.getInstance().getDataKeeper().put(TAG_SIGN_INFO_MODEL, "");
        for (DBSport dbSport : SportDBModel.getInstance().getDBSportList()) {
            if (dbSport.tag > 20) {
                dbSport.isSelect = true;
                mSignItemList.add(dbSport);
            }
        }
        clearListData();
    }


    public SigninInfoDto getSignInfoDto() {
        SigninInfoDto signinInfoDto = new SigninInfoDto();
        signinInfoDto.setDiets(new ArrayList<SigninInfoDiet>());
        signinInfoDto.setSports(new ArrayList<SigninInfoSport>());
        for (Object object : mSignItemList) {
            if (object instanceof DBSport) {
                DBSport dbSport = (DBSport) object;
                SigninInfoSport signinInfoSport = new SigninInfoSport();
                signinInfoSport.setName(dbSport.name);
                signinInfoSport.setSport_category_id(dbSport.category_id);
                signinInfoSport.setGroup_count(dbSport.group_count);
                signinInfoSport.setCount(dbSport.count);
                signinInfoSport.setDistance(dbSport.distance);
                signinInfoSport.setDuration(dbSport.duration);
                signinInfoSport.setWeight(dbSport.weight);
                if (dbSport.isSelect) {
                    signinInfoDto.getSports().add(signinInfoSport);
                }
            } else if (object instanceof DBDiet) {
                DBDiet dbDiet = (DBDiet) object;
                SigninInfoDiet signinInfoDiet = new SigninInfoDiet();
                signinInfoDiet.setName(dbDiet.name);
                signinInfoDiet.setDescription(dbDiet.content);
                if (dbDiet.isSelect) {
                    signinInfoDto.getDiets().add(signinInfoDiet);
                }

            } else if (object instanceof SigninInfoContent) {
                SigninInfoContent signinInfoContent = (SigninInfoContent) object;
                if (signinInfoContent.isSelect) {
                    signinInfoDto.setContent(signinInfoContent.description);
                    signinInfoDto.setImage(signinInfoContent.img);
                }
            }
        }
        return signinInfoDto;
    }

    public void clearListData() {
        for (Object object : mSignItemList) {
            if (object instanceof DBSport) {
                DBSport dbSport = (DBSport) object;
                if (dbSport.isSelect) {
                    dbSport.weight = 0;
                    dbSport.distance = 0;
                    dbSport.group_count = 0;
                    dbSport.count = 0;
                    dbSport.duration = 0;
                    dbSport.isSelect = false;
                }
            } else if (object instanceof DBDiet) {
                DBDiet dbDiet = (DBDiet) object;
                if (dbDiet.isSelect) {
                    dbDiet.content = "";
                    dbDiet.isSelect = false;
                }
            } else if (object instanceof SigninInfoContent) {
                SigninInfoContent signinInfoContent = (SigninInfoContent) object;
                if (signinInfoContent.isSelect) {
                    signinInfoContent.description = "";
                    signinInfoContent.img = "";
                    signinInfoContent.isSelect = false;
                }
            }
        }
        save();
        SportDBModel.getInstance().updateSportList();
    }


    public List<Object> getSignItemList() {
        return mSignItemList;
    }

    public void moveSignItem(int fromPosition, int toPosition) {
        Object object = mSignItemList.remove(fromPosition);
        mSignItemList.add(toPosition, object);
        save();
    }

    public void removeItem(int position) {
        mSignItemList.remove(position);
        save();
    }

    public void removeItem(Object o) {
        mSignItemList.remove(o);
        save();
    }

    public void updateSignItem(Object item) {
        mSignItemList.set(mSignItemList.indexOf(item), item);
        save();
    }

    private void save() {
        DataKeeperHelper.getInstance().getDataKeeper().put(TAG_SIGN_INFO_MODEL,
                GsonHelper.getInstance().getGson().toJson(normalListToKeepList(mSignItemList)));
    }

    public void addSignItem(Object o) {
        if (o instanceof DBSport) {
            boolean hasSport = false;
            for (int i = 0; i < mSignItemList.size(); i++) {
                Object object = mSignItemList.get(i);
                if (object instanceof DBSport) {
                    DBSport dbSport = (DBSport) object;
                    DBSport dbSport1 = (DBSport) o;
                    if (dbSport.name.equals(dbSport1.name)) {
                        hasSport = true;
                        mSignItemList.set(i, dbSport1);
                        break;
                    }
                }
            }
            if (!hasSport) {
                mSignItemList.add(o);
            }
        } else if (o instanceof DBDiet) {
            boolean hasDiet = false;
            for (int i = 0; i < mSignItemList.size(); i++) {
                Object object = mSignItemList.get(i);
                if (object instanceof DBDiet) {
                    DBDiet dbDiet = (DBDiet) object;
                    DBDiet dbDiet1 = (DBDiet) o;
                    if (dbDiet.name.equals(dbDiet1.name)) {
                        hasDiet = true;
                        mSignItemList.set(i, dbDiet1);
                        break;
                    }
                }
            }
            if (!hasDiet) {
                mSignItemList.add(o);
            }
        } else if (o instanceof SigninInfoContent) {
            boolean hasContent = false;
            for (int i = 0; i < mSignItemList.size(); i++) {
                Object object = mSignItemList.get(i);
                if (object instanceof SigninInfoContent) {
                    hasContent = true;
                    mSignItemList.set(i, o);
                    break;
                }
            }
            if (!hasContent) {
                mSignItemList.add(o);
            }
        }
        save();
    }


    private static List<DATA_KEEP_DTO> normalListToKeepList(List<Object> signItemList) {
        List<DATA_KEEP_DTO> dataKeepDtoList = new ArrayList<>();
        for (Object object : signItemList) {
            if (object instanceof DBSport) {
                DBSport dbSport = (DBSport) object;
                DATA_KEEP_DTO data_keep_dto = new DATA_KEEP_DTO();
                data_keep_dto.type = TYPE_SPORT;
                data_keep_dto.id = dbSport.id;
                data_keep_dto.name = dbSport.name;
                data_keep_dto.category_id = dbSport.category_id;
                data_keep_dto.parameters = dbSport.parameters;
                data_keep_dto.duration = dbSport.duration;
                data_keep_dto.count = dbSport.count;
                data_keep_dto.group_count = dbSport.group_count;
                data_keep_dto.distance = dbSport.distance;
                data_keep_dto.weight = dbSport.weight;
                data_keep_dto.tag = dbSport.tag;
                data_keep_dto.isSelect = dbSport.isSelect;
                dataKeepDtoList.add(data_keep_dto);
            } else if (object instanceof DBDiet) {
                DBDiet dbDiet = (DBDiet) object;
                DATA_KEEP_DTO data_keep_dto = new DATA_KEEP_DTO();
                data_keep_dto.type = TYPE_DIET;
                data_keep_dto.id = dbDiet.id;
                data_keep_dto.name = dbDiet.name;
                data_keep_dto.icon = dbDiet.icon;
                data_keep_dto.des = dbDiet.des;
                data_keep_dto.content = dbDiet.content;
                data_keep_dto.isSelect = dbDiet.isSelect;
                dataKeepDtoList.add(data_keep_dto);
            } else if (object instanceof SigninInfoContent) {
                SigninInfoContent signinInfoContent = (SigninInfoContent) object;
                DATA_KEEP_DTO data_keep_dto = new DATA_KEEP_DTO();
                data_keep_dto.type = TYPE_CONTENT;
                data_keep_dto.img = signinInfoContent.img;
                data_keep_dto.description = signinInfoContent.description;
                data_keep_dto.isSelect = signinInfoContent.isSelect;
                dataKeepDtoList.add(data_keep_dto);
            }
        }
        return dataKeepDtoList;
    }

    private static List<Object> keepListToNormalList(List<DATA_KEEP_DTO> dataKeepDtoList) {
        List<Object> normalList = new ArrayList<>();
        for (DATA_KEEP_DTO data_keep_dto : dataKeepDtoList) {
            if (data_keep_dto.type == TYPE_SPORT) {
                DBSport dbSport = new DBSport();
                dbSport.id = data_keep_dto.id;
                dbSport.name = data_keep_dto.name;
                dbSport.category_id = data_keep_dto.category_id;
                dbSport.parameters = data_keep_dto.parameters;
                dbSport.duration = data_keep_dto.duration;
                dbSport.count = data_keep_dto.count;
                dbSport.group_count = data_keep_dto.group_count;
                dbSport.distance = data_keep_dto.distance;
                dbSport.weight = data_keep_dto.weight;
                dbSport.tag = data_keep_dto.tag;
                dbSport.isSelect = data_keep_dto.isSelect;
                normalList.add(dbSport);
            } else if (data_keep_dto.type == TYPE_DIET) {
                DBDiet dbDiet = new DBDiet();
                dbDiet.id = data_keep_dto.id;
                dbDiet.name = data_keep_dto.name;
                dbDiet.icon = data_keep_dto.icon;
                dbDiet.des = data_keep_dto.des;
                dbDiet.content = data_keep_dto.content;
                dbDiet.isSelect = data_keep_dto.isSelect;
                normalList.add(dbDiet);
            } else if (data_keep_dto.type == TYPE_CONTENT) {
                SigninInfoContent signinInfoContent = new SigninInfoContent();
                signinInfoContent.img = data_keep_dto.img;
                signinInfoContent.description = data_keep_dto.description;
                signinInfoContent.isSelect = data_keep_dto.isSelect;
                normalList.add(signinInfoContent);
            }
        }
        return normalList;
    }


    private static class DATA_KEEP_DTO {
        //        public int id;
//        public String name;
        public int category_id;
        public String parameters;
        public int duration;
        public int count;
        public int group_count;
        public double distance;
        public double weight;
        public int tag;
        public boolean isSelect;

        public int id;
        public String name;
        public String icon;
        public String des;
        public String content;

        private String img;
        private String description;

        public int type;

    }

}
