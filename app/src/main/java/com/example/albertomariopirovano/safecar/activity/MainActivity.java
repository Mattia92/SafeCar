package com.example.albertomariopirovano.safecar.activity;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.albertomariopirovano.safecar.R;
import com.example.albertomariopirovano.safecar.adapters.NavListAdapter;
import com.example.albertomariopirovano.safecar.concurrency.DownloadImage;
import com.example.albertomariopirovano.safecar.fragments.HomeFragment;
import com.example.albertomariopirovano.safecar.fragments.ProfileFragment;
import com.example.albertomariopirovano.safecar.fragments.SettingsFragment;
import com.example.albertomariopirovano.safecar.fragments.ShareFragment;
import com.example.albertomariopirovano.safecar.model.NavItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private DrawerLayout drawerLayout;
    private RelativeLayout drawerPane;
    private ListView lvNav;

    private List<NavItem> listNavItems;
    private List<Fragment> listFragments;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private TextView nameTextView;
    private TextView emailTextView;
    private ImageView iconImageView;

    private File directory;
    private File profilePngFile;

    private ContextWrapper cw;

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener logout_listener;
    private FirebaseUser currentUser;

    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        auth = FirebaseAuth.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance().getReference();

        addLogoutListener(auth);
        handleDrawerProfileDetails();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerPane = (RelativeLayout) findViewById(R.id.drawer_pane);
        lvNav = (ListView) findViewById(R.id.nav_list);

        listNavItems = new ArrayList<NavItem>();

        listNavItems.add(new NavItem("Home", "Home page", R.drawable.ic_home_black_24dp));
        listNavItems.add(new NavItem("Profile", "Modify your profile", R.drawable.ic_person_black_24dp));
        listNavItems.add(new NavItem("Share", "Share app", R.drawable.ic_share_black_24dp));
        listNavItems.add(new NavItem("Settings", "Change settings", R.drawable.ic_settings_black_24dp));

        NavListAdapter navListAdapter = new NavListAdapter(getApplicationContext(), R.layout.item_nav_list, listNavItems);

        lvNav.setAdapter(navListAdapter);

        listFragments = new ArrayList<>();
        listFragments.add(new HomeFragment());
        listFragments.add(new ProfileFragment());
        listFragments.add(new ShareFragment());
        listFragments.add(new SettingsFragment());

        //Load first fragment as default
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main_content, listFragments.get(0)).commit();

        setTitle(listNavItems.get(0).getTitle());
        lvNav.setItemChecked(0, true);
        drawerLayout.closeDrawer(drawerPane);

        //Set listener for navigation items
        lvNav.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //Replace the fragment with the selection correspondingly
                FragmentManager fragmentManager = getSupportFragmentManager();

                fragmentManager.beginTransaction().replace(R.id.main_content, listFragments.get(position)).commit();

                setTitle(listNavItems.get(position).getTitle());
                lvNav.setItemChecked(position, true);
                drawerLayout.closeDrawer(drawerPane);
            }
        });

        //Create listener for drawer layout
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_opened, R.string.drawer_closed) {

            @Override
            public void onDrawerClosed(View drawerView) {
                invalidateOptionsMenu();
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu();
                super.onDrawerOpened(drawerView);
            }
        };

        drawerLayout.setDrawerListener(actionBarDrawerToggle);

    }

    private void handleDrawerProfileDetails() {

        nameTextView = (TextView) findViewById(R.id.nameTextView);
        emailTextView = (TextView) findViewById(R.id.emailTextView);
        iconImageView = (ImageView) findViewById(R.id.icon);

        if (!TextUtils.isEmpty(currentUser.getDisplayName())) {
            nameTextView.setText(currentUser.getDisplayName());
        } else {
            nameTextView.setText("No name provided");
        }

        if (!TextUtils.isEmpty(currentUser.getEmail())) {
            emailTextView.setText(currentUser.getEmail());
        } else {
            emailTextView.setText("No email provided");
        }

        cw = new ContextWrapper(this.getApplicationContext());
        directory = cw.getDir("safecar", Context.MODE_PRIVATE);
        profilePngFile = new File(directory, "profile.png");

        if (currentUser.getPhotoUrl() != null && !profilePngFile.exists()) {
            Log.d(TAG, "download profile image");
            new DownloadImage(profilePngFile).execute(currentUser.getPhotoUrl().toString());
        }

        if (currentUser.getPhotoUrl() != null) {
            Log.d(TAG, "load profile image");
            try {
                iconImageView.setImageBitmap(BitmapFactory.decodeStream(new FileInputStream(profilePngFile)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "load standard profile image");
            iconImageView.setImageResource(R.drawable.user);
        }
    }

    public void addLogoutListener(FirebaseAuth a) {
        // this listener will be called when there is change in firebase user session
        logout_listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                System.out.println("INSIDE !");
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        a.addAuthStateListener(logout_listener);
    }

    public void setEnabledNavigationDrawer(boolean isEnabled) {
        actionBarDrawerToggle.setDrawerIndicatorEnabled(isEnabled);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.d("MainActivity", "onOptionsItemSelected");

        switch (item.getItemId()) {

            case R.id.menu_item_logout:

                System.out.println("Logout current user");

                database.child("users").child(auth.getCurrentUser().getUid()).child("active").setValue(false);
                auth.signOut();

                if (profilePngFile.exists()) {
                    if (profilePngFile.delete()) {
                        System.out.println("file Deleted :" + profilePngFile.getPath());
                    } else {
                        System.out.println("file not Deleted :" + profilePngFile.getPath());
                    }
                }

                return false;

            case R.id.menu_item_delete_account:

                System.out.println("Delete current user");

                FirebaseUser user = auth.getCurrentUser();

                database.child("users").child(user.getUid()).setValue(null);

                if (user != null) {
                    user.delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(MainActivity.this, "Your profile is deleted:( Create a account now!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(MainActivity.this, "Failed to delete your account!", Toast.LENGTH_SHORT).show();
                                    }
                                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                }
                            });
                } else {
                    System.out.println("Your are trying to eliminate an account while you already performed logout !");
                }

                if (profilePngFile.exists()) {
                    if (profilePngFile.delete()) {
                        System.out.println("file Deleted :" + profilePngFile.getPath());
                    } else {
                        System.out.println("file not Deleted :" + profilePngFile.getPath());
                    }
                }

                return true;

            default:

                System.out.println("DEFAULT CASE !");

                if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
                    return true;
                }
                onBackPressed();
                setEnabledNavigationDrawer(true);
                setTitle(getString(R.string.action_settings));

                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }
}
