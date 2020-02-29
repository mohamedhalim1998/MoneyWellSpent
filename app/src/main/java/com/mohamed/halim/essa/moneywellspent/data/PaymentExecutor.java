package com.mohamed.halim.essa.moneywellspent.data;

import android.os.Looper;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PaymentExecutor {


    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static PaymentExecutor sInstance;
    private final Executor diskIO;


    private PaymentExecutor(Executor diskIO) {
        this.diskIO = diskIO;

    }

    public static PaymentExecutor getInstance() {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new PaymentExecutor(Executors.newSingleThreadExecutor());
            }
        }
        return sInstance;
    }

    public Executor diskIO() {
        return diskIO;
    }

}

