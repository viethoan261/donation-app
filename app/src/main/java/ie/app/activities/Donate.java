package ie.app.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AlertDialog;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import ie.app.R;
import ie.app.api.DonationApi;
import ie.app.models.Donation;

public class Donate extends Base{
    private Button donateButton;
    private RadioGroup paymentMethod;
    private ProgressBar progressBar;
    private NumberPicker amountPicker;
    private EditText amountText;
    private TextView amountTotal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.content_donate);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(V iew view) {
//                Snackbar.make(view, "Replace with your own action",
//                        Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        donateButton = (Button) findViewById(R.id.donateButton);
        paymentMethod = (RadioGroup) findViewById(R.id.paymentMethod);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        amountPicker = (NumberPicker) findViewById(R.id.amountPicker);
        amountText = (EditText) findViewById(R.id.paymentAmount);
        amountTotal = (TextView) findViewById(R.id.totalSoFar);
//        int allAmount = 0;
//        List<Donation> data = DonationApi.getAll((String) "/donations");
//        for (int i = 0 ; i < data.size() ; i ++) {
//            allAmount += data.get(i).amount;
//        }
//        if (app.totalDonated == 0 && allAmount != 0) {
//            app.totalDonated = allAmount;
//        }

        amountPicker.setMinValue(0);
        amountPicker.setMaxValue(1000);
        progressBar.setMax(10000);
        amountTotal.setText("Total so far $"+ app.totalDonated);
        progressBar.setProgress(app.totalDonated);
    }

    public void donateButtonPressed (View view)
    {
        if (app.totalDonated < app.target) {
            String method = paymentMethod.getCheckedRadioButtonId() ==
                    R.id.paypal ? "PayPal" : "Direct";
            int donatedAmount = amountPicker.getValue();
            if (donatedAmount == 0) {
                String text = amountText.getText().toString();
                if (!text.equals(""))
                    donatedAmount = Integer.parseInt(text);
            }
            if (donatedAmount > 0) {
                app.newDonation(new Donation(donatedAmount, method, 0));
                progressBar.setProgress(app.totalDonated);
                String totalDonatedStr = "Total so far $" + app.totalDonated;
                amountTotal.setText(totalDonatedStr);
                new InsertTask(this).execute("/donations");
            }
        } else {
            Toast.makeText(this, "Target Reached!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void reset(MenuItem item) {

//        Menu menu = new Menu;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Donation?");
        builder.setIcon(android.R.drawable.ic_delete);
        builder.setMessage("Are you sure you want to Delete ALL the Donations?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                new ResetTask(Donate.this).execute("/donations");
                progressBar.setProgress(0);
                app.totalDonated = 0;
                amountTotal.setText("Total so far $" + app.totalDonated);
                Donate.super.reset(item);


            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create() ;
        alert.show();
    }
    private class ResetTask extends AsyncTask<Object, Void, String> {
        protected ProgressDialog dialog;
        protected Context context;
        public ResetTask(Context context)
        {
            this.context = context;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog = new ProgressDialog(context, 1);
            this.dialog.setMessage("Deleting Donations....");
            this.dialog.show();
        }
        @Override
        protected String doInBackground(Object... params) {
            String res = null;
            try {
                res = DonationApi.deleteAll((String)params[0]);
            }
            catch(Exception e)
            {
                Log.v("donate"," RESET ERROR : " + e);
                e.printStackTrace();
            }
            return res;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            app.totalDonated = 0;
            progressBar.setProgress(app.totalDonated);
            amountTotal.setText("Total so far $" + app.totalDonated);
            if (dialog.isShowing())
                dialog.dismiss();
        }
    }
    private class InsertTask extends AsyncTask<Object, Void, String> {
        protected ProgressDialog dialog;
        protected Context context;
        public InsertTask(Context context)
        {
            this.context = context;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog = new ProgressDialog(context, 1);
            this.dialog.setMessage("Saving Donation....");
            this.dialog.show();
        }
        @Override
        protected String doInBackground(Object... params) {
            String method = paymentMethod.getCheckedRadioButtonId() ==
                    R.id.paypal ? "PayPal" : "Direct";

            DonationApi.insert(params[0].toString(),new Donation(
                    amountPicker.getValue() == 0 ?Integer.parseInt(amountText.getText().toString()) : amountPicker.getValue(),method, 0) );
            String res = null;

            try {
                Log.v("donate", "Donation App Inserting");
            }
            catch(Exception e)
            {
                Log.v("donate","ERROR : " + e);
                e.printStackTrace();
            }
            return res;
        }
        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);

            progressBar.setProgress(app.totalDonated);
            amountTotal.setText("Total so far $" + app.totalDonated);

            if (dialog.isShowing())
                dialog.dismiss();
        }
    }

}