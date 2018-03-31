package cn.xxyangyoulin.android_custom_view_tab_indicator;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by mnn on 2016/9/5.
 */

public class VpFragment extends Fragment {
    public static final String BUNDLE_TITLE = "title";

    public static VpFragment newInstance(String title) {
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_TITLE, title);

        VpFragment indicator = new VpFragment();
        indicator.setArguments(bundle);

        return indicator;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Bundle bundle = getArguments();

        TextView tv = new TextView(getActivity());
        tv.setGravity(Gravity.CENTER);
        tv.setText(bundle.getString(BUNDLE_TITLE));

        return tv;
    }
}
