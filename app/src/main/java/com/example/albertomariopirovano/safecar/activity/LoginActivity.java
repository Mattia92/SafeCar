package com.example.albertomariopirovano.safecar.activity;

import android.content.Context;
import android.content.Intent;
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
import com.example.albertomariopirovano.safecar.firebase_model.User;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
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

/**
 * Created by albertomariopirovano on 14/03/17.
 */

public class LoginActivity extends AppCompatActivity implements Animation.AnimationListener, GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 007;
    private static final String TAG = LoginActivity.class.getSimpleName();
    private ImageView logo;
    private ImageView cover;
    private EditText emailEditText;
    private EditText pwdEditText;
    private TextView langTextView;
    private TextView forgotPwdTextView;
    private Button loginButton;
    private Button newAccountButton;
    private ImageView staticLogo;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private SignInButton btnSignIn;
    private GoogleApiClient mGoogleApiClient;
    private Boolean ANIMATION_ENDED = false;

    private DatabaseReference database;

    // converte il valore in dp in px
    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            Log.d(TAG, "No cached Google sign-in");
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
                        Log.d(TAG, "onStart:failed");
                        // [END_EXCLUDE]
                    }
                    //handleSignInResult(googleSignInResult);
                }
            });
        }
    }

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
                Log.d(TAG, "onActivityResult:failed");
                // [END_EXCLUDE]
            }
            //handleSignInResult(result);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
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
                            Log.d(TAG, "signInWithCredential:success");
                            final FirebaseUser user = auth.getCurrentUser();
                            database.child("users").orderByChild("authUID").equalTo(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        Log.d("firebaseAuthWithGoogle", "user already present, let's set active to true");
                                        database.child("users").child(auth.getCurrentUser().getUid()).child("active").setValue(true);
                                    } else {
                                        Log.d("firebaseAuthWithGoogle", "user not present, let's add him");
                                        //String userID = database.child("users").push().getKey();
                                        database.child("users").child(auth.getCurrentUser().getUid()).setValue(new User(user.getUid(), user.getDisplayName(), user.getEmail(), user.getPhotoUrl().toString()));
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            Log.d(TAG, user.getDisplayName() + "\n" + user.getEmail() + "\n" + user.getPhotoUrl());

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [START_EXCLUDE]
                        progressBar.setVisibility(View.GONE);
                        // [END_EXCLUDE]
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();

        if (auth.getCurrentUser() != null) {
            Log.d(TAG, "skip -> " + auth.getCurrentUser().getEmail() + " " + auth.getCurrentUser().getDisplayName());
            database.child("users").child(auth.getCurrentUser().getUid()).child("active").setValue(true);
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        } else {
            Log.d(TAG, "no skip");
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
                                    Log.d(TAG, "signin successful");
                                    database.child("users").child(auth.getCurrentUser().getUid()).child("active").setValue(true);
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
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
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            Log.d(TAG, "Signed in successfully, name: " + acct.getDisplayName());

            String personName = acct.getDisplayName();
            String personPhotoUrl = acct.getPhotoUrl().toString();
            String email = acct.getEmail();

            Log.d(TAG, "Name: " + personName + ", email: " + email
                    + ", Image: " + personPhotoUrl);

        } else {
            // Signed out, show unauthenticated UI.
            Log.d(TAG, "Signed out successfully");
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
}