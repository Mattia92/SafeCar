package com.example.albertomariopirovano.safecar.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;
import android.widget.TextView;

import com.example.albertomariopirovano.safecar.R;
import com.example.albertomariopirovano.safecar.adapters.AppFragmentPagerAdapter;
import com.example.albertomariopirovano.safecar.inner.fragments.TAGInterface;
import com.example.albertomariopirovano.safecar.inner.fragments.TabFragment;
import com.example.albertomariopirovano.safecar.inner.fragments.TabHome;
import com.example.albertomariopirovano.safecar.inner.fragments.TabOrderDSI;
import com.example.albertomariopirovano.safecar.inner.fragments.TabOrderDate;
import com.example.albertomariopirovano.safecar.inner.fragments.TabOrderDuration;
import com.example.albertomariopirovano.safecar.inner.fragments.TabOrderKM;
import com.example.albertomariopirovano.safecar.services.FetchService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mattiacrippa on 14/03/17.
 */

public class HomeFragment extends Fragment implements OnPageChangeListener, OnTabChangeListener, TAGInterface {

    private static final String TAG = "HomeFragment";
    //int i = 0;
    View v;
    private ViewPager viewPager;
    private TabHost tabHost;
    private AppFragmentPagerAdapter appFragmentPagerAdapter;
    private List<Fragment> listFragments;
    private FetchService dataService = FetchService.getInstance();

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    public String getAssignedTag() {
        return TAG;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "Destroying view");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //init ViewPager
        this.initViewPager();

        //init TabHost
        this.initTabHost(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.i(TAG, "Creating view");

        v = inflater.inflate(R.layout.tabs_viewpager_layout, container, false);

        return v;
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
        viewPager.setOffscreenPageLimit(5);
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

        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
            TextView tv = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setTextColor(getResources().getColor(R.color.colorPrimaryText));
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
}