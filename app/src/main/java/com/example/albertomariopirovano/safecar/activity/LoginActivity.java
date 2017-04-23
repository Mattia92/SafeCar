package com.example.albertomariopirovano.safecar.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by albertomariopirovano on 14/03/17.
 */

public class LoginActivity extends AppCompatActivity implements Animation.AnimationListener {

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

    private Boolean ANIMATION_ENDED = false;

    // converte il valore in dp in px
    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            System.out.println("skip" + auth.getCurrentUser());
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        setContentView(R.layout.activity_login);

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
