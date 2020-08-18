package com.example.user.restaurantreviewapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.user.restaurantreviewapp.model.User;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String TAG = "MainActivity";
    FirebaseDatabase database;
    DatabaseReference myRef;
    private FirebaseUser firebaseUser;
    private String uid;
    public static double source_lat, source_long;

    private CircleImageView proImageView;

    private TextView usernameTextView, emailTextView;

    private User user;
    public static final String PREFS_FILE_NAME = "sharedPreferences";


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            uid = firebaseUser.getUid();
            Log.w(TAG, uid);
            myRef = database.getReference();
            myRef.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    user = dataSnapshot.getValue(User.class);
                    if (user == null || user.getUsername() == null || user.getUsername().isEmpty()) {
                        startActivity(new Intent(MainActivity.this, CreateProfileActivity.class));
                        finish();
                    } else {
                        initializeDefaultFragment(savedInstanceState,0);

                        proImageView = findViewById(R.id.nav_header_circleimageview_id);
                        usernameTextView = findViewById(R.id.nav_header_name_id);
                        emailTextView = findViewById(R.id.nav_header_emailaddress_id);

                        Picasso.with(MainActivity.this).load(user.getImage_url())
                                .resize(80, 80).into(proImageView);
                        emailTextView.setText(user.getEmail());
                        usernameTextView.setText(user.getUsername());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.w(TAG, databaseError.getMessage());
                }
            });
        } else {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }

        toggleDrawer();

//        setDarkModeSwitchListener();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("search");


//        searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Place place = (Place) parent.getItemAtPosition(position);
//                Log.w("before fetching place",place.getDescription());
//                searchView.setText(place.getDescription());
//            }
//        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Bundle bundle = new Bundle();
                bundle.putString("queryText", query);
                replaceFragments(SearchResultsFragment.class, bundle);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Initialize all widgets
     */
    private void initializeViews(){
        toolbar = findViewById(R.id.toolbar_id);
        toolbar.setTitle("restaurant review");
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout_id);
        frameLayout = findViewById(R.id.framelayout_id);
        navigationView = findViewById(R.id.navigationview_id);
        navigationView.setNavigationItemSelectedListener(this);

//        darkModeSwitch = (SwitchCompat)navigationView.getMenu().findItem(R.id.nav_darkmode_id).getActionView();
    }

    /**
     * Checks if the savedInstanceState is null - onCreate() is ran
     * If so, display fragment of navigation drawer menu at position itemIndex and
     * set checked status as true
     * @param savedInstanceState
     * @param itemIndex
     */
    private void initializeDefaultFragment(Bundle savedInstanceState, int itemIndex){
        if (savedInstanceState == null){
            MenuItem menuItem = navigationView.getMenu().getItem(itemIndex).setChecked(true);
            onNavigationItemSelected(menuItem);
        }
    }

    /**
     * Creates an instance of the ActionBarDrawerToggle class:
     * 1) Handles opening and closing the navigation drawer
     * 2) Creates a hamburger icon in the toolbar
     * 3) Attaches listener to open/close drawer on icon clicked and rotates the icon
     */
    private void toggleDrawer() {
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        //Checks if the navigation drawer is open -- If so, close it
        Log.w("stack",String.valueOf(getSupportFragmentManager().getBackStackEntryCount()));
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        // If drawer is already close -- Do not override original functionality
        else {
            if(getSupportFragmentManager().getBackStackEntryCount() == 1) {
                finish();
            }else {
                super.onBackPressed();
            }

        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.nav_restaurants_id:
                replaceFragments(RestaurantsFragment.class, new Bundle());
//                getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_id, new RestaurantsFragment()).addToBackStack(null)
//                        .commit();
                closeDrawer();
                break;
            case R.id.nav_favorites_id:
                replaceFragments(MyFavoritesFragment.class, new Bundle());
//                getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_id, new MyFavoritesFragment()).addToBackStack(null)
//                        .commit();
                closeDrawer();
                break;
            case R.id.nav_reviews_id:
                replaceFragments(MyReviewsFragment.class, new Bundle());
//                getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_id, new MyReviewsFragment()).addToBackStack(null)
//                        .commit();
                closeDrawer();
                break;
            case R.id.nav_profile_id:
                replaceFragments(MyProfileFragment.class, new Bundle());
//                getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_id,new MyProfileFragment()).addToBackStack(null)
//                        .commit();
                closeDrawer();
                break;
//            case R.id.nav_settings_id:
//                getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_id, new SettingsFragment())
//                        .commit();
//                deSelectCheckedState();
//                closeDrawer();
//                break;
            case R.id.nav_logout_id:
                if(mAuth!=null) {
                    mAuth.signOut();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
                break;
        }
        deSelectCheckedState();
        menuItem.setChecked(true);
        return true;
    }

    /**
     * Attach setOnCheckedChangeListener to the dark mode switch
     */
//    private void setDarkModeSwitchListener(){
//        darkModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (!isChecked){
//                    Toast.makeText(MainActivity.this, "Dark Mode Turn Off", Toast.LENGTH_SHORT).show();
//                }
//                else {
//                    Toast.makeText(MainActivity.this, "Dark Mode Turn On", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }

    /**
     * Checks if the navigation drawer is open - if so, close it
     */
    private void closeDrawer(){
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }
    
    /**
     * Iterates through all the items in the navigation menu and deselects them:
     * removes the selection color
     */
    private void deSelectCheckedState(){
        int noOfItems = navigationView.getMenu().size();
        for (int i=0; i<noOfItems;i++){
            navigationView.getMenu().getItem(i).setChecked(false);
        }
    }

    public void replaceFragments(Class fragmentClass, Bundle args) {
        System.out.println("stack " + getSupportFragmentManager().getBackStackEntryCount());
        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        assert fragment != null;
        fragment.setArguments(args);
        fragmentManager.beginTransaction().replace(R.id.framelayout_id, fragment).addToBackStack(null)
                .commit();
        System.out.println("stack " + getSupportFragmentManager().getBackStackEntryCount());
    }

}
