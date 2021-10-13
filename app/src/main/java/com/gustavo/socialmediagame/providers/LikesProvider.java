package com.gustavo.socialmediagame.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.gustavo.socialmediagame.models.Like;

public class LikesProvider {

    CollectionReference mCollection;


    public  LikesProvider(){
        mCollection = FirebaseFirestore.getInstance().collection("Likes");
    }

    public Task<Void> create(Like like){
        DocumentReference document = mCollection.document();
        String id = document.getId();
        like.setId(id);
        return document.set(like);
    }

    public  Query getLikeByPost(String idPost){
            return mCollection.whereEqualTo("idPost", idPost);
    }

    public Query getLikeByPostAndUser(String idPost, String idUser ){
        return mCollection.whereEqualTo("idPost", idPost).whereEqualTo("idUser", idUser);
    }

    public  Task<Void> delete(String id){
        return mCollection.document(id).delete();
    }
}
