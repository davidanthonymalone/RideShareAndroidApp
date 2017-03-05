package inc.david.androidridesharenavigation.Models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by david on 18/02/17.
 */
@IgnoreExtraProperties
public class Comment {
    private String comment, postedby;

    public Comment(){

    }

    public String getComment() {
        return comment;
    }

    public Comment(String comment, String postedby) {
        this.comment = comment;
        this.postedby = postedby;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPostedby() {
        return postedby;
    }

    public void setPostedby(String postedby) {
        this.postedby = postedby;
    }
}
