package ru.novil.sergey.academegtruestories;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ru.novil.sergey.navigationdraweractivity.R;
import ru.novil.sergey.academegtruestories.other.MyApplication;

public class SecondFragment  extends Fragment {

    boolean quest_01, quest_02;

    int printAllCount, printAllCount_02;

    int ans_01 = 0;
    int ans_02 = 0;
    int ans_03 = 0;

    int ans_02_01 = 0;
    int ans_02_02 = 0;
    int ans_02_03 = 0;

    LinearLayout llAllVoices_01, llBlokAnswer_01, llBlokQuestion_01;
    TextView question_01, tvViewAnswer_01, tvViewAnswer_02, tvViewAnswer_03;
    RadioButton answer_01, answer_02, answer_03;
    ProgressBar progressBar_01, progressBar_02, progressBar_03;
    //--------------------------------------------------
    LinearLayout llAllVoices_02, llBlokAnswer_02, llBlokQuestion_02;

    TextView question_02_01, tvViewAnswer_02_01, tvViewAnswer_02_02, tvViewAnswer_02_03;
    RadioButton answer_02_01, answer_02_02, answer_02_03;
    ProgressBar progressBar_02_01, progressBar_02_02, progressBar_02_03;

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mConditionRef_01 = mRootRef.child("question_01");
    DatabaseReference mConditionRef_02 = mRootRef.child("question_02");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second, container, false);

        //Заполняем переменные значением из облака для инкремента голоса
        nnn();
        llAllVoices_01 = (LinearLayout) view.findViewById(R.id.llAllVoices_01);
        llBlokAnswer_01 = (LinearLayout) view.findViewById(R.id.llBlokAnswer_01);
        llBlokQuestion_01 = (LinearLayout) view.findViewById(R.id.llBlokQuestion_01);
        question_01 = (TextView) view.findViewById(R.id.question_01);
        tvViewAnswer_01 = (TextView) view.findViewById(R.id.tvViewAnswer_01);
        tvViewAnswer_02 = (TextView) view.findViewById(R.id.tvViewAnswer_02);
        tvViewAnswer_03 = (TextView) view.findViewById(R.id.tvViewAnswer_03);
        answer_01 = (RadioButton) view.findViewById(R.id.answer_01);
        answer_02 = (RadioButton) view.findViewById(R.id.answer_02);
        answer_03 = (RadioButton) view.findViewById(R.id.answer_03);
        progressBar_01 = (ProgressBar) view.findViewById(R.id.progressBar_01);
        progressBar_02 = (ProgressBar) view.findViewById(R.id.progressBar_02);
        progressBar_03 = (ProgressBar) view.findViewById(R.id.progressBar_03);
        //-----------------------------------------------
        question_02_01 = (TextView) view.findViewById(R.id.question_02_01);

        llAllVoices_02 = (LinearLayout) view.findViewById(R.id.llAllVoices_02);
        llBlokAnswer_02 = (LinearLayout) view.findViewById(R.id.llBlokAnswer_02);
        llBlokQuestion_02 = (LinearLayout) view.findViewById(R.id.llBlokQuestion_02);
        tvViewAnswer_02_01 = (TextView) view.findViewById(R.id.tvViewAnswer_02_01);
        tvViewAnswer_02_02 = (TextView) view.findViewById(R.id.tvViewAnswer_02_02);
        tvViewAnswer_02_03 = (TextView) view.findViewById(R.id.tvViewAnswer_02_03);
        answer_02_01 = (RadioButton) view.findViewById(R.id.answer_02_01);
        answer_02_02 = (RadioButton) view.findViewById(R.id.answer_02_02);
        answer_02_03 = (RadioButton) view.findViewById(R.id.answer_02_03);
        progressBar_02_01 = (ProgressBar) view.findViewById(R.id.progressBar_02_01);
        progressBar_02_02 = (ProgressBar) view.findViewById(R.id.progressBar_02_02);
        progressBar_02_03 = (ProgressBar) view.findViewById(R.id.progressBar_02_03);
        quest01();
        quest02();

        return view;
    }

    private void quest01(){
        MyApplication myApplication = (MyApplication) getActivity().getApplication();
        if (!myApplication.getViewQuestion()){
            llBlokQuestion_01.setVisibility(View.VISIBLE);
            llBlokAnswer_01.setVisibility(View.GONE);
            llAllVoices_01.setVisibility(View.GONE);
        } else {
            llBlokQuestion_01.setVisibility(View.GONE);
            llBlokAnswer_01.setVisibility(View.VISIBLE);
            llAllVoices_01.setVisibility(View.VISIBLE);
        }

        answer_01.setOnClickListener(radioButtonClickListener);
        answer_02.setOnClickListener(radioButtonClickListener);
        answer_03.setOnClickListener(radioButtonClickListener);

        //Получаем и выводим общее количество проголосовавших
        mConditionRef_01.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                printAllCount = 0;
                for (DataSnapshot postSnapShot : dataSnapshot.getChildren()){
                    int i = postSnapShot.getValue(Integer.class);
                    printAllCount = printAllCount + i;
                    progressBar_01.setMax(printAllCount);
                    progressBar_02.setMax(printAllCount);
                    progressBar_03.setMax(printAllCount);
                }
                question_01.setText(String.valueOf(printAllCount));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        //Получаем и выводим количество голосов за первый вариант
        mConditionRef_01.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot post = dataSnapshot.child("answer_01");
                tvViewAnswer_01.setText(String.valueOf(post.getValue(Integer.class)));
                progressBar_01.setProgress(post.getValue(Integer.class));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        //Получаем и выводим количество голосов за второй вариант
        mConditionRef_01.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot post = dataSnapshot.child("answer_02");
                tvViewAnswer_02.setText(String.valueOf(post.getValue(Integer.class)));
                progressBar_02.setProgress(post.getValue(Integer.class));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        //Получаем и выводим количество голосов за третий вариант
        mConditionRef_01.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot post = dataSnapshot.child("answer_03");
                tvViewAnswer_03.setText(String.valueOf(post.getValue(Integer.class)));
                progressBar_03.setProgress(post.getValue(Integer.class));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void quest02(){
        MyApplication myApplication = (MyApplication) getActivity().getApplication();
        if (!myApplication.getViewQuestion_02()){
            llBlokQuestion_02.setVisibility(View.VISIBLE);
            llBlokAnswer_02.setVisibility(View.GONE);
            llAllVoices_02.setVisibility(View.GONE);
        } else {
            llBlokQuestion_02.setVisibility(View.GONE);
            llBlokAnswer_02.setVisibility(View.VISIBLE);
            llAllVoices_02.setVisibility(View.VISIBLE);
        }

        answer_02_01.setOnClickListener(radioButtonClickListener_02);
        answer_02_02.setOnClickListener(radioButtonClickListener_02);
        answer_02_03.setOnClickListener(radioButtonClickListener_02);

        //Получаем и выводим общее количество проголосовавших
        mConditionRef_02.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                printAllCount_02 = 0;
                for (DataSnapshot postSnapShot : dataSnapshot.getChildren()){
                    int i = postSnapShot.getValue(Integer.class);
                    printAllCount_02 = printAllCount_02 + i;
                    progressBar_02_01.setMax(printAllCount_02);
                    progressBar_02_02.setMax(printAllCount_02);
                    progressBar_02_03.setMax(printAllCount_02);
                }
                question_02_01.setText(String.valueOf(printAllCount_02));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        //Получаем и выводим количество голосов за первый вариант
        mConditionRef_02.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot post = dataSnapshot.child("answer_01");
                tvViewAnswer_02_01.setText(String.valueOf(post.getValue(Integer.class)));
                progressBar_02_01.setProgress(post.getValue(Integer.class));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        //Получаем и выводим количество голосов за второй вариант
        mConditionRef_02.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot post = dataSnapshot.child("answer_02");
                tvViewAnswer_02_02.setText(String.valueOf(post.getValue(Integer.class)));
                progressBar_02_02.setProgress(post.getValue(Integer.class));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        //Получаем и выводим количество голосов за третий вариант
        mConditionRef_02.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot post = dataSnapshot.child("answer_03");
                tvViewAnswer_02_03.setText(String.valueOf(post.getValue(Integer.class)));
                progressBar_02_03.setProgress(post.getValue(Integer.class));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    View.OnClickListener radioButtonClickListener = new View.OnClickListener() {
//        MyApplication myApplication = (MyApplication) getActivity().getApplication();
        @Override
        public void onClick(View v) {
            RadioButton rb = (RadioButton) v;
            switch (rb.getId()){
                case R.id.answer_01:
                    mConditionRef_01.child("answer_01").setValue(ans_01 + 1);
//                    myApplication.setViewQuestion();
                    viewAnswers();
                    break;
                case R.id.answer_02:
                    mConditionRef_01.child("answer_02").setValue(ans_02 + 1);
//                    myApplication.setViewQuestion();
                    viewAnswers();
                    break;
                case R.id.answer_03:
                    mConditionRef_01.child("answer_03").setValue(ans_03 + 1);
//                    myApplication.setViewQuestion();
                    viewAnswers();
                    break;
                default:
                    break;
            }
        }
    };

    View.OnClickListener radioButtonClickListener_02 = new View.OnClickListener() {
//        MyApplication myApplication = (MyApplication) getActivity().getApplication();
        @Override
        public void onClick(View v) {
            RadioButton rb = (RadioButton) v;
            switch (rb.getId()){
                case R.id.answer_02_01:
                    mConditionRef_02.child("answer_01").setValue(ans_02_01 + 1);
//                    myApplication.setViewQuestion_02();
                    viewAnswers_02();
                    break;
                case R.id.answer_02_02:
                    mConditionRef_02.child("answer_02").setValue(ans_02_02 + 1);
//                    myApplication.setViewQuestion_02();
                    viewAnswers_02();
                    break;
                case R.id.answer_02_03:
                    mConditionRef_02.child("answer_03").setValue(ans_02_03 + 1);
//                    myApplication.setViewQuestion_02();
                    viewAnswers_02();
                    break;
                default:
                    break;
            }
        }
    };

    private void viewAnswers(){
        MyApplication myApplication = (MyApplication) getActivity().getApplication();
        llBlokQuestion_01.setVisibility(View.GONE);
        llBlokAnswer_01.setVisibility(View.VISIBLE);
        llAllVoices_01.setVisibility(View.VISIBLE);
        myApplication.setViewQuestion();
    }

    private void viewAnswers_02(){
        MyApplication myApplication = (MyApplication) getActivity().getApplication();
        llBlokQuestion_02.setVisibility(View.GONE);
        llBlokAnswer_02.setVisibility(View.VISIBLE);
        llAllVoices_02.setVisibility(View.VISIBLE);
        myApplication.setViewQuestion_02();
    }

    private void nnn (){
        mConditionRef_01.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot dsAns_01 = dataSnapshot.child("answer_01");
                DataSnapshot dsAns_02 = dataSnapshot.child("answer_02");
                DataSnapshot dsAns_03 = dataSnapshot.child("answer_03");
                ans_01 = dsAns_01.getValue(Integer.class);
                ans_02 = dsAns_02.getValue(Integer.class);
                ans_03 = dsAns_03.getValue(Integer.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        mConditionRef_02.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot dsAns_02_01 = dataSnapshot.child("answer_01");
                DataSnapshot dsAns_02_02 = dataSnapshot.child("answer_02");
                DataSnapshot dsAns_02_03 = dataSnapshot.child("answer_03");
                ans_02_01 = dsAns_02_01.getValue(Integer.class);
                ans_02_02 = dsAns_02_02.getValue(Integer.class);
                ans_02_03 = dsAns_02_03.getValue(Integer.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}