package org.cbccessence.cch.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import org.cbccessence.R;
import org.cbccessence.activity.AboutActivity;
import org.cbccessence.cch.model.User;
import org.digitalcampus.oppia.activity.HelpActivity;
import org.digitalcampus.oppia.activity.PrefsActivity;
import org.digitalcampus.oppia.application.DbHelper;
import org.cbccessence.utilities.TaskCompleteListener;
import org.digitalcampus.oppia.service.TrackerService;
import org.cbccessence.cch.fragments.ClientListFragment;
import org.cbccessence.cch.fragments.ClientRegistrationFragment;
import org.cbccessence.poc.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class ClientActivity extends BaseActivity  implements TaskCompleteListener{

    LinearLayout extraLayout;
    int translation;
    String TAG = ClientActivity.class.getSimpleName();
    DbHelper databaseHelper;
    ViewPager viewPager;
    TabLayout tabLayout;






    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.client_main_activity);


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);

        }

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
       // setupPagerIcons();



        ClientRegistrationFragment.setTaskCompleteListener(this);

    }


    private void setupPagerIcons() {

        tabLayout.getTabAt(0).setText(viewPager.getCurrentItem());
        tabLayout.getTabAt(1).setText(viewPager.getCurrentItem());


        tabLayout.getTabAt(0).getIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(1).getIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
    }







    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_home, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_settings) {
            Intent i = new Intent(this, PrefsActivity.class);
            startActivity(i);
            return true;
        } else if (itemId == R.id.menu_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        } else if (itemId == R.id.menu_help) {
            startActivity(new Intent(this, HelpActivity.class));
            return true;
        } else if (itemId == R.id.menu_logout) {
            super.logout();
            return true;
        } else if (itemId == R.id.menu_sync) {
            Intent service = new Intent(this, TrackerService.class);
            Bundle tb = new Bundle();
            tb.putBoolean("backgroundData", true);
            service.putExtras(tb);
            //this.startService(service);

            return true;
        }

        return true;
    }





    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new ClientListFragment(), "Client List");
        adapter.addFrag(new ClientRegistrationFragment(), "Registration");
        viewPager.setAdapter(adapter);
    }





    @Override
    public void submitTaskComplete(User user) {

        Log.i(TAG, "Submit listener from User addition is fired");
        viewPager.setCurrentItem(0, true);



    }







    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }


        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
            // return null;
        }

    }

    public class ZoomOutPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();


            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {

                    //ord_icon.setTranslationX((float) (-(1 - position) * 1.7 * pageWidth));


                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);

                    //ord_icon.setTranslationX((float) ((1 + position) * 1.7 * pageWidth));

                }

                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }

}
