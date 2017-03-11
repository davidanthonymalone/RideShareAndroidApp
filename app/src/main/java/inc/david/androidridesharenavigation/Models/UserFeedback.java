package inc.david.androidridesharenavigation.Models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by david on 18/02/17.
 */
@IgnoreExtraProperties
public class UserFeedback {
    private String comment, postedby;
    private double rating;

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public UserFeedback(){

    }

    public String getComment() {
        return comment;
    }

    public UserFeedback(String comment, String postedby, double rating) {
        this.comment = comment;
        this.postedby = postedby;
        this.rating = rating;
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
