package com.gadogado.piter;

import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static android.support.test.espresso.contrib.DrawerMatchers.*;
import static org.hamcrest.core.IsNot.not;

import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.espresso.intent.Intents;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.Gravity;

import com.gadogado.piter.Module.Home.TweetActivity;
import com.gadogado.piter.Module.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class PiterEspressoTest {

    @Rule
    public ActivityTestRule<MainActivity> mainRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void checkToolbar() {
        onView(withId(R.id.layout_toolbar)).check(matches(isDisplayed()));
    }

    @Test
    public void checkToolbarTitleOnHome() {
        onView(withId(R.id.main_drawer_layout))
                .check(matches(isClosed(Gravity.START)))
                .perform(DrawerActions.open());

        onView(withId(R.id.main_nav_view))
                .perform(NavigationViewActions.navigateTo(R.id.nav_home));

        onView(withId(R.id.toolbar_title)).check(matches(withText(R.string.app_name)));
    }

    @Test
    public void checkToolbarTitleOnMoments() {
        onView(withId(R.id.main_drawer_layout))
                .check(matches(isClosed(Gravity.START)))
                .perform(DrawerActions.open());

        onView(withId(R.id.main_nav_view))
                .perform(NavigationViewActions.navigateTo(R.id.nav_moments));

        onView(withId(R.id.toolbar_title)).check(matches(withText(R.string.moments)));
    }

    @Test
    public void checkToolbarTitleOnConfiguration() {
        onView(withId(R.id.main_drawer_layout))
                .check(matches(isClosed(Gravity.START)))
                .perform(DrawerActions.open());

        onView(withId(R.id.main_nav_view))
                .perform(NavigationViewActions.navigateTo(R.id.nav_configuration));

        onView(withId(R.id.toolbar_title)).check(matches(withText(R.string.configuration)));
    }

    @Test
    public void checkToolbarTitleOnAbout() {
        onView(withId(R.id.main_drawer_layout))
                .check(matches(isClosed(Gravity.START)))
                .perform(DrawerActions.open());

        onView(withId(R.id.main_nav_view))
                .perform(NavigationViewActions.navigateTo(R.id.nav_about));

        onView(withId(R.id.toolbar_title)).check(matches(withText(R.string.about)));
    }

    @Test
    public void checkToolbarTitleOnContactUs() {
        onView(withId(R.id.main_drawer_layout))
                .check(matches(isClosed(Gravity.START)))
                .perform(DrawerActions.open());

        onView(withId(R.id.main_nav_view))
                .perform(NavigationViewActions.navigateTo(R.id.nav_contactus));

        onView(withId(R.id.toolbar_title)).check(matches(withText(R.string.contact_us)));
    }

    @Test
    public void checkFloatingActionButton() {
        onView(withId(R.id.home_add)).check(matches(isDisplayed()));
        onView(withId(R.id.home_add)).check(matches(isClickable()));
    }

    @Test
    public void checkFloatingActionButtonTarget() {
        Intents.init();
        onView(withId(R.id.home_add)).check(matches(isDisplayed())).perform(click());
        intended(hasComponent(TweetActivity.class.getName()));
        Intents.release();
    }

    @Test
    public void checkTweetButton() {
        Intents.init();
        onView(withId(R.id.home_add)).check(matches(isDisplayed())).perform(click());
        intended(hasComponent(TweetActivity.class.getName()));

        onView(withId(R.id.toolbar_tweet_button)).check(matches(isClickable()));
        onView(withId(R.id.toolbar_tweet_button)).check(matches(not(isEnabled())));

        Intents.release();
    }

    @Test
    public void checkCharCount() {
        Intents.init();
        onView(withId(R.id.home_add)).check(matches(isDisplayed())).perform(click());
        intended(hasComponent(TweetActivity.class.getName()));

        onView(withId(R.id.tweet_textfield))
                .check(matches(isDisplayed()))
                .perform(typeText("Hello, World!"));

        onView(withId(R.id.tweet_charcount))
                .check(matches(isDisplayed()))
                .check(matches(withText("13")));

        onView(withId(R.id.toolbar_tweet_button))
                .check(matches(isEnabled()))
                .perform(click());

        Intents.release();
    }

    @Test
    public void checkCharCountLimit() {
        Intents.init();
        onView(withId(R.id.home_add)).check(matches(isDisplayed())).perform(click());
        intended(hasComponent(TweetActivity.class.getName()));

        onView(withId(R.id.tweet_textfield))
                .check(matches(isDisplayed()))
                .perform(typeText("Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                        "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
                        "Ut enim ad minim veniam, " +
                        "quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo co.nsequat. " +
                        "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. " +
                        "Excepteur sint occaecat cupidatat non proident, " +
                        "sunt in culpa qui officia deserunt mollit anim id est laborum."));

        onView(withId(R.id.tweet_charcount))
                .check(matches(isDisplayed()))
                .check(matches(hasTextColor(R.color.colorWarning)));

        onView(withId(R.id.toolbar_tweet_button))
                .check(matches(not(isEnabled())));

        Intents.release();
    }

    @Test
    public void checkSelectImageButtons() {
        Intents.init();
        onView(withId(R.id.home_add)).check(matches(isDisplayed())).perform(click());
        intended(hasComponent(TweetActivity.class.getName()));

        onView(withId(R.id.tweet_camera)).check(matches(isEnabled()));
        onView(withId(R.id.tweet_gallery)).check(matches(isEnabled()));
        onView(withId(R.id.tweet_clear)).check(matches(not(isEnabled())));

        Intents.release();
    }
}
