package inc.david.androidridesharenavigation.Adapter;


import android.widget.Button;
import android.widget.TextView;

public class CommentList {
    String postedBy = "";
    String commentText = "";
    Boolean showAcceptButton;
    String accepted = "";

    public String getAccepted() {
        return accepted;
    }

    public void setAccepted(String accepted) {
        this.accepted = accepted;
    }



    public TextView getStatusTextView() {
        return statusTextView;
    }

    public void setStatusTextView(TextView statusTextView) {
        this.statusTextView = statusTextView;
    }

    TextView statusTextView;

    public TextView getAcceptedTextView() {
        return acceptedTextView;
    }

    public void setAcceptedTextView(TextView acceptedTextView) {
        this.acceptedTextView = acceptedTextView;
    }

    TextView acceptedTextView;

    public Boolean getShowAcceptButton() {
        return showAcceptButton;
    }

    public void setShowAcceptButton(Boolean showAcceptButton) {
        this.showAcceptButton = showAcceptButton;
    }

    public Boolean getShowDeleteButton() {
        return showDeleteButton;
    }

    public void setShowDeleteButton(Boolean showDeleteButton) {
        this.showDeleteButton = showDeleteButton;
    }

    Boolean showDeleteButton;
    public Button getAcceptButton() {
        return acceptButton;
    }

    public void setAcceptButton(Button acceptButton) {
        this.acceptButton = acceptButton;
    }

    public Button getDeleteButton() {
        return this.deleteButton;
    }

    public void setDeleteButton(Button deleteButton) {
        this.deleteButton = deleteButton;
    }

    Button acceptButton;
    Button deleteButton;

    public String getPostedByIDText() {
        return postedByIDText;
    }

    public void setPostedByIDText(String postedByIDText) {
        this.postedByIDText = postedByIDText;
    }

    String postedByIDText = "";

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }



    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }


}
