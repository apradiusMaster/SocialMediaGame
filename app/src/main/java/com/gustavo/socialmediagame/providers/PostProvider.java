package com.gustavo.socialmediagame.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.gustavo.socialmediagame.models.Post;

public class PostProvider {

    CollectionReference mCollection;

    public  PostProvider(){
        mCollection = FirebaseFirestore.getInstance().collection("posts");
    }

    public Task<Void> save(Post post){
        return  mCollection.document().set(post);
    }
}