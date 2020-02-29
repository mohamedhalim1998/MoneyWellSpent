package com.mohamed.halim.essa.moneywellspent.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mohamed.halim.essa.moneywellspent.R;
import com.mohamed.halim.essa.moneywellspent.adapters.PaymentAdapter;
import com.mohamed.halim.essa.moneywellspent.data.PaymentEntry;
import com.mohamed.halim.essa.moneywellspent.data.PaymentViewModel;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener ,
PaymentAdapter.PaymentClickListener{
    RecyclerView mRecyclerView;
    PaymentAdapter mAdapter;
    TextView mRemain, mTotal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // initial the views
        mRecyclerView = findViewById(R.id.main_recycle_view);
        mTotal = findViewById(R.id.summary_total);
        mRemain = findViewById(R.id.summary_remain);
        FloatingActionButton fab = findViewById(R.id.fab);
        // set onClick on the floating action button to open PaymentActivity
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, PaymentActivity.class);
                i.setAction(PaymentActivity.ACTION_ADD_NEW);
                startActivity(i);
            }
        });

        // configure Recycle view
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new PaymentAdapter(null, this);
        mRecyclerView.setAdapter(mAdapter);
        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(decoration);

        // get the data from a view model
        PaymentViewModel viewModel = ViewModelProviders.of(this).get(PaymentViewModel.class);
        viewModel.getPayments().observe(this, new Observer<List<PaymentEntry>>() {
            @Override
            public void onChanged(List<PaymentEntry> list) {
                mAdapter.swapList(list);
            }
        });
        //initialize the shared preference
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
        // set the summary section
        setSummary();


    }

    /**
     * set the values of the total and remain section
     */
    private void setSummary() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!sharedPreferences.contains(SettingsActivity.PREFERENCE_TOTAL_KEY)) {
            sharedPreferences.edit().putFloat(SettingsActivity.PREFERENCE_TOTAL_KEY, SettingsActivity.PREFERENCE_TOTAL_DEFAULT).apply();
        }
        float total = sharedPreferences.getFloat(SettingsActivity.PREFERENCE_TOTAL_KEY, SettingsActivity.PREFERENCE_TOTAL_DEFAULT);
        float remain = Float.parseFloat(sharedPreferences.getString(getString(R.string.amount_setting_key),
                getString(R.string.amount_setting_default))) - total;

        mTotal.setText(getString(R.string.summary_total, total));
        mRemain.setText(getString(R.string.summary_remain, remain));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        setSummary();
    }

    @Override
    public void onPaymentClickListener(long id) {
        Intent i = new Intent(this, PaymentActivity.class);
        i.setAction(PaymentActivity.ACTION_EDIT_EXIST);
        i.putExtra(PaymentActivity.PAYMENT_ID_EXTRA, id);
        startActivity(i);
    }
}
