package edu.byu.cs.tweeter.client.view.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.client.R;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.backgroundTask.LogoutTask;
import edu.byu.cs.tweeter.client.backgroundTask.PostStatusTask;
import edu.byu.cs.tweeter.client.backgroundTask.UnfollowTask;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.presenter.FollowPresenter;
import edu.byu.cs.tweeter.client.presenter.IsFollowerPresenter;
import edu.byu.cs.tweeter.client.presenter.LogoutPresenter;
import edu.byu.cs.tweeter.client.presenter.StatusPresenter;
import edu.byu.cs.tweeter.client.view.login.LoginActivity;
import edu.byu.cs.tweeter.client.view.login.StatusDialogFragment;
import edu.byu.cs.tweeter.client.view.util.ImageUtils;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * The main activity for the application. Contains tabs for feed, story, following, and followers.
 */
public class MainActivity extends AppCompatActivity implements StatusDialogFragment.Observer, FollowPresenter.FollowView, IsFollowerPresenter.IsFollowerView, LogoutPresenter.LogoutView, StatusPresenter.StatusView {

    private static final String LOG_TAG = "MainActivity";

    public static final String CURRENT_USER_KEY = "CurrentUser";

//    public static final String USER_KEY = "UserKey";

    private Toast logOutToast;
    private Toast postingToast;
    private User selectedUser;
    private User targetUser;
    private TextView followeeCount;
    private TextView followerCount;
    private Button followButton;

    private FollowPresenter followPresenter;
    private IsFollowerPresenter isFollowerPresenter;
    private LogoutPresenter logoutPresenter;
    private StatusPresenter statusPresenter;

    @Override
    public void displayErrorMessage(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void displayInfoMessage(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void enableFollowButton(boolean value) {
        followButton.setEnabled(value);
    }

    @Override
    public void updateFollowButton(String text, int textColor, int backgroundColor) {
        followButton.setText(text);
        followButton.setBackgroundColor(backgroundColor);
        followButton.setTextColor(textColor);
    }

    @Override
    public void setFollowAvailable() {
        followButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void setFollowUnavailable() {
        followButton.setVisibility(View.GONE);
    }

    @Override
    public void logoutUser() {
        //Revert to login screen.
        Intent intent = new Intent(this, LoginActivity.class);
        //Clear everything so that the main activity is recreated with the login page.
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //Clear user data (cached data).
        Cache.getInstance().clearCache();
        startActivity(intent);
    }

    @Override
    public void updateFollowersCount(int count) {
        followerCount.setText(getString(R.string.followerCount, String.valueOf(count)));
    }

    @Override
    public void updateFollowingCount(int count) {
        followeeCount.setText(getString(R.string.followerCount, String.valueOf(count)));
    }

    @Override
    public void displayStatus() {
        postingToast = Toast.makeText(this, "Posting Status...", Toast.LENGTH_LONG);
        postingToast.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectedUser = (User) getIntent().getSerializableExtra(CURRENT_USER_KEY);

        isFollowerPresenter = new IsFollowerPresenter(this, Cache.getInstance().getCurrUserAuthToken(), selectedUser);
        followPresenter = new FollowPresenter(this, Cache.getInstance().getCurrUserAuthToken(), selectedUser);

        if (selectedUser == null) {
            throw new RuntimeException("User not passed to activity");
        }

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(), selectedUser);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StatusDialogFragment statusDialogFragment = new StatusDialogFragment();
                statusDialogFragment.show(getSupportFragmentManager(), "post-status-dialog");
            }
        });

        //updateSelectedUserFollowingAndFollowers();
        followPresenter.updateFollowerAndFollowingCounts(Cache.getInstance().getCurrUserAuthToken(),
                selectedUser);

        TextView userName = findViewById(R.id.userName);
        userName.setText(selectedUser.getName());

        TextView userAlias = findViewById(R.id.userAlias);
        userAlias.setText(selectedUser.getAlias());

        ImageView userImageView = findViewById(R.id.userImage);
        userImageView.setImageDrawable(ImageUtils.drawableFromByteArray(selectedUser.getImageBytes()));

        followeeCount = findViewById(R.id.followeeCount);
        followeeCount.setText(getString(R.string.followeeCount, "..."));

        followerCount = findViewById(R.id.followerCount);
        followerCount.setText(getString(R.string.followerCount, "..."));

        followButton = findViewById(R.id.followButton);

        isFollowerPresenter.isFollower(targetUser);

        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                followPresenter.updateFollow(followButton.getText().toString());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logoutMenu) {
            logOutToast = Toast.makeText(this, "Logging Out...", Toast.LENGTH_LONG);
            logOutToast.show();
            logoutPresenter = new LogoutPresenter(this);
            logoutPresenter.logout();

            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStatusPosted(String post) {

        try {
            Status newStatus = new Status(post, Cache.getInstance().getCurrUser(), getFormattedDateTime(), parseURLs(post), parseMentions(post));
            statusPresenter = new StatusPresenter(this);
            statusPresenter.postStatus(Cache.getInstance().getCurrUserAuthToken(), newStatus);
        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage(), ex);
            Toast.makeText(this, "Failed to post the status because of exception: " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public String getFormattedDateTime() throws ParseException {
        SimpleDateFormat userFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat statusFormat = new SimpleDateFormat("MMM d yyyy h:mm aaa");

        return statusFormat.format(userFormat.parse(LocalDate.now().toString() + " " + LocalTime.now().toString().substring(0, 8)));
    }

    public List<String> parseURLs(String post) throws MalformedURLException {
        List<String> containedUrls = new ArrayList<>();
        for (String word : post.split("\\s")) {
            if (word.startsWith("http://") || word.startsWith("https://")) {

                int index = findUrlEndIndex(word);

                word = word.substring(0, index);

                containedUrls.add(word);
            }
        }

        return containedUrls;
    }

    public List<String> parseMentions(String post) {
        List<String> containedMentions = new ArrayList<>();

        for (String word : post.split("\\s")) {
            if (word.startsWith("@")) {
                word = word.replaceAll("[^a-zA-Z0-9]", "");
                word = "@".concat(word);

                containedMentions.add(word);
            }
        }

        return containedMentions;
    }

    public int findUrlEndIndex(String word) {
        if (word.contains(".com")) {
            int index = word.indexOf(".com");
            index += 4;
            return index;
        } else if (word.contains(".org")) {
            int index = word.indexOf(".org");
            index += 4;
            return index;
        } else if (word.contains(".edu")) {
            int index = word.indexOf(".edu");
            index += 4;
            return index;
        } else if (word.contains(".net")) {
            int index = word.indexOf(".net");
            index += 4;
            return index;
        } else if (word.contains(".mil")) {
            int index = word.indexOf(".mil");
            index += 4;
            return index;
        } else {
            return word.length();
        }
    }

}
