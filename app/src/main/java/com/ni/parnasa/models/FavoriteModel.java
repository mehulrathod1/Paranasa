package com.ni.parnasa.models;

/**
 * Created by vishal on 8/8/18.
 */

public class FavoriteModel {

    String id,name,image,rating;

    public FavoriteModel(String id, String name, String image, String rating) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.rating = rating;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}
