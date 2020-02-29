package com.mohamed.halim.essa.moneywellspent.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
@Dao
public interface PaymentDAO  {
    @Query("SELECT * FROM payment ORDER BY paymentDate")
     LiveData<List<PaymentEntry>> loadAllPayments();
    @Query("SELECT * FROM payment WHERE id = :id")
    PaymentEntry loadPaymentById(long id);
    @Insert
    void insertPayment(PaymentEntry taskEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updatePayment(PaymentEntry taskEntry);

    @Delete
    void deletePayment(PaymentEntry taskEntry);
}
