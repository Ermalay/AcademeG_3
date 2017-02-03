package ru.novil.sergey.academegtruestories;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ru.novil.sergey.navigationdraweractivity.R;

public class AdapterListVideo extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] itemImage;
    private final String[] itemName;
    private final String[] itemDescription;
    private final String[] itemPublished;
    private final String[] channelTitle;
    private boolean bOnView = true;
    private LinearLayout llMyListDesc;


    public AdapterListVideo(
            Activity context,
            String[] itemName,
            String[] itemImage,
            String[] itemDescription,
            String[] itemPublished,
            String[] channelTitle){
        super(context, R.layout.my_list, itemName);

        this.context = context;
        this.itemName = itemName;
        this.itemImage = itemImage;
        this.itemDescription = itemDescription;
        this.itemPublished = itemPublished;
        this.channelTitle = channelTitle;
    }


    @Override
    public View getView (int position,View view,ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.my_list, parent, false);
        TextView item = (TextView) rowView.findViewById(R.id.item);
        TextView desc = (TextView) rowView.findViewById(R.id.desc);
        ImageView icon = (ImageView) rowView.findViewById(R.id.icon);
        TextView publish = (TextView) rowView.findViewById(R.id.tvPublished);
        TextView channel = (TextView) rowView.findViewById(R.id.tvChannelTitle);
        llMyListDesc = (LinearLayout) rowView.findViewById(R.id.llMyListDesc);
        LinearLayout llMyListPublish = (LinearLayout) rowView.findViewById(R.id.llMyListPublish);
        llMyListDesc.setVisibility(View.GONE);

//        llMyListPublish.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (bOnView){
//                    llMyListDesc.setVisibility(View.VISIBLE);
//                    bOnView = false;
//                } else {
//                    llMyListDesc.setVisibility(View.GONE);
//                    bOnView = true;
//                }
//            }
//        });


        item.setText(itemName[position]);
        desc.setText(itemDescription[position]);
        publish.setText(myDateFormat(itemPublished[position]));
        channel.setText(channelTitle[position]);

        Picasso
                .with(context)
                .load(itemImage[position])
                .into(icon);

        return rowView;
    }

    private String myDateFormat (String dateFromSQLie) {
        Locale locale = new Locale("ru_RU");
        String formattedDate = "";
        SimpleDateFormat sqlDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.000'Z'");
        SimpleDateFormat outputDate = new SimpleDateFormat("'Опубликовано \n'dd.MM.yyyy'г. в 'HH:mm");
        Date res = null;
        try {
            res = sqlDate.parse(dateFromSQLie);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        formattedDate = outputDate.format(res);
        return formattedDate;
    }
}
