package de.dlrg.bietigheim_bissingen.dlrgbietigheim_bissingen;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class PflichtterminAdapter extends RecyclerView.Adapter<PflichtterminAdapter.PflichtterminHolder> {
    private List<Termine> mDataset;

    public static class PflichtterminHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ImageButton imageButton;
        public PflichtterminHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.termin_text);
            imageButton = itemView.findViewById(R.id.delete_button);
        }
    }

    public PflichtterminAdapter(List<Termine> myDataset) {
        mDataset = myDataset;
    }

    @NonNull
    @Override
    public PflichtterminAdapter.PflichtterminHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pflichttermin, parent, false);

        PflichtterminHolder vh = new PflichtterminHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull PflichtterminAdapter.PflichtterminHolder holder, int position) {
        Termine termine = mDataset.get(position);

        TextView textView = holder.textView;
        ImageButton imageButton = holder.imageButton;

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        Date date = termine.getAnfang().toDate();
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"));
        cal.setTime(date);
        String s = cal.get(Calendar.DAY_OF_MONTH) + ". " + Termine.Monate[cal.get(Calendar.MONTH)] + " von 09:45 Uhr bis 18:00 Uhr";
        textView.setText(s);
     }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
