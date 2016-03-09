package com.gezhii.fitgroup.dto.basic;

import java.util.List;

/**
 * Created by isansmith on 15/12/30.
 */
public class PushArticleAndSignin {
    private String title;
    private String img;
    private String link;
    private List<Integer> sign_ids;

    public List<Integer> getSign_ids() {
        return sign_ids;
    }

    public void setSign_ids(List<Integer> sign_ids) {
        this.sign_ids = sign_ids;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }


}
