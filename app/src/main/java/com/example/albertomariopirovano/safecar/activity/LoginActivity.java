package com.example.albertomariopirovano.safecar.activity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.animation.Animation;
import android.content.Context;
import android.widget.Toast;

import com.example.albertomariopirovano.safecar.R;
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
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


/**
 * Created by albertomariopirovano on 14/03/17.
 */

public class LoginActivity extends AppCompatActivity implements Animation.AnimationListener,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private ImageView logo;
    private ImageView cover;
    private EditText emailEditText;
    private EditText pwdEditText;
    private TextView langTextView;
    private TextView forgotPwdTextView;
    private Button loginButton;
    private Button newAccountButton;
    private ImageView staticLogo;
    private GoogleApiClient mGoogleApiClient;
    private SignInButton signInButton;
    private Boolean ANIMATION_ENDED = false;

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;

    private FirebaseAuth authVariable;
    private FirebaseAuth.AuthStateListener authListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initAnimation();

        authVariable = FirebaseAuth.getInstance();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // [START_EXCLUDE]
                //updateUI(user);
                // [END_EXCLUDE]
            }
        };

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }

    @Override
    public void onStart() {
        super.onStart();
        authVariable.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            authVariable.removeAuthStateListener(authListener);
        }
    }


    public void signInEmailPW(String email, String pw) {
        System.out.print("signInWithEmailAndPw ");
        System.out.print(email + " ");
        System.out.print(pw + "\n");

        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        //showProgressDialog();

        AuthCredential credential = EmailAuthProvider.getCredential(email,pw);
        // [START sign_in_with_email]
        authVariable.signInWithEmailAndPassword(email, pw)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(LoginActivity.this, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            System.out.print("Authentication successful with email and pwd");
                        }
                        Intent i = new Intent(LoginActivity.this, MainActivity.class);

                        startActivity(i);

                        //hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });

    }

    private void initAnimation() {

        logo = (ImageView) findViewById(R.id.logo);
        cover = (ImageView) findViewById(R.id.cover);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        pwdEditText = (EditText) findViewById(R.id.pswEditText);
        langTextView = (TextView) findViewById(R.id.langTextView);
        forgotPwdTextView = (TextView) findViewById(R.id.forgotPasswordTextView);
        loginButton = (Button) findViewById(R.id.loginButton);
        newAccountButton = (Button) findViewById(R.id.newAccountButton);
        staticLogo = (ImageView) findViewById(R.id.static_image_view);
        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setVisibility(View.GONE);
        cover.setVisibility(View.GONE);
        emailEditText.setVisibility(View.GONE);
        pwdEditText.setVisibility(View.GONE);
        langTextView.setVisibility(View.GONE);
        forgotPwdTextView.setVisibility(View.GONE);
        loginButton.setVisibility(View.GONE);
        newAccountButton.setVisibility(View.GONE);
        staticLogo.setVisibility(View.GONE);

        findViewById(R.id.loginButton).setOnClickListener(this);
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.newAccountButton).setOnClickListener(this);

        Animation moveLogo = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_logo);
        moveLogo.setFillAfter(true);
        moveLogo.setAnimationListener(this);
        logo.startAnimation(moveLogo);

        final View activityRootView = findViewById(R.id.constraint_layout);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(ANIMATION_ENDED) {
                    int heightDiff = activityRootView.getRootView().getHeight()-activityRootView.getHeight();
                    if(heightDiff > dpToPx(LoginActivity.this, 200)) {
                        //soft keyboard is shown
                        cover.setVisibility(View.GONE);
                        langTextView.setVisibility(View.GONE);
                        forgotPwdTextView.setVisibility(View.GONE);
                        staticLogo.setVisibility(View.VISIBLE);
                    } else {
                        // soft keyboard is hidden
                        cover.setVisibility(View.VISIBLE);
                        langTextView.setVisibility(View.VISIBLE);
                        forgotPwdTextView.setVisibility(View.VISIBLE);
                        staticLogo.setVisibility(View.GONE);
                    }
                }
            }
        });
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
        signInButton.setAlpha(0f);
        signInButton.setVisibility(View.VISIBLE);

        int mediumAnimationTime = getResources().getInteger(android.R.integer.config_mediumAnimTime);

        emailEditText.animate()
                .alpha(1f)
                .setDuration(mediumAnimationTime)
                .setListener(null);
        pwdEditText.animate()
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
        signInButton.animate()
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

    // converte il valore in dp in px
    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signInGoogle();
                break;
            case R.id.loginButton:
                signInEmailPW(emailEditText.getText().toString(), pwdEditText.getText().toString());
            case R.id.newAccountButton:
                createAccountEmailPwd(emailEditText.getText().toString(), pwdEditText.getText().toString());
        }
    }

    private void createAccountEmailPwd(String email, String pwd) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        //showProgressDialog();

        // [START create_user_with_email]
        authVariable.createUserWithEmailAndPassword(email, pwd)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        }

                        // [START_EXCLUDE]
                        //hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }


    private void signInGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
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
                //updateUI(null);
                // [END_EXCLUDE]
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        //showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        authVariable.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        System.out.print("Authentication successful with google");

                        Intent i = new Intent(LoginActivity.this, MainActivity.class);

                        startActivity(i);

                        // [START_EXCLUDE]
                        //hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private boolean validateForm() {
        boolean valid = true;

        String email = emailEditText.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Required.");
            valid = false;
        } else {
            emailEditText.setError(null);
        }

        String password = pwdEditText.getText().toString();
        if (TextUtils.isEmpty(password)) {
            pwdEditText.setError("Required.");
            valid = false;
        } else {
            pwdEditText.setError(null);
        }

        return valid;
    }
}