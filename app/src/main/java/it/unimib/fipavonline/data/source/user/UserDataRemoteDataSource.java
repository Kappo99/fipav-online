package it.unimib.fipavonline.data.source.user;

import static it.unimib.fipavonline.util.Constants.FIREBASE_FAVORITE_CAMPIONATO_COLLECTION;
import static it.unimib.fipavonline.util.Constants.FIREBASE_REALTIME_DATABASE;
import static it.unimib.fipavonline.util.Constants.FIREBASE_USERS_COLLECTION;
import static it.unimib.fipavonline.util.Constants.SHARED_PREFERENCES_FILE_NAME;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.unimib.fipavonline.model.Campionato;
import it.unimib.fipavonline.model.User;
import it.unimib.fipavonline.util.SharedPreferencesUtil;

/**
 * Class that gets the user information using Firebase Realtime Database.
 */
public class UserDataRemoteDataSource extends BaseUserDataRemoteDataSource {

    private static final String TAG = UserDataRemoteDataSource.class.getSimpleName();

    private final DatabaseReference databaseReference;
    private final SharedPreferencesUtil sharedPreferencesUtil;

    public UserDataRemoteDataSource(SharedPreferencesUtil sharedPreferencesUtil) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(FIREBASE_REALTIME_DATABASE);
        databaseReference = firebaseDatabase.getReference().getRef();
        this.sharedPreferencesUtil = sharedPreferencesUtil;
    }

    @Override
    public void saveUserData(User user) {
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(user.getIdToken()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Log.d(TAG, "User already present in Firebase Realtime Database");
                    userResponseCallback.onSuccessFromRemoteDatabase(user);
                } else {
                    Log.d(TAG, "User not present in Firebase Realtime Database");
                    databaseReference.child(FIREBASE_USERS_COLLECTION).child(user.getIdToken()).setValue(user)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                userResponseCallback.onSuccessFromRemoteDatabase(user);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                userResponseCallback.onFailureFromRemoteDatabase(e.getLocalizedMessage());
                            }
                        });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                userResponseCallback.onFailureFromRemoteDatabase(error.getMessage());
            }
        });
    }

    @Override
    public void getUserFavoriteCampionato(String idToken) {
        databaseReference.child(FIREBASE_USERS_COLLECTION).child(idToken).
            child(FIREBASE_FAVORITE_CAMPIONATO_COLLECTION).get().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.d(TAG, "Error getting data", task.getException());
                    userResponseCallback.onFailureFromRemoteDatabase(task.getException().getLocalizedMessage());
                }
                else {
                    Log.d(TAG, "Successful read: " + task.getResult().getValue());

                    List<Campionato> campionatoList = new ArrayList<>();
                    for(DataSnapshot ds : task.getResult().getChildren()) {
                        Campionato campionato = ds.getValue(Campionato.class);
                        campionatoList.add(campionato);
                    }

                    userResponseCallback.onSuccessFromRemoteDatabase(campionatoList);
                }
            });
    }
}
