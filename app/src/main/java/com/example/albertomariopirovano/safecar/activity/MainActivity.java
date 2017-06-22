package com.example.albertomariopirovano.safecar.activity;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
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
import com.example.albertomariopirovano.safecar.firebase_model.Plug;
import com.example.albertomariopirovano.safecar.firebase_model.Trip;
import com.example.albertomariopirovano.safecar.firebase_model.User;
import com.example.albertomariopirovano.safecar.fragments.HomeFragment;
import com.example.albertomariopirovano.safecar.fragments.ProfileFragment;
import com.example.albertomariopirovano.safecar.fragments.SettingsFragment;
import com.example.albertomariopirovano.safecar.fragments.ShareFragment;
import com.example.albertomariopirovano.safecar.inner.fragments.TAGInterface;
import com.example.albertomariopirovano.safecar.realm_model.LocalModel;
import com.example.albertomariopirovano.safecar.realm_model.NavItem;
import com.example.albertomariopirovano.safecar.services.SavedStateHandler;
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

    private ContextWrapper cw;

    private File directory;
    private File profilePngFile;

    private List<NavItem> listNavItems;
    private List<Fragment> listFragments;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private TextView nameTextView;
    private TextView emailTextView;
    private ImageView iconImageView;

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener logout_listener;
    private DatabaseReference database;


    private LocalModel localModel = LocalModel.getInstance();
    private SavedStateHandler savedStateHandler = SavedStateHandler.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cw = new ContextWrapper(this.getApplicationContext());
        directory = cw.getDir("safecar", Context.MODE_PRIVATE);
        profilePngFile = new File(directory, "profile.png");

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerPane = (RelativeLayout) findViewById(R.id.drawer_pane);
        lvNav = (ListView) findViewById(R.id.nav_list);

        addLogoutListener(auth);

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
        fragmentManager.beginTransaction().replace(R.id.main_content, listFragments.get(0), ((TAGInterface) listFragments.get(0)).getAssignedTag()).commit();

        setTitle(listNavItems.get(0).getTitle());
        lvNav.setItemChecked(0, true);
        drawerLayout.closeDrawer(drawerPane);

        //Set listener for navigation items
        lvNav.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //Replace the fragment with the selection correspondingly
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                Log.d(TAG, ((TAGInterface) listFragments.get(position)).getAssignedTag());

                transaction.replace(R.id.main_content, listFragments.get(position), ((TAGInterface) listFragments.get(position)).getAssignedTag()).commit();

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

        testRealmDB();

        handleDrawerProfileDetails();

    }


    private void testRealmDB() {

        Log.d(TAG, "testRealmDB - testing Realm cache");

        User u = localModel.getUser();

        Log.d(TAG, u.toString());
        Log.d(TAG, "testRealmDB - 1] local user cached successfully");


        if (profilePngFile.exists()) {
            Log.d(TAG, "testRealmDB - 2.0] " + profilePngFile.getAbsolutePath() + " - exists");
            Log.d(TAG, "testRealmDB - 2.1] local user-profile image cached");
        } else {
            Log.d(TAG, "testRealmDB - 2.0] " + profilePngFile.getAbsolutePath() + " - doesn't exist");
        }

        if (localModel.getTrips() != null) {
            for (Trip t : localModel.getTrips()) {
                Log.d(TAG, t.toString());
            }
            Log.d(TAG, "testRealmDB - 3] local trips cached");
        }
        if (localModel.getPlugs() != null) {
            for (Plug p : localModel.getPlugs()) {
                Log.d(TAG, p.toString());
            }
            Log.d(TAG, "testRealmDB - 4] local plugs cached");
        }
    }

    private void handleDrawerProfileDetails() {

        nameTextView = (TextView) findViewById(R.id.nameTextView);
        emailTextView = (TextView) findViewById(R.id.emailTextView);
        iconImageView = (ImageView) findViewById(R.id.icon);

        if (!TextUtils.isEmpty(localModel.getUser().name)) {
            nameTextView.setText(localModel.getUser().name);
        } else {
            nameTextView.setText("No name provided");
        }

        if (!TextUtils.isEmpty(localModel.getUser().email)) {
            emailTextView.setText(localModel.getUser().email);
        } else {
            emailTextView.setText("No email provided");
        }

        if (localModel.getUser().photoURL == null) {
            Log.d(TAG, "handleDrawerProfileDetails - load standard profile image");
            iconImageView.setImageResource(R.drawable.user);
        } else {
            Log.d(TAG, "handleDrawerProfileDetails - load Google+ profile image");
            try {
                iconImageView.setImageBitmap(BitmapFactory.decodeStream(new FileInputStream(profilePngFile)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

    public void addLogoutListener(FirebaseAuth a) {
        // this listener will be called when there is change in firebase user session
        logout_listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                Log.d(TAG, "addLogoutListener");
                if (user == null) {
                    Log.d(TAG, "addLogoutListener - null user");
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

                database.child("users").child(auth.getCurrentUser().getUid()).child("active").setValue(false);

                if (profilePngFile.exists()) {
                    if (profilePngFile.delete()) {
                        Log.d(TAG, "file Deleted :" + profilePngFile.getPath());
                    } else {
                        Log.d(TAG, "file not Deleted :" + profilePngFile.getPath());
                    }
                }

                auth.signOut();

                localModel.updateCloudModel();
                localModel.drop();

                savedStateHandler.removeState("TabHome");
                savedStateHandler.removeTargetPlug();

                Log.d(TAG, "Logout current user");

                startActivity(new Intent(MainActivity.this, LoginActivity.class));

                return false;

            case R.id.menu_item_delete_account:

                FirebaseUser user = auth.getCurrentUser();

                if (profilePngFile.exists()) {
                    if (profilePngFile.delete()) {
                        Log.d(TAG, "file Deleted :" + profilePngFile.getPath());
                    } else {
                        Log.d(TAG, "file not Deleted :" + profilePngFile.getPath());
                    }
                }

                database.child("users").child(user.getUid()).setValue(null);

                if (user != null) {
                    user.delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(MainActivity.this, "Your profile is deleted!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(MainActivity.this, "Failed to delete your account!", Toast.LENGTH_SHORT).show();
                                    }
                                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                }
                            });
                } else {
                    Log.d(TAG, "Your are trying to eliminate an account while you already performed logout !");
                }

                localModel.drop();

                Log.d(TAG, "Delete current user");

                return true;

            default:

                Log.d(TAG, "DEFAULT CASE !");

                if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
                    return true;
                }

                Fragment f = getSupportFragmentManager().findFragmentByTag("ManageplugsFragment");
                if (f != null && f.isVisible()) {
                    Log.d(TAG, "ManageplugsFragment VISIBLE");
                    onBackPressed();
                    setTitle("Smart Objects");
                    return super.onOptionsItemSelected(item);
                }
                f = getSupportFragmentManager().findFragmentByTag("PairPlugFragment");
                if (f != null && f.isVisible()) {
                    Log.d(TAG, "PairPlugFragment VISIBLE");
                    onBackPressed();
                    setTitle("Smart Objects");
                    return super.onOptionsItemSelected(item);
                }
                f = getSupportFragmentManager().findFragmentByTag("SettingsPlugs");
                if (f != null && f.isVisible()) {
                    Log.d(TAG, "SettingsPlugs VISIBLE");
                    onBackPressed();
                    setEnabledNavigationDrawer(true);
                    setTitle(getString(R.string.action_settings));
                    return super.onOptionsItemSelected(item);
                }
                f = getSupportFragmentManager().findFragmentByTag("SettingsNotificationFragment");
                if (f != null && f.isVisible()) {
                    Log.d(TAG, "SettingsNotificationFragment VISIBLE");
                    onBackPressed();
                    setEnabledNavigationDrawer(true);
                    setTitle(getString(R.string.action_settings));
                    return super.onOptionsItemSelected(item);
                }
                f = getSupportFragmentManager().findFragmentByTag("SettingsShareFragment");
                if (f != null && f.isVisible()) {
                    Log.d(TAG, "SettingsShareFragment VISIBLE");
                    onBackPressed();
                    setEnabledNavigationDrawer(true);
                    setTitle(getString(R.string.action_settings));
                    return super.onOptionsItemSelected(item);
                }
                f = getSupportFragmentManager().findFragmentByTag("ReportFragment");
                if (f != null && f.isVisible()) {
                    Log.d(TAG, "ReportFragment VISIBLE");
                    onBackPressed();
                    setEnabledNavigationDrawer(true);
                    setTitle("Home");
                    return super.onOptionsItemSelected(item);
                }

                onBackPressed();
                setEnabledNavigationDrawer(true);
                setTitle("Home");
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {

        Fragment f = getSupportFragmentManager().findFragmentByTag("SettingsFragment");
        if (f != null && f.isVisible()) {
            Log.d(TAG, "SettingsFragment VISIBLE");
            new AlertDialog.Builder(this)
                    .setMessage("Are you sure you want to exit?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.super.onBackPressed();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    })
                    .show();
        }
        f = getSupportFragmentManager().findFragmentByTag("HomeFragment");
        if (f != null && f.isVisible()) {
            Log.d(TAG, "HomeFragment VISIBLE");
            new AlertDialog.Builder(this)
                    .setMessage("Are you sure you want to exit?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.super.onBackPressed();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    })
                    .show();
        }
        f = getSupportFragmentManager().findFragmentByTag("ProfileFragment");
        if (f != null && f.isVisible()) {
            Log.d(TAG, "ProfileFragment VISIBLE");
            new AlertDialog.Builder(this)
                    .setMessage("Are you sure you want to exit?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.super.onBackPressed();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    })
                    .show();
        }
        f = getSupportFragmentManager().findFragmentByTag("ShareFragment");
        if (f != null && f.isVisible()) {
            Log.d(TAG, "ShareFragment VISIBLE");
            new AlertDialog.Builder(this)
                    .setMessage("Are you sure you want to exit?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.super.onBackPressed();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    })
                    .show();
        }
        f = getSupportFragmentManager().findFragmentByTag("SettingsPlugs");
        if (f != null && f.isVisible()) {
            Log.d(TAG, "SettingsPlugs VISIBLE");
            setEnabledNavigationDrawer(true);
            setTitle(getString(R.string.action_settings));
            super.onBackPressed();
            return;
        }
        f = getSupportFragmentManager().findFragmentByTag("PairPlugFragment");
        if (f != null && f.isVisible()) {
            Log.d(TAG, "PairPlugFragment VISIBLE");
            setTitle(getString(R.string.action_settings));
            super.onBackPressed();
            return;
        }
        f = getSupportFragmentManager().findFragmentByTag("ManageplugsFragment");
        if (f != null && f.isVisible()) {
            Log.d(TAG, "ManageplugsFragment VISIBLE");
            setTitle(getString(R.string.action_settings));
            super.onBackPressed();
            return;
        }
        f = getSupportFragmentManager().findFragmentByTag("SettingsNotificationFragment");
        if (f != null && f.isVisible()) {
            Log.d(TAG, "SettingsNotificationFragment VISIBLE");
            setEnabledNavigationDrawer(true);
            setTitle(getString(R.string.action_settings));
            super.onBackPressed();
            return;
        }
        f = getSupportFragmentManager().findFragmentByTag("SettingsShareFragment");
        if (f != null && f.isVisible()) {
            Log.d(TAG, "SettingsShareFragment VISIBLE");
            setEnabledNavigationDrawer(true);
            setTitle(getString(R.string.action_settings));
            super.onBackPressed();
            return;
        }

        f = getSupportFragmentManager().findFragmentByTag("ReportFragment");
        if (f != null && f.isVisible()) {
            Log.d(TAG, "ReportFragment VISIBLE");
            setEnabledNavigationDrawer(true);
            setTitle("Home");
            super.onBackPressed();
            return;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}