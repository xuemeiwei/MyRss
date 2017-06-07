package com.coen268.project.myrss;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    private ImageView image;
    private TextView name;
    private TextView email;
    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInAccount googleSignInAccount;
    private NavigationView navigationView;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate:");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();

        googleSignInAccount = (GoogleSignInAccount) getIntent().getExtras().get("user");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        setHeaderView(navigationView);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;


    }

    @Override
    protected void onStart() {

        super.onStart();
        android.app.FragmentManager manager = getFragmentManager();
        LatestFragment latestFragment = new LatestFragment();
        manager.beginTransaction().replace(R.id.contentLayout,
                latestFragment, latestFragment.getTag()).commit();
    }

    public void displayGoogleAccountInfo(GoogleSignInAccount googleSignInAccount) {
        Uri imageUri = googleSignInAccount.getPhotoUrl();
        String emailString = googleSignInAccount.getEmail();
        String nameString = googleSignInAccount.getDisplayName();
        if(imageUri != null) {
            Picasso.with(this).load(imageUri).resize(140,140).into(image);
        }
        name.setText(nameString);
        email.setText(emailString);
    }

    public GoogleSignInAccount getGoogleAccountInfoFromMain() {
        return googleSignInAccount;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        android.app.FragmentManager manager = getFragmentManager();
        if (id == R.id.latest) {
            setTitle("Latest");

            LatestFragment latestFragment = new LatestFragment();
            manager.beginTransaction().replace(R.id.contentLayout,
                    latestFragment, latestFragment.getTag()).commit();

        } else if (id == R.id.toRead) {
            setTitle("ToRead");
            ToReadFragment toReadFragment = new ToReadFragment();
            manager.beginTransaction().replace(R.id.contentLayout,
                    toReadFragment, toReadFragment.getTag()).addToBackStack(null).
                    commit();

        } else if (id == R.id.fav) {
            setTitle("Favorite");

        } else if (id == R.id.sign_out) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            setSignOutDialog(builder);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onResume() {
        super.onResume();
        if(toolbar.getTitle().equals("ToRead")) {
            android.app.FragmentManager manager = getFragmentManager();
            ToReadFragment toReadFragment = new ToReadFragment();
            manager.beginTransaction().replace(R.id.contentLayout,
                    toReadFragment, toReadFragment.getTag()).
                    commit();
        }
    }
    public void setSignOutDialog(AlertDialog.Builder builder) {
        builder.setMessage(R.string.signout_message);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                signOut();
                Intent goLogin = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(goLogin);
                finish();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Toast.makeText(getApplicationContext(),"Logged Out",Toast.LENGTH_SHORT).show();
                        Intent i=new Intent(getApplicationContext(),LoginActivity.class);
                        startActivity(i);
                    }
                });
    }

    public void setTitle(CharSequence title) {
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(title);
    }

    public void setHeaderView(NavigationView navigationView){
        View hView =  navigationView.inflateHeaderView(R.layout.nav_header_main);
        image = (ImageView)hView.findViewById(R.id.imageView);
        name = (TextView)hView.findViewById(R.id.nameView);
        email = (TextView)hView.findViewById(R.id.emailView);
        displayGoogleAccountInfo(googleSignInAccount);
    }
}
//    public void getGoogleAccountInfo (GoogleSignInResult result) {
//        if (result.isSuccess()) {
//            googleSignInAccount = result.getSignInAccount();
//        }
//    }

//    public void getGoogleInfo(GoogleApiClient mGoogleApiClient) {
//
//        OptionalPendingResult<GoogleSignInResult> pendingResult =
//                Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
//        if (pendingResult.isDone()) {
//            // There's immediate result available.
//            GoogleSignInResult result = pendingResult.get();
//            getGoogleAccountInfo(result);
//        } else {
//            // There's no immediate result ready, displays some progress indicator and waits for the
//            // async callback.
//            pendingResult.setResultCallback(new ResultCallback<GoogleSignInResult>() {
//                @Override
//                public void onResult(@NonNull GoogleSignInResult result) {
//                    getGoogleAccountInfo(result);
//                }
//            });
//        }
//    }