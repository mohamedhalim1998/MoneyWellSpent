package com.mohamed.halim.essa.moneywellspent.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
@Database(entities = {PaymentEntry.class}, version = 1, exportSchema = false)
public abstract class PaymentDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "payment";
    private static PaymentDatabase sInstance;

    public static PaymentDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (new Object()) {
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        PaymentDatabase.class, PaymentDatabase.DATABASE_NAME)
                        .build();
            }
        }
        return sInstance;
    }

    public abstract PaymentDAO paymentDAO();
}
