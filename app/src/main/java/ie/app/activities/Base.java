package ie.app.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.DropBoxManager;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;



import java.util.ArrayList;
import java.util.List;

import ie.app.R;
import ie.app.api.DonationApi;
import ie.app.main.DonationApp;
import ie.app.models.Donation;

// base activity
// AppCompatActivity: Base class for activities that wish to use some of the newer platform features
// on older Android devices
public class Base extends AppCompatActivity
{
    public DonationApp app;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        app = (DonationApp) getApplication();
        new GetAllTask(this).execute("/donations");


//        Log.v(String.valueOf(app.donations.get(0)), "log5");
//        new GetAllTask(this).execute("/donations");

//        app.dbManager.open();
//        app.dbManager.setTotalDonated(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        app.dbManager.close();
    }

//    public boolean newDonation(Donation donation)
//    {
//        boolean targetAchieved = totalDonated > target;
//        if (!targetAchieved)
//        {
//            app.donations.add(donation);
//            totalDonated += donation.amount;
//        }
//        else
//        {
//            Toast toast = Toast.makeText(this, "Target Exceeded!", Toast.LENGTH_SHORT);
//            toast.show();}
//        return targetAchieved;
//    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_donate, menu);
        return true;
    }


    @Override
    public boolean onPrepareOptionsMenu (Menu menu){
//        Log.i(String.valueOf(menu),"log 1");
        super.onPrepareOptionsMenu(menu);
        MenuItem report = menu.findItem(R.id.menuReport);
        MenuItem donate = menu.findItem(R.id.menuDonate);
        MenuItem reset = menu.findItem(R.id.menuReset);

        //get donations list from db, while doing this show the dialog
//        Log.i(String.valueOf(app.donations),"log4");

        if(app.donations.size() == 0)
        {
            report.setEnabled(false);
            reset.setEnabled(false);
        }
        else {
            report.setEnabled(true);
            reset.setEnabled(true);
        }
        if (this instanceof Donate){
            donate.setVisible(false);
            if(app.donations.size() == 0)
            {
                report.setVisible(true);
//                reset.setEnabled(true);
            }
        }
        else {
            report.setVisible(false);
            donate.setVisible(true);
            reset.setVisible(false);
        }
        return true;
    }
//    public void settings(MenuItem item)
//    {
//        Toast.makeText(this, "Settings Selected", Toast.LENGTH_SHORT).show();
//    }
    public void report(MenuItem item)
    {
        startActivity (new Intent(this, Report.class));
    }
    public void donate(MenuItem item)
    {
        startActivity (new Intent(this, Donate.class));
    }

    public void reset(MenuItem item) {

//        app.totalDonated = 0;
        app.donations.clear();

    }

    private class GetAllTask extends AsyncTask<String, Void, List<Donation>> {

        protected ProgressDialog dialog;
        protected Context context;

        public GetAllTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog = new ProgressDialog(context, 1);
            this.dialog.setMessage("Retrieving Donations List");
            this.dialog.show();
        }

        @Override
        protected List<Donation> doInBackground(String... params) {

            try {

                return (List<Donation>) DonationApi.getAll((String) params[0]);
            } catch (Exception e) {
                Log.v("ASYNC", "ERROR : " + e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Donation> result) {
            super.onPostExecute(result);

            app.donations = result;
            if (dialog.isShowing())
                dialog.dismiss();
        }
    }

}