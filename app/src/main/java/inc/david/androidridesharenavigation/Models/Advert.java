package inc.david.androidridesharenavigation.Models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by david on 23/01/17.
 */
@IgnoreExtraProperties
public class Advert {
    private String comingFrom, goingTo, username,
            image, uid;
    private Map<String,String> comments = new HashMap<String, String>();
    private Map<String,String> likedBy = new HashMap<>();

    public Map<String, String> getComments() {
        return comments;
    }

    public void setComments(Map<String, String> comments) {
        this.comments = comments;
    }

    public Map<String, String> getLikedBy() {
        return likedBy;
    }

    public void setLikedBy(Map<String, String> likedBy) {
        this.likedBy = likedBy;
    }

    public Map<String, String> getUsersAccepted() {
        return usersAccepted;
    }

    public void setUsersAccepted(Map<String, String> usersAccepted) {
        this.usersAccepted = usersAccepted;
    }

    Map<String,String> usersAccepted = new HashMap<>();

    public long getGoingToLat() {
        return goingToLat;
    }

    public void setGoingToLat(long goingToLat) {
        this.goingToLat = goingToLat;
    }

    public long getGoingToLng() {
        return goingToLng;
    }

    public void setGoingToLng(long goingToLng) {
        this.goingToLng = goingToLng;
    }

    public long getComingFromLat() {
        return comingFromLat;
    }

    public void setComingFromLat(long comingFromLat) {
        this.comingFromLat = comingFromLat;
    }

    public long getComingFromLng() {
        return comingFromLng;
    }

    public void setComingFromLng(long comingFromLng) {
        this.comingFromLng = comingFromLng;
    }

    public int getSeatsRemaining() {
        return seatsRemaining;
    }

    public void setSeatsRemaining(int seatsRemaining) {
        this.seatsRemaining = seatsRemaining;
    }

    public int getNoOfSeats() {
        return noOfSeats;
    }

    public void setNoOfSeats(int noOfSeats) {
        this.noOfSeats = noOfSeats;
    }

    private long goingToLat, goingToLng, comingFromLat, comingFromLng;
    private int seatsRemaining, noOfSeats;


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
    public String getComingFrom() {
        return comingFrom;
    }

    public void setComingFrom(String comingFrom) {
        this.comingFrom = comingFrom;
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

    public Advert(String comingFrom, String username, String goingTo, String image, String uid,
                  long goingToLat, long goingToLng, long comingFromLat, long comingFromLng,
                  int seatsRemaining, int noOfSeats, Map<String,String> comments, Map<String,String> likedBy, Map<String,String> usersAccepted) {
        this.comingFrom = comingFrom;
        this.username = username;
        this.goingTo = goingTo;
        this.image = image;
        this.uid = uid;
        this.goingToLat = goingToLat;
        this.goingToLng = goingToLng;
        this.comingFromLat = comingFromLat;
        this.comingFromLng = comingFromLng;
        this.seatsRemaining = seatsRemaining;
        this.noOfSeats = noOfSeats;
        this.comments = comments;
        this.usersAccepted = usersAccepted;
        this.likedBy = likedBy;
    }
}