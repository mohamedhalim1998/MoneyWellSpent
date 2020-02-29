package com.mohamed.halim.essa.moneywellspent.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mohamed.halim.essa.moneywellspent.R;
import com.mohamed.halim.essa.moneywellspent.data.PaymentEntry;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.PaymentHolder> {
    private List<PaymentEntry> payments;
    private PaymentClickListener paymentClickListener;

    public PaymentAdapter(List<PaymentEntry> payments, PaymentClickListener paymentClickListener) {
        this.payments = payments;
        this.paymentClickListener = paymentClickListener;
    }

    /**
     * @param parent   : of the view to create
     * @param viewType : not used
     * @return a new paymentholder for the view
     */
    @NonNull
    @Override
    public PaymentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.payment_list_item, parent, false);

        return new PaymentHolder(view);
    }

    /**
     * @param holder   : holding the view to bind
     * @param position : of the item
     */
    @Override
    public void onBindViewHolder(@NonNull PaymentHolder holder, int position) {
        // bind the holder with data
        holder.bind(payments.get(position));
    }

    /**
     * @return the count of the list items
     */
    @Override
    public int getItemCount() {
        if (payments == null) return 0;
        return payments.size();
    }

    /**
     * swap the data list
     *
     * @param list : to swap with
     */
    public void swapList(List<PaymentEntry> list) {
        payments = list;
        notifyDataSetChanged();
    }

    public interface PaymentClickListener {
        void onPaymentClickListener(long id);
    }

    public class PaymentHolder extends RecyclerView.ViewHolder  implements  View.OnClickListener{
        private TextView day;
        private TextView month;
        private TextView amount;
        private TextView category;
        private ImageView note;
        private Context mContext;
        public PaymentHolder(@NonNull View view) {
            super(view);
            view.setOnClickListener(this);
            mContext = view.getContext();
            day = view.findViewById(R.id.day_list_item);
            month = view.findViewById(R.id.month_list_item);
            amount = view.findViewById(R.id.amount_list_item);
            category = view.findViewById(R.id.category_list_item);
            note = view.findViewById(R.id.note_list_item);
        }

        /**
         * bind the holder with data
         *
         * @param payment : to bind
         */
        public void bind(PaymentEntry payment) {
            SimpleDateFormat format = new SimpleDateFormat("dd MMM, YYYY");
            Date date = new Date(payment.getPaymentDate());
            day.setText(format.format(date).substring(0, 2));
            month.setText(format.format(date).substring(2));
            String[] categories  = mContext.getResources().getStringArray(R.array.category_array);
            category.setText(categories[payment.getCategory()]);
            amount.setText("" + payment.getAmount());
            if (!TextUtils.isEmpty(payment.getNote()))
                note.setVisibility(View.VISIBLE);
        }

        /*private int setCategory(int category) {
            switch (category) {
                case PaymentEntry.CATEGORY_FOOD:
                    return R.string.category_food;

                case PaymentEntry.CATEGORY_ENTERTAINMENT:
                    return R.string.category_entertainment;

                case PaymentEntry.CATEGORY_BILL:
                    return R.string.category_bill;

                case PaymentEntry.CATEGORY_HEALTH:
                    return R.string.category_health;

                case PaymentEntry.CATEGORY_OTHER:
                    return R.string.category_other;
            }
            return R.string.category_other;
        }*/


        @Override
        public void onClick(View v) {
            long id = payments.get(getAdapterPosition()).getId();
            paymentClickListener.onPaymentClickListener(id);
        }
    }
}
