package ru.novil.sergey.academegtruestories;

import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import ru.novil.sergey.navigationdraweractivity.R;

public class ThirdFragment extends Fragment {

    private ViewPager mViewPager;
    ListView lv1;
    TextView qweqwe;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_second, container, false);
        View view = inflater.inflate(R.layout.activity_third_fragment, container, false);


        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Get the ViewPager and set it's PagerAdapter so that it can display items
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager_3);
        mViewPager.setAdapter(new SamplePagerAdapter());
        mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
    }

    // Adapter
    class SamplePagerAdapter extends PagerAdapter {

        /**
         * Вернуть Количество страниц для отображения
         */
        @Override
        public int getCount() {
            return 2;
        }

        /**
         * Возвратите True, если значение, возвращенное из такой же объект, как вид
         * добавлены в ViewPager.
         */
        @Override
        public boolean isViewFromObject(View view, Object o) {
            return o == view;
        }

        /**
         * Return the title of the item at position. This is important as what
         * this method returns is what is displayed in the SlidingTabLayout.
         */
        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0){
                return "Лента";
            } else if (position == 1) {
                return "AcademeG";
            } else if (position == 2) {
                return "AcademeG 2hd";
            } else if (position == 3) {
                return "DailyStream";
            } else {
                return "Вкладка " + (position + 1);
            }

        }

        /**
         * Создать представление, которое будет отображаться в позиции. Здесь мы
         * надуть макет из ресурсов приложения, а затем измените текст
         * целью обозначить позицию.
         */
        @Override
        public Object instantiateItem (ViewGroup container, int position) {
            if (position == 0) {
                // Надуть новый макет из наших ресурсов
                View view = getActivity().getLayoutInflater().inflate(R.layout.test,
                        container, false);

                container.addView(view);
                return view;

                //Вторая вкладка
            } if (position == 1){
                View view = getActivity().getLayoutInflater().inflate(R.layout.test_2,
                        container, false);

                container.addView(view);
                return view;


            }
//            if (position == 2){
//                View view = getActivity().getLayoutInflater().inflate(R.layout.pager_item_3,
//                        container, false);
//
//                container.addView(view);
//                return view;
//            }
//            if (position == 3){
//                View view = getActivity().getLayoutInflater().inflate(R.layout.pager_item_4,
//                        container, false);
//
//                container.addView(view);
//                return view;
//            }
            //Другие вкладки справа
            else {
                // Надуть новый макет с наших ресурсов
                View view = getActivity().getLayoutInflater().inflate(R.layout.pager_item,
                        container, false);
                // Добавить созданный вид на ViewPager
                container.addView(view);

                // Retrieve a TextView from the inflated View, and update it's text
                TextView title = (TextView) view.findViewById(R.id.item_title);
                title.setText(String.valueOf(position + 1));

                // Return the View
                return view;
            }
        }

        /**
         * Destroy the item from the ViewPager. In our case this is simply
         * removing the View.
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
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
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
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
