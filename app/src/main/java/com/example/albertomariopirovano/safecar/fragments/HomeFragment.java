package com.example.albertomariopirovano.safecar.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;

import com.example.albertomariopirovano.safecar.adapters.AppFragmentPagerAdapter;
import com.example.albertomariopirovano.safecar.R;
import com.example.albertomariopirovano.safecar.inner.fragments.TabHome;
import com.example.albertomariopirovano.safecar.inner.fragments.TabFragment;
import com.example.albertomariopirovano.safecar.inner.fragments.TabOrderDSI;
import com.example.albertomariopirovano.safecar.inner.fragments.TabOrderDate;
import com.example.albertomariopirovano.safecar.inner.fragments.TabOrderDuration;
import com.example.albertomariopirovano.safecar.inner.fragments.TabOrderKM;
import com.example.albertomariopirovano.safecar.services.FetchService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by mattiacrippa on 14/03/17.
 */

public class HomeFragment extends Fragment implements OnPageChangeListener, OnTabChangeListener {

    private ViewPager viewPager;
    private TabHost tabHost;
    private AppFragmentPagerAdapter appFragmentPagerAdapter;
    private List<Fragment> listFragments;
    private Realm realm;
    private FetchService dataService = FetchService.getInstance();
    //int i = 0;
    View v;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.tabs_viewpager_layout, container, false);

        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("default2")
                .deleteRealmIfMigrationNeeded()
                .build();

        realm = Realm.getInstance(config);

        //init ViewPager
        this.initViewPager();

        //init TabHost
        this.initTabHost(savedInstanceState);

        return v;
    }

    class FakeContent implements TabContentFactory {

        private final Context context;

        public FakeContent(Context mcontext) {
            context = mcontext;
        }

        @Override
        public View createTabContent(String s) {

            View fakeView = new View(context);
            fakeView.setMinimumHeight(0);
            fakeView.setMinimumWidth(0);
            return fakeView;
        }
    }

    private void initViewPager() {

        listFragments = new ArrayList<Fragment>();
        listFragments.add(new TabHome());
        listFragments.add(new TabOrderDate());
        listFragments.add(new TabOrderDSI());
        listFragments.add(new TabOrderDuration());
        listFragments.add(new TabOrderKM());

        this.appFragmentPagerAdapter = new AppFragmentPagerAdapter(getChildFragmentManager(), listFragments);
        this.viewPager = (ViewPager) v.findViewById(R.id.view_pager);
        this.viewPager.setAdapter(this.appFragmentPagerAdapter);
        this.viewPager.setOnPageChangeListener(this);
    }

    private void initTabHost(Bundle args) {

        tabHost = (TabHost) v.findViewById(android.R.id.tabhost);
        tabHost.setup();

        for (int i = 0; i <= 4; i++) {
            TabHost.TabSpec tabSpec;
            tabSpec = tabHost.newTabSpec(((TabFragment)listFragments.get(i)).getName());
            tabSpec.setIndicator(((TabFragment)listFragments.get(i)).getName());
            tabSpec.setContent(new FakeContent(getActivity()));
            tabHost.addTab(tabSpec);
        }

        tabHost.setOnTabChangedListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify  parent activity in AndroidManifest.xml
        int id = item.getItemId();
        //if (id == R.id.action_settings) {
        //    return true;
        //}
        return super.onOptionsItemSelected(item);
    }

    @Override
    // tabHost Listener
    public void onTabChanged(String tabId) {

        int selectedItem = this.tabHost.getCurrentTab();
        this.viewPager.setCurrentItem(selectedItem);

        HorizontalScrollView horizontalScrollView = (HorizontalScrollView) v.findViewById(R.id.h_scroll_view);
        View tabView = tabHost.getCurrentTabView();
        int scrollPos = tabView.getLeft() - (horizontalScrollView.getWidth() - tabView.getWidth()) / 2;
        horizontalScrollView.smoothScrollTo(scrollPos, 0);

    }

    @Override
    // viewPager Listener
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int selectedItem) {
        tabHost.setCurrentTab(selectedItem);
    }
}