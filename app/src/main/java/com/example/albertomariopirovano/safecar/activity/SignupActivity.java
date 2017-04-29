package com.example.albertomariopirovano.safecar.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.albertomariopirovano.safecar.R;
import com.example.albertomariopirovano.safecar.firebase_model.Plug;
import com.example.albertomariopirovano.safecar.firebase_model.Trip;
import com.example.albertomariopirovano.safecar.firebase_model.User;
import com.example.albertomariopirovano.safecar.realm_model.LocalModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = SignupActivity.class.getSimpleName();
    private LocalModel localModel = LocalModel.getInstance();

    private EditText inputEmail, inputPassword;
    private Button btnSignIn, btnSignUp;
    private ProgressBar progressBar;
    private FirebaseAuth auth;

    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();

        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        inputEmail = (EditText) findViewById(R.id.email_signup);
        inputPassword = (EditText) findViewById(R.id.pwd_signup);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                //create user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(SignupActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                //progressBar.setVisibility(View.GONE);
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(SignupActivity.this, "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.d("createUserPassword", "userId = " + task.getResult().getUser().getUid());
                                    //String userID = database.child("users").push().getKey();
                                    database.child("users").child(auth.getCurrentUser().getUid()).setValue(new User(task.getResult().getUser().getUid(), task.getResult().getUser().getEmail()));
                                    initLocalDB(auth.getCurrentUser());
                                    startActivity(new Intent(SignupActivity.this, MainActivity.class));
                                    finish();
                                }
                            }
                        });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    public void cacheUserProfileImage(FirebaseUser currentUser) {

        Intent whereToGoNext = new Intent(SignupActivity.this, MainActivity.class);

        progressBar.setVisibility(View.GONE);

        startActivity(whereToGoNext);
        finish();
    }

    private void initLocalDB(FirebaseUser currentUser) {

        Log.d(TAG, "initLocalDb");

        try {
            new UserHandler(currentUser).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private class UserHandler extends AsyncTask<Void, Void, Void> {

        private final FirebaseUser user;

        public UserHandler(FirebaseUser user) {
            this.user = user;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            Log.d(TAG, "UserHandler - doInBackground");

            Log.d(TAG, user.getUid());

            database.child("users").orderByChild("authUID").equalTo(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot dataSnapshot) {

                    Log.d(TAG, "UserHandler - doInBackground user localModel");
                    int i = 0;
                    for (DataSnapshot snap : dataSnapshot.getChildren()) {
                        if (i == 0) {
                            localModel.setUser(snap.getValue(User.class));
                            i++;
                        }
                    }

                    database.child("trips").orderByChild("userId").equalTo(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(final DataSnapshot dataSnapshot) {

                            Log.d(TAG, "UserHandler - doInBackground user localTrips");
                            ArrayList<Trip> tripList = new ArrayList<Trip>();
                            for (DataSnapshot parsedTrip : dataSnapshot.getChildren()) {
                                Trip trip = parsedTrip.getValue(Trip.class);
                                tripList.add(trip);
                            }

                            localModel.setTrips(tripList);

                            database.child("pulgs").orderByChild("userId").equalTo(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(final DataSnapshot dataSnapshot) {

                                    Log.d(TAG, "UserHandler - doInBackground user localPlugs");
                                    ArrayList<Plug> plugList = new ArrayList<Plug>();
                                    for (DataSnapshot parsedPlug : dataSnapshot.getChildren()) {
                                        Plug plug = parsedPlug.getValue(Plug.class);
                                        plugList.add(plug);
                                    }

                                    localModel.setPlugs(plugList);

                                    cacheUserProfileImage(user);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
            return null;
        }
    }

}
