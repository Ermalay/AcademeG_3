package ru.novil.sergey.academegtruestories;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import ru.novil.sergey.navigationdraweractivity.R;


public class FifthFragment extends Fragment {

    private WebView webView;
    LinearLayout ll_pb_tv_5;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.activity_fifth_fragment, container, false);

        ll_pb_tv_5 = (LinearLayout) view.findViewById(R.id.ll_pb_tv_5);
        webView = (WebView) view.findViewById(R.id.webViewFragFifth);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.loadUrl("https://www.instagram.com/academeg/");
//        webView.loadUrl("https://www.instagram.com/academeg/");
        webView.setWebViewClient(new MyWebViewClient());


        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()){
                    webView.goBack();
                    return false;
                } else {
                    return false;
                }


//                {
//                    if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
//                        // handle back button's click listener
//                        Toast.makeText(getActivity(), "safdasfa", Toast.LENGTH_SHORT).show();
//                        return true;
//                    }
//                }
//                return true;
            }
        });

        return view;
    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            //you might need this
            ll_pb_tv_5.setVisibility(View.GONE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            view.loadUrl(url);
//            if(Uri.parse(url).getHost().length() == 0) {
            if(Uri.parse(url).getHost().contains("instagram.com")) {


                return false;
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            view.getContext().startActivity(intent);
            return true;

        }

    }
}
