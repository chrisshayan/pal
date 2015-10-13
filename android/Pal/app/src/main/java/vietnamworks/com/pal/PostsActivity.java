package vietnamworks.com.pal;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import vietnamworks.com.pal.components.DrawerEventListener;
import vietnamworks.com.pal.fragments.FragmentHeader;

public class PostsActivity extends AppCompatActivity {
    public FragmentHeader fragment_header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        fragment_header = ((FragmentHeader)getSupportFragmentManager().findFragmentById(R.id.fragment_toolbar));

        Bundle b = getIntent().getExtras();
        if (b != null) {
            String mode = b.getString("mode");
            if (mode != null) {
                if (mode.equalsIgnoreCase("recent")) {
                    fragment_header.setTitle("Recent posts");
                } else if (mode.equalsIgnoreCase("evaluated")) {
                    fragment_header.setTitle("Evaluated posts");
                }
            }
        }
    }

    public void onOpenDrawer(View v) {
        //drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new DrawerEventListener(drawer));
        drawer.openDrawer(navigationView);
    }
}
