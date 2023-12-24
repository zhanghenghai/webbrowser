package com.one.browser.entity;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * @author 18517
 */
public class Certificate implements Serializable {

    private Bitmap image;

    public Certificate(Bitmap image) {
        this.image = image;

    }

    public Certificate() {

    }

    public Bitmap getImageView() {
        return image;
    }

    public void setImageView(Bitmap image) {
        this.image = image;
    }
}
