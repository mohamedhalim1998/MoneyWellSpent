package com.mohamed.halim.essa.moneywellspent.data;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class PaymentViewModel extends AndroidViewModel {
    private LiveData<List<PaymentEntry>> payments;
    private PaymentDatabase db;
    public PaymentViewModel(@NonNull Application application) {
        super(application);
        db = PaymentDatabase.getInstance(this.getApplication());
        payments = db.paymentDAO().loadAllPayments();
    }

    public LiveData<List<PaymentEntry>> getPayments() {
        return payments;
    }

  /*  public LiveData<PaymentEntry> getPaymentByID(long id){
        return  db.paymentDAO().loadPaymentById(id);
    }*/
}
