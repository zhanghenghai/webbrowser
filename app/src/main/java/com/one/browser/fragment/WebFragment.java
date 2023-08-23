package com.one.browser.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.one.browser.R;
import com.one.browser.databinding.ActivityMainBinding;
import com.one.browser.utils.DataUtil;
import com.one.browser.web.NewWeb;

import java.io.File;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WebFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 * @author 18517
 */
public class WebFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private LinearLayout linearLayout;

    private String mParam1;
    private String mParam2;

    public WebFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BookmakeFragment.
     */
    public static WebFragment newInstance(String param1, String param2) {
        WebFragment fragment = new WebFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_web, container, false);
        NewWeb newWeb = new NewWeb(getActivity());
        String path = Objects.requireNonNull(getContext()).getFilesDir().getPath()+ File.separator+"bookmark.html";
        String filePath= DataUtil.generateHomepageHtml(path,"");
        newWeb.loadUrl("file://"+filePath);
        linearLayout = view.findViewById(R.id.fragment_web);
        linearLayout.addView(newWeb, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        return view;
    }
}