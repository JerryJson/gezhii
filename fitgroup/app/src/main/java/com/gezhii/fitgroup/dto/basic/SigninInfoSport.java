package com.gezhii.fitgroup.dto.basic;

/**
 * Created by xianrui on 15/10/23.
 */
public class SigninInfoSport {

    private String name;
    private int sport_category_id;
    private int duration;
    private double distance;
    private int count;
    private int group_count;
    private double weight;
    private int step;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSport_category_id() {
        return sport_category_id;
    }

    public void setSport_category_id(int sport_category_id) {
        this.sport_category_id = sport_category_id;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getGroup_count() {
        return group_count;
    }

    public void setGroup_count(int group_count) {
        this.group_count = group_count;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }
}
