package com.example.gwarancja;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ReceiptAdapter extends FirestoreRecyclerAdapter<Receipt, ReceiptAdapter.ReceiptHolder> {

    private OnItemClickListener listener;
    private String endDate;
    private Integer daysleft;


    public ReceiptAdapter(@NonNull FirestoreRecyclerOptions<Receipt> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ReceiptHolder holder, int position, @NonNull Receipt model) {
        holder.textViewProduct.setText(model.getProduct());
        holder.textViewDate.setText("Data zakupu: " + model.getDate());
        holder.textViewYears.setText("Lata gwarancj: " + String.valueOf(model.getYears()));

        endDate = model.getEndDate();
        final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date mDate = dateFormat.parse(endDate);
            Calendar cal = Calendar.getInstance();

            daysleft = daysBetween(cal.getTime(), mDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }

         if(daysleft<365){
             holder.viewTextDaysLeft.setText(daysleft + " dni");
         } else if (daysleft>365 && daysleft<730) {
            Integer daysLeft = daysleft - 365;
            holder.viewTextDaysLeft.setText("1 rok " + daysLeft + " dni");
        } else if (daysleft > 730) {
             Integer daysLeft = daysleft - 2 * 365;
             holder.viewTextDaysLeft.setText("2 lata " + daysLeft + " dni");
         } else if (daysleft > 730 && daysleft<1095) {
             Integer daysLeft = daysleft - 3 * 365;
             holder.viewTextDaysLeft.setText("3 lata " + daysLeft + " dni");
         }

    }

    @NonNull
    @Override
    public ReceiptHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.receipt_item,
                parent, false);
        return new ReceiptHolder(v);
    }

    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    class ReceiptHolder extends RecyclerView.ViewHolder  {
        TextView textViewProduct;
        TextView textViewDate;
        TextView textViewYears;
        TextView viewTextDaysLeft;

        public ReceiptHolder(View itemView) {
            super(itemView);
            textViewProduct = itemView.findViewById(R.id.textView5);
            textViewDate = itemView.findViewById(R.id.textView7);
            textViewYears = itemView.findViewById(R.id.textView6);
            viewTextDaysLeft = itemView.findViewById(R.id.textView8);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position),position);
                    }

                }
            });

        }


    }


    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public int daysBetween(Date d1, Date d2){
        return (int)( (d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
    }


}

