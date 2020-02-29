package com.mohamed.halim.essa.moneywellspent.data;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "payment")
public class PaymentEntry {
  /*  @Ignore
    public static final int CATEGORY_FOOD = 0;
    @Ignore
    public static final int CATEGORY_ENTERTAINMENT = 1;
    @Ignore
    public static final int CATEGORY_HEALTH = 2;
    @Ignore
    public static final int CATEGORY_BILL = 3;
    @Ignore
    public static final int CATEGORY_OTHER = 4;*/

    @PrimaryKey(autoGenerate = true)
    private long id;
    private long paymentDate;
    private float amount;
    private String note;
    private int category;

    @Ignore
    public PaymentEntry(long paymentDate, float amount, String note, int category) {
        this.paymentDate = paymentDate;
        this.amount = amount;
        this.note = note;
        this.category = category;
    }

    public PaymentEntry(long id, long paymentDate, float amount, String note, int category) {
        this.id = id;
        this.paymentDate = paymentDate;
        this.amount = amount;
        this.note = note;
        this.category = category;
    }

    public long getId() {
        return id;
    }

    public long getPaymentDate() {
        return paymentDate;
    }

    public float getAmount() {
        return amount;
    }

    public String getNote() {
        return note;
    }

    public int getCategory() {
        return category;
    }

    public void setId(long id) {
        this.id = id;
    }
}
