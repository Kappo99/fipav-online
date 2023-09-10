package it.unimib.fipavonline.data.source.news;

import static it.unimib.fipavonline.util.Constants.FIREBASE_FAVORITE_NEWS_COLLECTION;
import static it.unimib.fipavonline.util.Constants.FIREBASE_REALTIME_DATABASE;
import static it.unimib.fipavonline.util.Constants.FIREBASE_USERS_COLLECTION;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import it.unimib.fipavonline.model.Campionato;

/**
 * Class to get the user favorite news using Firebase Realtime Database.
 */
public class FavoriteNewsDataSource extends BaseFavoriteNewsDataSource {

    private static final String TAG = FavoriteNewsDataSource.class.getSimpleName();

    private final DatabaseReference databaseReference;
    private final String idToken;

    public FavoriteNewsDataSource(String idToken) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(FIREBASE_REALTIME_DATABASE);
        databaseReference = firebaseDatabase.getReference().getRef();
        this.idToken = idToken;
    }

    @Override
    public void getFavoriteNews() {
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).
            child(FIREBASE_FAVORITE_NEWS_COLLECTION).get().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.d(TAG, "Error getting data", task.getException());
                }
                else {
                    Log.d(TAG, "Successful read: " + task.getResult().getValue());

                    List<Campionato> campionatoList = new ArrayList<>();
                    for(DataSnapshot ds : task.getResult().getChildren()) {
                        Campionato campionato = ds.getValue(Campionato.class);
                        campionato.setSynchronized(true);
                        campionatoList.add(campionato);
                    }

                    newsCallback.onSuccessFromCloudReading(campionatoList);
                }
            });
    }

    @Override
    public void addFavoriteNews(Campionato campionato) {
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).
            child(FIREBASE_FAVORITE_NEWS_COLLECTION).child(String.valueOf(campionato.hashCode())).setValue(campionato)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    campionato.setSynchronized(true);
                    newsCallback.onSuccessFromCloudWriting(campionato);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    newsCallback.onFailureFromCloud(e);
                }
            });
    }

    @Override
    public void synchronizeFavoriteNews(List<Campionato> notSynchronizedCampionatoList) {
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).
            child(FIREBASE_FAVORITE_NEWS_COLLECTION).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    List<Campionato> campionatoList = new ArrayList<>();
                    for (DataSnapshot ds : task.getResult().getChildren()) {
                        Campionato campionato = ds.getValue(Campionato.class);
                        campionato.setSynchronized(true);
                        campionatoList.add(campionato);
                    }

                    campionatoList.addAll(notSynchronizedCampionatoList);

                    for (Campionato campionato : campionatoList) {
                        databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).
                            child(FIREBASE_FAVORITE_NEWS_COLLECTION).
                            child(String.valueOf(campionato.hashCode())).setValue(campionato).addOnSuccessListener(
                                    new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            campionato.setSynchronized(true);
                                        }
                                    }
                            );
                    }
                }
            });
    }

    @Override
    public void deleteFavoriteNews(Campionato campionato) {
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).
            child(FIREBASE_FAVORITE_NEWS_COLLECTION).child(String.valueOf(campionato.hashCode())).
            removeValue().addOnSuccessListener(aVoid -> {
                campionato.setSynchronized(false);
                newsCallback.onSuccessFromCloudWriting(campionato);
            }).addOnFailureListener(e -> {
                newsCallback.onFailureFromCloud(e);
            });
    }

    @Override
    public void deleteAllFavoriteNews() {
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).
            child(FIREBASE_FAVORITE_NEWS_COLLECTION).removeValue().addOnSuccessListener(aVoid -> {
                newsCallback.onSuccessFromCloudWriting(null);
            }).addOnFailureListener(e -> {
                newsCallback.onFailureFromCloud(e);
            });
    }
}
