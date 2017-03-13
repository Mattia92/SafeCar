package com.example.albertomariopirovano.safecar.fragment;

        import android.os.Bundle;
        import android.app.Fragment;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;

        import com.example.albertomariopirovano.safecar.R;

public class FragmentShare extends Fragment {

    public FragmentShare() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.share_fragment, container, false);
        return rootView;
    }
}
