package com.mohamed.halim.essa.moneywellspent.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.mohamed.halim.essa.moneywellspent.R;
import com.mohamed.halim.essa.moneywellspent.data.PaymentDatabase;
import com.mohamed.halim.essa.moneywellspent.data.PaymentEntry;
import com.mohamed.halim.essa.moneywellspent.data.PaymentExecutor;
import com.mohamed.halim.essa.moneywellspent.data.PaymentViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PaymentActivity extends AppCompatActivity implements Runnable, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    public static final String TAG = PaymentActivity.class.getSimpleName();
    public static final String ACTION_ADD_NEW = "add-new";
    public static final String ACTION_EDIT_EXIST = "edit-exist";
    public static final String PAYMENT_ID_EXTRA = "id-extra";
    private EditText mNoteEditText, mAmountEditText;
    private TextView mDateTextView, mTimeTextView;
    private long mDate;
    private Spinner mCategorySpinner;
    ArrayAdapter<CharSequence> mAdapter;
    private long mPaymentId;
    private float mPrevAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        Intent i = getIntent();
        String action = i.getAction();
        mPaymentId = -1;
        mDate = System.currentTimeMillis();
        // setup the view
        mAmountEditText = findViewById(R.id.payment_amount);
        mCategorySpinner = findViewById(R.id.paymnet_category_spinner);
        mNoteEditText = findViewById(R.id.payment_note);
        mDateTextView = findViewById(R.id.payment_date);
        mTimeTextView = findViewById(R.id.payment_time);
        // set up the spinner
        mAdapter = ArrayAdapter.createFromResource(this, R.array.category_array, android.R.layout.simple_spinner_dropdown_item);
        mCategorySpinner.setAdapter(mAdapter);
        // set date text
        setDateText(mDate);
        setTimeText(mDate);
        if (action != null && action.equals(ACTION_EDIT_EXIST)) {
            mPaymentId = i.getLongExtra(PAYMENT_ID_EXTRA, -1);
            populateUi();
        }
        // open the date dialog to pick a date of payment
        mDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(mDate);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                int mMon = c.get(Calendar.MONTH);
                int mYear = c.get(Calendar.YEAR);
                DatePickerDialog datePickerDialog =
                        new DatePickerDialog(PaymentActivity.this, PaymentActivity.this, mYear, mMon, mDay);
                datePickerDialog.show();
            }
        });
        mTimeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                int mHour = c.get(Calendar.HOUR);
                int mMin = c.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog =
                        new TimePickerDialog(PaymentActivity.this, PaymentActivity.this, mHour, mMin, false);
                timePickerDialog.show();
            }
        });

    }

    /**
     * set the date of the date text view to the format "dd/MM/YYYY"
     *
     * @param date : to format and set
     */
    private void setDateText(long date) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/YYYY", Locale.getDefault());
        Date d = new Date(date);
        mDateTextView.setText(format.format(d));
    }

    /**
     * set the time of the time text view to the format "hh:mm a"
     *
     * @param date : to format and set
     */
    private void setTimeText(long date) {
        SimpleDateFormat format = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        Date d = new Date(date);
        mTimeTextView.setText(format.format(d));
    }

    /**
     * populate the ui element if want to edit a payment
     */
    private void populateUi() {
        PaymentViewModel viewModel = ViewModelProviders.of(this).get(PaymentViewModel.class);
        PaymentExecutor.getInstance().diskIO().execute(this);

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mPaymentId == -1) {
            menu.findItem(R.id.action_delete_payment).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.payment_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_save_payment:
                try {
                    savePayment();
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Empty amount nothing is saved", Toast.LENGTH_SHORT).show();
                } finally {
                    finish();
                }

                break;
            case R.id.action_delete_payment:
                deletePayment();
                finish();
                break;
        }
        return true;
    }

    /**
     * delete a payment form the DB
     */
    private void deletePayment() {
        PaymentExecutor.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                updateSharedPreference(0 - mPrevAmount);
                PaymentEntry p = PaymentDatabase.getInstance(PaymentActivity.this).paymentDAO().loadPaymentById(mPaymentId);
                PaymentDatabase.getInstance(PaymentActivity.this).paymentDAO().deletePayment(p);
            }
        });
    }

    /**
     * save the values of th payment into the DB
     */
    private void savePayment() {

        final float amount = Float.valueOf(mAmountEditText.getText().toString());
        String note = mNoteEditText.getText().toString().trim();
        int category = mCategorySpinner.getSelectedItemPosition();
        long date = mDate;
        final PaymentEntry payment = new PaymentEntry(date, amount, note, category);
        PaymentExecutor.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (mPaymentId == -1) {
                    updateSharedPreference(amount);
                    PaymentDatabase.getInstance(PaymentActivity.this).paymentDAO().insertPayment(payment);
                } else {
                    payment.setId(mPaymentId);
                    updateSharedPreference(0 - mPrevAmount);
                    updateSharedPreference(amount);
                    PaymentDatabase.getInstance(PaymentActivity.this).paymentDAO().updatePayment(payment);
                }
            }
        });
    }

    /**
     * update the total value of the shared preference with the given amount
     *
     * @param amount : to add to the data base
     */
    private void updateSharedPreference(float amount) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        float total = preferences.getFloat(SettingsActivity.PREFERENCE_TOTAL_KEY, SettingsActivity.PREFERENCE_TOTAL_DEFAULT);
        preferences.edit().putFloat(SettingsActivity.PREFERENCE_TOTAL_KEY, total + amount).apply();
    }

    /**
     * observer to the live date
     */
    @Override
    public void run() {
        PaymentEntry p = PaymentDatabase.getInstance(this).paymentDAO().loadPaymentById(mPaymentId);
        float amount = p.getAmount();
        mPrevAmount = amount;
        String note = p.getNote();
        int category = p.getCategory();
        mDate = p.getPaymentDate();
        mNoteEditText.setText(note);
        setDateText(mDate);
        setTimeText(mDate);
        mCategorySpinner.setSelection(category);
        mAmountEditText.setText(String.valueOf(amount));
    }

    /**
     * date from the date picker
     */
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(year, month, dayOfMonth);
        mDate = c.getTimeInMillis();
        setDateText(mDate);
        setTimeText(mDate);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        mDate = c.getTimeInMillis();
        setTimeText(mDate);
    }
}
