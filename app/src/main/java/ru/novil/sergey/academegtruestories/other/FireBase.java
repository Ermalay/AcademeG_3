package ru.novil.sergey.academegtruestories.other;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class FireBase {

    int poiu = 1;
    int iAnswer = 0;

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
//    DatabaseReference mConditionRef = mRootRef.child("cond");
    DatabaseReference mConditionRef;// = mRootRef.child("question_01");


    public void fbOnClick(String question, final String answer){
        //сначала получаем число в конкретном ответе
//        final String aaa = answer;
        DatabaseReference mConditionRef_2 = mRootRef.child(question);
        mConditionRef_2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot post = dataSnapshot.child(answer);
                iAnswer = post.getValue(Integer.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        // Добавляем к ответу единичку
        mConditionRef = mRootRef.child(question).child(answer);
        mConditionRef.setValue(iAnswer + 1);
//        poiu = poiu + 1;

    }

    private void cool(){
        // Добавляем к ответу единичку

    }

    public void retur(){
        mConditionRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Voices voices  = dataSnapshot.getValue(Voices.class);
//                voices.getAns1();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }




    private void checkCount(){
        mConditionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Voices voices = dataSnapshot.getValue(Voices.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public class Voices {
        public int ans_1;
        public int ans_2;
        public int ans_3;

        public Voices(){
        }

        public Voices(int ans_1, int ans_2, int ans_3){
            this.ans_1 = ans_1;
            this.ans_2 = ans_2;
            this.ans_3 = ans_3;
        }

        public void setAns_1(){

        }

        public int getAns1(){
            return ans_1;
        }
    }

}
