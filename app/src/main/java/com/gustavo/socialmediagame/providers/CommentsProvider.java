package com.gustavo.socialmediagame.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.gustavo.socialmediagame.models.Comment;

public class CommentsProvider {

    CollectionReference mCollection;

    public CommentsProvider(){
        mCollection = FirebaseFirestore.getInstance().collection("Comments");
    }

    public Task<Void> create(Comment comment){
        return mCollection.document().set(comment);
    }

    public Query getCommentByPost(String id){

        return mCollection.whereEqualTo("idPost", id);
    }
}
