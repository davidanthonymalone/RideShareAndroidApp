package inc.david.androidridesharenavigation.Models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by david on 23/01/17.
 */
@IgnoreExtraProperties
public class Advert {
    private String leaving, image, goingTo, username, uid;


    public String getGoingTo() {
        return goingTo;
    }

    public void setGoingTo(String goingTo) {
        this.goingTo = goingTo;
    }

    public Advert() {

    }
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
    public String getLeaving() {
        return leaving;
    }

    public void setLeaving(String leaving) {
        this.leaving = leaving;
    }

    public String getTitle() {
        return goingTo;
    }

    public void setTitle(String title) {
        this.goingTo = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Advert(String leaving, String username, String title, String image, String uid) {
        this.leaving = leaving;
        this.username = username;
        this.goingTo = title;
        this.image = image;
        this.uid = uid;
    }
}