package com.gezhii.fitgroup.model;

/**
 * Created by fantasy on 15/12/1.
 */
public class UserWeightModel {
    int sex;
    int height;
    float weight;
    float goalWeight;
    boolean isFirstSet;
    boolean isBmiChange;

    private static class UserWeightModelHolder {
        public final static UserWeightModel sington = new UserWeightModel();
    }

    public static UserWeightModel getInstance() {
        return UserWeightModelHolder.sington;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getGoalWeight() {
        return goalWeight;
    }

    public void setGoalWeight(float goalWeight) {
        this.goalWeight = goalWeight;
    }

    public boolean isFirstSet() {
        return isFirstSet;
    }

    public void setIsFirstSet(boolean isFirstSet) {
        this.isFirstSet = isFirstSet;
    }

    public boolean isBmiChange() {
        return isBmiChange;
    }

    public void setIsBmiChange(boolean isBmiChange) {
        this.isBmiChange = isBmiChange;
    }

}
