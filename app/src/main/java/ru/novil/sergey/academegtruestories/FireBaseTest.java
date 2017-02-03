package ru.novil.sergey.academegtruestories;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ru.novil.sergey.navigationdraweractivity.R;

public class FireBaseTest extends AppCompatActivity {

    TextView textView;
    Button button2, button3;

    int poiu = 0;

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mConditionRef = mRootRef.child("cond");

//    FireBase fireBase = new FireBase();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fire_base_test);

        textView = (TextView) findViewById(R.id.textView);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
    }

    @Override
    protected void onStart(){
        super.onStart();

        mConditionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String wer = dataSnapshot.getKey();

//                Object qqq = dataSnapshot.getValue();
//                textView.setText(String.valueOf(qqq));
                textView.setText(wer);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                fireBase.fbOnClick();
            }
        });

//        button2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mConditionRef.setValue(poiu + 1);
//                poiu = poiu + 1;
//            }
//        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mConditionRef.setValue(poiu - 1);
                poiu = poiu - 1;
            }
        });
    }
}
