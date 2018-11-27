package com.example.jt.locus_draft;

/**
 * Created by User on 4/20/2018.
 */

public class Photo {

    private String mImgUrl;
    PhotoLoc pl;

    public Photo(String img, PhotoLoc pl) {
        mImgUrl = img;
        this.pl = pl;
    }

    public String getImgUrl() {
        return mImgUrl;
    }

}
