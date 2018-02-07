package com.example.albertomariopirovano.safecar.activity;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.albertomariopirovano.safecar.R;
import com.example.albertomariopirovano.safecar.concurrency.DownloadImage;
import com.example.albertomariopirovano.safecar.firebase_model.Plug;
import com.example.albertomariopirovano.safecar.firebase_model.Trip;
import com.example.albertomariopirovano.safecar.firebase_model.User;
import com.example.albertomariopirovano.safecar.realm_model.LocalModel;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by albertomariopirovano on 14/03/17.
 */

public class LoginActivity extends AppCompatActivity implements Animation.AnimationListener, GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 007;
    private static final String TAG = LoginActivity.class.getSimpleName();

    private LocalModel localModel = LocalModel.getInstance();

    private ImageView logo;
    private ImageView cover;
    private EditText emailEditText;
    private EditText pwdEditText;
    private TextView langTextView;
    private TextView forgotPwdTextView;
    private Button loginButton;
    private Button newAccountButton;
    private ImageView staticLogo;

    private ProgressBar progressBar;
    private SignInButton btnSignIn;
    private GoogleApiClient mGoogleApiClient;
    private Boolean ANIMATION_ENDED = false;

    private DatabaseReference database;
    private FirebaseAuth auth;

    private ContextWrapper cw;

    private File directory;
    private File profilePngFile;

    // converte il valore in dp in px
    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.i(TAG, "onConnectionFailed:" + connectionResult);
    }

    /*@Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.i(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            Log.i(TAG, "No cached Google sign-in");
            progressBar.setVisibility(View.VISIBLE);
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    progressBar.setVisibility(View.GONE);
                    if (googleSignInResult.isSuccess()) {
                        // Google Sign In was successful, authenticate with Firebase
                        GoogleSignInAccount account = googleSignInResult.getSignInAccount();
                        firebaseAuthWithGoogle(account);
                    } else {
                        // Google Sign In failed, update UI appropriately
                        // [START_EXCLUDE]
                        Log.i(TAG, "onStart:failed");
                        // [END_EXCLUDE]
                    }
                    //handleSignInResult(googleSignInResult);
                }
            });
        }
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // [START_EXCLUDE]
                Log.i(TAG, "onActivityResult:failed");
                // [END_EXCLUDE]
            }
            //handleSignInResult(result);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.i(TAG, "firebaseAuthWithGoogle - userID: " + acct.getId());
        // [START_EXCLUDE silent]
        progressBar.setVisibility(View.VISIBLE);
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.i(TAG, "firebaseAuthWithGoogle - [GOOGLE+ BASED] signin successfull");
                            final FirebaseUser user = auth.getCurrentUser();
                            database.child("users").orderByChild("authUID").equalTo(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        Log.i("firebaseAuthWithGoogle", "user already present, let's set active to true");
                                        database.child("users").child(auth.getCurrentUser().getUid()).child("active").setValue(true);
                                        Log.i(TAG, user.getDisplayName() + "\n" + user.getEmail() + "\n" + user.getPhotoUrl());
                                        initLocalDB(user);
                                    } else {
                                        Log.i("firebaseAuthWithGoogle", "user not present, let's add him");
                                        //String userID = database.child("users").push().getKey();
                                        database.child("users").child(auth.getCurrentUser().getUid()).setValue(new User(user.getUid(), user.getDisplayName(), user.getEmail(), user.getPhotoUrl().toString()));
                                        Log.i(TAG, user.getDisplayName() + "\n" + user.getEmail() + "\n" + user.getPhotoUrl().toString());
                                        initLocalDB(user);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void setUpGoogleAuth() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    public void cacheUserProfileImage(FirebaseUser currentUser) {

        Intent whereToGoNext = new Intent(LoginActivity.this, MainActivity.class);

        if (currentUser.getPhotoUrl() != null && !profilePngFile.exists()) {
            Log.i(TAG, "Caching user profile image");
            try {
                new DownloadImage(profilePngFile).execute(currentUser.getPhotoUrl().toString()).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        progressBar.setVisibility(View.GONE);

        Log.i("> Switching activity <", "Login Activity -> Main Activity");

        startActivity(whereToGoNext);
        finish();
    }

    private void initLocalDB(FirebaseUser currentUser) {

        Log.i(TAG, "Initializing local database. Downloading cloud data into a local copy");

        try {
            new UserHandler(currentUser).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();

        cw = new ContextWrapper(this.getApplicationContext());
        directory = cw.getDir("safecar", Context.MODE_PRIVATE);
        profilePngFile = new File(directory, "profile.png");

        if (auth.getCurrentUser() != null) {

            Log.i(TAG, "User already signed in, skipping autentication activity: User > email: " + auth.getCurrentUser().getEmail() + " > name:" + auth.getCurrentUser().getDisplayName());
            database.child("users").child(auth.getCurrentUser().getUid()).child("active").setValue(true);
            initLocalDB(auth.getCurrentUser());

        } else {

            Log.i(TAG, "Launching autentication activity");

        }

        setContentView(R.layout.activity_login);

        setUpGoogleAuth();

        logo = (ImageView) findViewById(R.id.logo);
        cover = (ImageView) findViewById(R.id.cover);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        pwdEditText = (EditText) findViewById(R.id.pswEditText);
        langTextView = (TextView) findViewById(R.id.langTextView);
        forgotPwdTextView = (TextView) findViewById(R.id.forgotPasswordTextView);
        loginButton = (Button) findViewById(R.id.loginButton);
        newAccountButton = (Button) findViewById(R.id.newAccountButton);
        staticLogo = (ImageView) findViewById(R.id.static_image_view);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        btnSignIn = (SignInButton) findViewById(R.id.btn_sign_in);

        btnSignIn.setVisibility(View.GONE);
        cover.setVisibility(View.GONE);
        emailEditText.setVisibility(View.GONE);
        pwdEditText.setVisibility(View.GONE);
        langTextView.setVisibility(View.GONE);
        forgotPwdTextView.setVisibility(View.GONE);
        loginButton.setVisibility(View.GONE);
        newAccountButton.setVisibility(View.GONE);
        staticLogo.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        Animation moveLogo = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_logo);
        moveLogo.setFillAfter(true);
        moveLogo.setAnimationListener(this);
        logo.startAnimation(moveLogo);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        forgotPwdTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });

        newAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                final String password = pwdEditText.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //authenticate user
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                progressBar.setVisibility(View.GONE);
                                if (!task.isSuccessful()) {
                                    // there was an error
                                    if (password.length() < 6) {
                                        pwdEditText.setError(getString(R.string.minimum_password));
                                    } else {
                                        Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Log.i(TAG, "onCreate - [PASSWORD BASED] signin successful");
                                    database.child("users").child(auth.getCurrentUser().getUid()).child("active").setValue(true);
                                    initLocalDB(auth.getCurrentUser());
                                }
                            }
                        });
            }
        });

        final View activityRootView = findViewById(R.id.constraint_layout);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(ANIMATION_ENDED) {
                    int heightDiff = activityRootView.getRootView().getHeight()-activityRootView.getHeight();
                    if(heightDiff > dpToPx(LoginActivity.this, 200)) {
                        //soft keyboard is shown
                        btnSignIn.setVisibility(View.GONE);
                        cover.setVisibility(View.GONE);
                        langTextView.setVisibility(View.GONE);
                        forgotPwdTextView.setVisibility(View.GONE);
                        staticLogo.setVisibility(View.VISIBLE);
                        newAccountButton.setVisibility(View.GONE);

                    } else {
                        // soft keyboard is hidden
                        cover.setVisibility(View.VISIBLE);
                        langTextView.setVisibility(View.VISIBLE);
                        forgotPwdTextView.setVisibility(View.VISIBLE);
                        btnSignIn.setVisibility(View.VISIBLE);
                        staticLogo.setVisibility(View.GONE);
                        newAccountButton.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.i(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            Log.i(TAG, "Signed in successfully, name: " + acct.getDisplayName());

            String personName = acct.getDisplayName();
            String personPhotoUrl = acct.getPhotoUrl().toString();
            String email = acct.getEmail();

            Log.i(TAG, "Name: " + personName + ", email: " + email
                    + ", Image: " + personPhotoUrl);

        } else {
            // Signed out, show unauthenticated UI.
            Log.i(TAG, "Signed out successfully");
            Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

        //staticLogo.setVisibility(View.VISIBLE);
        logo.clearAnimation();
        logo.setVisibility(View.GONE);

        emailEditText.setAlpha(0f);
        emailEditText.setVisibility(View.VISIBLE);
        pwdEditText.setAlpha(0f);
        pwdEditText.setVisibility(View.VISIBLE);
        langTextView.setAlpha(0f);
        langTextView.setVisibility(View.VISIBLE);
        forgotPwdTextView.setAlpha(0f);
        forgotPwdTextView.setVisibility(View.VISIBLE);
        loginButton.setAlpha(0f);
        loginButton.setVisibility(View.VISIBLE);
        newAccountButton.setAlpha(0f);
        newAccountButton.setVisibility(View.VISIBLE);
        cover.setAlpha(0f);
        cover.setVisibility(View.VISIBLE);
        btnSignIn.setAlpha(0f);
        btnSignIn.setVisibility(View.VISIBLE);

        int mediumAnimationTime = getResources().getInteger(android.R.integer.config_mediumAnimTime);

        emailEditText.animate()
                .alpha(1f)
                .setDuration(mediumAnimationTime)
                .setListener(null);
        pwdEditText.animate()
                .alpha(1f)
                .setDuration(mediumAnimationTime)
                .setListener(null);
        btnSignIn.animate()
                .alpha(1f)
                .setDuration(mediumAnimationTime)
                .setListener(null);
        langTextView.animate()
                .alpha(1f)
                .setDuration(mediumAnimationTime)
                .setListener(null);
        forgotPwdTextView.animate()
                .alpha(1f)
                .setDuration(mediumAnimationTime)
                .setListener(null);
        loginButton.animate()
                .alpha(1f)
                .setDuration(mediumAnimationTime)
                .setListener(null);
        newAccountButton.animate()
                .alpha(1f)
                .setDuration(mediumAnimationTime)
                .setListener(null);
        cover.animate()
                .alpha(1f)
                .setDuration(mediumAnimationTime)
                .setListener(null);

        ANIMATION_ENDED = true;
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }


    private class UserHandler extends AsyncTask<Void, Void, Void> {

        private final FirebaseUser user;

        public UserHandler(FirebaseUser user) {
            this.user = user;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            Log.i(TAG, "UserHandler - starting user related data download task");

            Log.i(TAG, "> uid:" + user.getUid());

            database.child("users").orderByChild("authUID").equalTo(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot dataSnapshot) {

                    Log.i(TAG, "UserHandler -[1]- downloading user profile from cloud firebase database");
                    int i = 0;
                    for (DataSnapshot snap : dataSnapshot.getChildren()) {
                        if (i == 0) {
                            localModel.setUser(snap.getValue(User.class));
                            i++;
                        }
                    }
                    Log.i(TAG, localModel.getUser().toString());

                    database.child("trips").orderByChild("userId").equalTo(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(final DataSnapshot dataSnapshot) {

                            Log.i(TAG, "UserHandler -[2]- downloading user trips from cloud firebase database");
                            ArrayList<Trip> tripList = new ArrayList<Trip>();
                            for (DataSnapshot parsedTrip : dataSnapshot.getChildren()) {
                                Trip trip = parsedTrip.getValue(Trip.class);
                                trip.setTripId(parsedTrip.getKey());
                                Log.i(TAG, trip.toString());
                                tripList.add(trip);
                            }

                            Log.i(TAG, "> Cloud trip-list is empty: " + String.valueOf(tripList.isEmpty()));

                            localModel.setTrips(tripList);

                            Log.i(TAG, "> Realm local trip-list is empty: " + String.valueOf(localModel.getTrips().isEmpty()));

                            database.child("plugs").orderByChild("userId").equalTo(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(final DataSnapshot dataSnapshot) {

                                    Log.i(TAG, "UserHandler -[3]- downloading user plugs from cloud firebase database");
                                    ArrayList<Plug> plugList = new ArrayList<Plug>();
                                    for (DataSnapshot parsedPlug : dataSnapshot.getChildren()) {
                                        Plug plug = parsedPlug.getValue(Plug.class);
                                        plug.setPlugId(parsedPlug.getKey());
                                        Log.i(TAG, plug.toString());
                                        plugList.add(plug);
                                    }

                                    Log.i(TAG, "> Cloud plug-list is empty: " + String.valueOf(plugList.isEmpty()));

                                    localModel.setPlugs(plugList);

                                    Log.i(TAG, "> Realm local plug-list is empty: " + String.valueOf(localModel.getPlugs().isEmpty()));

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