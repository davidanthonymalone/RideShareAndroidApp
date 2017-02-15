package inc.david.androidridesharenavigation.Models;

/**
 * Created by david on 23/01/17.
 */

public class Advert {
    private String desc, image, title, username;

    public Advert() {

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

    public Advert(String desc, String username, String title, String image) {
        this.desc = desc;
        this.username = username;
        this.title = title;
        this.image = image;
    }
}