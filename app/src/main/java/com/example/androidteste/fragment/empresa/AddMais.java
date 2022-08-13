package com.example.androidteste.fragment.empresa;

import com.example.androidteste.helper.FirebaseHelper;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class AddMais {

    public static void salvar(List<String> addMaisList){
        DatabaseReference addMaisRef = FirebaseHelper.getDatabaseReference()
                .child("addMais")
                .child(FirebaseHelper.getIdFirebase());
        addMaisRef.setValue(addMaisList);

    }
}
