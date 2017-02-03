package ru.novil.sergey.academegtruestories.other;

import android.app.Application;

public class MyApplication extends Application {

    private String pageToken = "";
    private boolean bPageToken;

    private String pageTokenAca = "";
    private String lastVideoId;
    private String pageTokenAca2nd = "";
    private int iJsons, iSQLites;
    private boolean bViewQuestion, bViewQuestion_02;

    public boolean getViewQuestion_02(){
        return bViewQuestion_02;
    }

    public boolean getViewQuestion(){
        return bViewQuestion;
    }

    public String getPageToken(){
        return pageToken;
    }

    public String getPageTokenAca(){
        return pageTokenAca;
    }

    public String getPageTokenAca2nd(){
        return pageTokenAca2nd;
    }

    public int getJsonCount(){
        return iJsons;
    }

    public int getSQLiteCount(){
        return iSQLites;
    }

    public boolean getbPageToken(){
        return bPageToken;
    }

    //-------------------------------

    public void setbPageToken(boolean bbPageToken){
        bPageToken = bbPageToken;
    }

    public void setLastVideoId(String videoIdMA){
        lastVideoId = "";
        lastVideoId = videoIdMA;
    }

    public void setViewQuestion_02(){
        bViewQuestion_02 = true;
    }

    public void setViewQuestion(){
        bViewQuestion = true;
    }

    public void setJsonCountAsZero(){
        iJsons = 0;
    }

    public void setJsonCount(int iJson){
        iJsons = iJsons + iJson;
    }

    public void setSQLiteCount(int iSQLite){
        iSQLites = iSQLite;
    }

    public void setPageTokenAca(String sPageToken){
        pageTokenAca = sPageToken;
    }

    public void setPageToken(String sPageTokenN){
        pageToken = sPageTokenN;
    }

    public void setPageTokenAca2nd(String sPageToken2nd){
        pageTokenAca2nd = sPageToken2nd;
    }
}
