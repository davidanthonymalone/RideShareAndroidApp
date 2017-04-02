package inc.david.androidridesharenavigation.Models;

import android.util.StringBuilderPrinter;
import android.widget.Button;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by david on 18/02/17.
 */
@IgnoreExtraProperties
public class Comment {
    private String commentText;
    private String postedBy;
    private String postedByIDText;
    private String accepted;
    private Button acceptButton;
    private Button deleteButton;

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }


    public String getAccepted() {
        return accepted;
    }

    public void setAccepted(String accepted) {
        this.accepted = accepted;
    }

    public Comment(){

    }

    public Comment(String commentText, String postedBy, String postedByIDText, String accepted) {
        this.commentText = commentText;
        this.postedBy = postedBy;
        this.postedByIDText = postedByIDText;
        this.accepted = accepted;
    }

    public Button getDeleteButton() {
        return deleteButton;
    }

    public void setDeleteButton(Button deleteButton) {
        this.deleteButton = deleteButton;
    }

    public Button getAcceptButton() {
        return acceptButton;
    }

    public void setAcceptButton(Button acceptButton) {
        this.acceptButton = acceptButton;
    }

    public String getCommentText() {
        return commentText;
    }

    public String getPostedByIDText() {
        return postedByIDText;
    }

    public void setPostedByIDText(String postedByIDText) {
        this.postedByIDText = postedByIDText;
    }


    public void setComment(String comment) {
        this.commentText = comment;
    }

    public String getPostedby() {
        return postedBy;
    }

    public void setPostedby(String postedBy) {
        this.postedBy = postedBy;
    }
}
