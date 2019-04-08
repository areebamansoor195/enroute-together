package com.example.areebamansoor.enroutetogether.gmail;

import android.os.AsyncTask;

import java.util.List;


public class SendEmailTask extends AsyncTask {


    public SendEmailTask() {

    }

    protected void onPreExecute() {
    }

    @Override
    protected Object doInBackground(Object... args) {
        try {
            Gmail androidEmail = new Gmail(args[0].toString(),
                    args[1].toString(), (List) args[2], args[3].toString(),
                    args[4].toString());
            androidEmail.createEmailMessage();
            androidEmail.sendEmail();
        } catch (Exception e) {
        }
        return null;
    }

    @Override
    public void onProgressUpdate(Object... values) {

    }

    @Override
    public void onPostExecute(Object result) {
    }

}
