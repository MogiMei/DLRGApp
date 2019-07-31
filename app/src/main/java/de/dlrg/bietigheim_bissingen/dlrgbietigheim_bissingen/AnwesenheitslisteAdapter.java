package de.dlrg.bietigheim_bissingen.dlrgbietigheim_bissingen;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AnwesenheitslisteAdapter extends RecyclerView.Adapter<AnwesenheitslisteAdapter.AnwesenheitslisteHolder> {
    private List<Person> mDataset;

    private String TAG = "WachgangerAdapterTAG";

    public static class AnwesenheitslisteHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public TextView textView2;
        public TextView textView3;
        public LinearLayout sub_item;

        public AnwesenheitslisteHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.wachganger_text);
            textView2 = itemView.findViewById(R.id.abzeichenText);
            textView3 = itemView.findViewById(R.id.sanitätsText);
            sub_item = itemView.findViewById(R.id.sub_item);
        }
    }

    public AnwesenheitslisteAdapter(List<Person> myDataset) {
        mDataset = myDataset;
    }

    @NonNull
    @Override
    public AnwesenheitslisteAdapter.AnwesenheitslisteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_wachganger, parent, false);

        AnwesenheitslisteHolder vh = new AnwesenheitslisteHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final AnwesenheitslisteAdapter.AnwesenheitslisteHolder holder, int position) {
        Person person = mDataset.get(position);

        holder.textView.setText(person.name);
        holder.textView2.setText(Anwesenheitsliste.anwesenheitsliste.getResources().getStringArray(R.array.abzeichen_array)[person.abzeichen]);
        holder.textView3.setText(Anwesenheitsliste.anwesenheitsliste.getResources().getStringArray(R.array.sanitätsausbildung_array)[person.sanitätsausbildung]);

        holder.sub_item.setVisibility(View.GONE);

        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.sub_item.setVisibility((holder.sub_item.getVisibility() == View.GONE ? View.VISIBLE : View.GONE));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
