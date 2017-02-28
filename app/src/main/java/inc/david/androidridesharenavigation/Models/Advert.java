package inc.david.androidridesharenavigation.Models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by david on 23/01/17.
 */
@IgnoreExtraProperties
public class Advert {
    private String desc, image, title, username, uid;



    public Advert() {

    }
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public Advert(String desc, String username, String title, String image, String uid) {
        this.desc = desc;
        this.username = username;
        this.title = title;
        this.image = image;
        this.uid = uid;
    }
}