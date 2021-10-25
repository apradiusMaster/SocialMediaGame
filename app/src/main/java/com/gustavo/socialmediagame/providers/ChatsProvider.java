package com.gustavo.socialmediagame.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.gustavo.socialmediagame.models.Chat;

import java.util.ArrayList;

public class ChatsProvider {

    CollectionReference mCollection;

    public ChatsProvider(){
        mCollection = FirebaseFirestore.getInstance().collection("Chats");
    }

    public void create(Chat chat){
       /* mCollection.document(chat.getIdUser1()).collection("Users").document(chat.getIdUser2()).set(chat);
        mCollection.document(chat.getIdUser2()).collection("Users").document(chat.getIdUser1()).set(chat); */
        mCollection.document(chat.getIdUser1() + chat.getIdUser2()).set(chat);
    }

    public Query getAll(String idUser){
       // return  mCollection.document(idUser).collection("Users");
        return mCollection.whereArrayContains("ids", idUser);
    }

    public  Query getChatByUser1AndUser2(String idUser1, String iduser2){

        ArrayList<String> ids = new ArrayList<>();
        ids.add(idUser1 + iduser2);
        ids.add(iduser2 + idUser1);
        return mCollection.whereIn("id", ids);
    }
}
