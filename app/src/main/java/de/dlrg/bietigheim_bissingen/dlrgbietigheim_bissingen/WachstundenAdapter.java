package de.dlrg.bietigheim_bissingen.dlrgbietigheim_bissingen;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class WachstundenAdapter extends RecyclerView.Adapter<WachstundenAdapter.WachstundenHolder> {
    private List<Termine> mDataset;

    private String TAG = "WachstundenAdapterTAG";

    public static class WachstundenHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public TextView textView2;
        public ImageButton imageButton;
        public WachstundenHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.wachstunden_text);
            textView2 = itemView.findViewById(R.id.stunden_text);
            imageButton = itemView.findViewById(R.id.delete_button);
        }
    }

    public WachstundenAdapter(List<Termine> myDataset) {
        mDataset = myDataset;
    }

    @NonNull
    @Override
    public WachstundenAdapter.WachstundenHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_wachstunde, parent, false);

        WachstundenHolder vh = new WachstundenHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull WachstundenAdapter.WachstundenHolder holder, int position) {
        final Termine termine = mDataset.get(position);

        TextView textView = holder.textView;
        TextView textView2 = holder.textView2;
        ImageButton imageButton = holder.imageButton;

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                Wachstunden.wachstunden.deleteWachstunde(termine.getDatumZeit());
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(Wachstunden.wachstunden);
                builder.setMessage("Bist du sicher, dass du diese Wachstunde löschen möchtest?").setPositiveButton("Ja", dialogClickListener)
                        .setNegativeButton("Abbrechen", dialogClickListener).show();

            }
        });
        Date anfang = termine.getAnfang().toDate();
        Date ende = termine.getEnde().toDate();
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        cal.setTime(anfang);
        String s = cal.get(Calendar.DAY_OF_MONTH) + ". " + Termine.Monate[cal.get(Calendar.MONTH)] + "\n" + simpleDateFormat.format(anfang) + " Uhr bis " + simpleDateFormat.format(ende) + " Uhr";
        textView.setText(s);
        s = String.valueOf((Math.round((termine.getEnde().getSeconds() - termine.getAnfang().getSeconds()) / 3600.0 * 2.0) / 2.0)) + " Stunden";
        textView2.setText(s);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
