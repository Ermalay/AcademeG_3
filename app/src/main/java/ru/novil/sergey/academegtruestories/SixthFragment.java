package ru.novil.sergey.academegtruestories;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import ru.novil.sergey.navigationdraweractivity.R;

public class SixthFragment extends Fragment {

    private WebView webView;
    LinearLayout ll_pb_tv;
    View v;
    Activity activity;
    private static long back_pressed;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.activity_sixth_fragment, container, false);
//        final ProgressBar progress1 = (ProgressBar) v.findViewById(R.id.progress1);
        ll_pb_tv = (LinearLayout) v.findViewById(R.id.ll_pb_tv);
        webView = (WebView) v.findViewById(R.id.webViewFragSixth);
        webView.getSettings().setJavaScriptEnabled(true);
//        webView.getSettings().setSupportZoom(true);
        webView.loadUrl("http://academeg-store.ru");
//        webView.loadUrl("https://www.instagram.com/academeg/");

        webView.setWebViewClient(new MyWebViewClient());
//        webView.setWebViewClient(new WebViewClient(){
//            @Override
//            public void onPageFinished(WebView view, String url) {
////                view.setVisibility(View.VISIBLE);
//                //you might need this
//                ll_pb_tv.setVisibility(View.GONE);
////                view.bringToFront();
//            }
//
////            @Override
////            public void onPageStarted(WebView view, String url,  Bitmap favicon) {
////                view.setVisibility(View.GONE);//hide the webview that will display your dialog
////            }
//        });


        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()){
                    webView.goBack();
                    return false;
                } else {

//                    if (back_pressed + 2000 > System.currentTimeMillis()){
////                        getActivity().finish();
//                    }
//                    else {
////                        Toast.makeText(getActivity(), "дважды", Toast.LENGTH_SHORT).show();
//                    }
//                    back_pressed = System.currentTimeMillis();
                    return false;
                }
//                return true;
            }
        });


        return v;
    }

    private class MyWebViewClient extends WebViewClient{
        @Override
        public void onPageFinished(WebView view, String url) {
//                view.setVisibility(View.VISIBLE);
            //you might need this
            ll_pb_tv.setVisibility(View.GONE);
//                view.bringToFront();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            view.loadUrl(url);
//            if(Uri.parse(url).getHost().length() == 0) {

            if(Uri.parse(url).getHost().contains("academeg-store.ru")) {


                return false;
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            view.getContext().startActivity(intent);
            return true;

//            webView.setOnKeyListener(new View.OnKeyListener() {
//                @Override
//                public boolean onKey(View v, int keyCode, KeyEvent event) {
//                    if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()){
//                        webView.goBack();
//                        return true;
//                    }
//                    return false;
//                }
//            });
//            return false;
        }

    }
//    public boolean goBack() {
//        if(webView.canGoBack()) {
//            webView.goBack();
//            return true;
//        }
//        return false;
//    }
}
