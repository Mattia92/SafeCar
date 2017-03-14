package com.example.albertomariopirovano.safecar.activity;

import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
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

import com.example.albertomariopirovano.safecar.R;

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

    private Boolean ANIMATION_ENDED = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        cover.setVisibility(View.GONE);
        emailEditText.setVisibility(View.GONE);
        pwdEditText.setVisibility(View.GONE);
        langTextView.setVisibility(View.GONE);
        forgotPwdTextView.setVisibility(View.GONE);
        loginButton.setVisibility(View.GONE);
        newAccountButton.setVisibility(View.GONE);
        staticLogo.setVisibility(View.GONE);

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

    // converte il valore in dp in px
    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

}
