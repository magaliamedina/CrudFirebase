package com.example.crudfirebase;

import com.google.firebase.database.FirebaseDatabase;

// android.app.Application: privilegios en ejecutarse primer
public class MyFirebaseApp extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true); //persistencia de datos- para cuando no hay conexion
    }
}
