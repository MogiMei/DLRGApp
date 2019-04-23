package de.dlrg.bietigheim_bissingen.dlrgbietigheim_bissingen;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class Wachplan extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private FirebaseFirestore db;
    private FirebaseUser user;

    private List<DocumentSnapshot> documentSnapshots = new LinkedList<>();
    private List<Termine> list = new LinkedList<>();

    private String TAG = "WachplanTAG";

    public static Wachplan wachplan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wachplan);

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        wachplan = this;

        final Calendar newCalendar = Calendar.getInstance();

        final DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, final int year, final int monthOfYear, final int dayOfMonth) {
                final Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                Map<String, Object> map = new HashMap<>();
                                final Date anfang = new Date(year-1900, monthOfYear, dayOfMonth, 9, 45);
                                final Date ende = new Date(year-1900, monthOfYear, dayOfMonth, 18, 0);
                                Log.d(TAG, anfang.toString());
                                Log.d(TAG, ende.toString());
                                map.put("anfangsZeit", new Timestamp(anfang));
                                map.put("endZeit", new Timestamp(ende));

                                String s = newDate.get(Calendar.DAY_OF_MONTH) + ". " + Termine.Monate[newDate.get(Calendar.MONTH)];

                                db.collection("Nutzer").document(user.getUid()).collection("Pflichttermine").document(s).set(map)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "DocumentSnapshot successfully written!");
                                                Termine termine = new Termine(anfang, ende);
                                                list.add(termine);
                                                Collections.sort(list);
                                                mAdapter.notifyDataSetChanged();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error writing document", e);
                                            }
                                        });
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(Wachplan.wachplan);
                builder.setMessage("Bist du sicher, dass du einen Pflichttermin am " + dayOfMonth + "." + monthOfYear + "." + year + " hinzufügen möchtest?").setPositiveButton("Ja", dialogClickListener)
                        .setNegativeButton("Abbrechen", dialogClickListener).show();

            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        Button button = findViewById(R.id.termin_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        CollectionReference collectionReference = db.collection("Nutzer").document(user.getUid()).collection("Pflichttermine");
        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                documentSnapshots = queryDocumentSnapshots.getDocuments();

                for(DocumentSnapshot documentSnapshot:documentSnapshots) {
                    Timestamp anfang = (Timestamp) documentSnapshot.get("anfangsZeit");
                    Timestamp ende = (Timestamp) documentSnapshot.get("endZeit");
                    list.add(new Termine(anfang, ende));
                }
                Collections.sort(list);
                mAdapter.notifyDataSetChanged();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.pflichttermineListe);

        mAdapter = new PflichtterminAdapter(list);
        recyclerView.setAdapter(mAdapter);

        layoutManager = new LinearLayoutManager(wachplan);
        recyclerView.setLayoutManager(layoutManager);
    }

    public void deletePflichttermin(final String t) {
        final Termine termine = getTermine(t);
        if(termine != null) {
            Date date = termine.getAnfang().toDate();
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"));
            cal.setTime(date);
            String s = cal.get(Calendar.DAY_OF_MONTH) + ". " + Termine.Monate[cal.get(Calendar.MONTH)];
            db.collection("Nutzer").document(user.getUid()).collection("Pflichttermine").document(s)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully deleted!");
                            int position = list.indexOf(termine);
                            list.remove(position);
                            mAdapter.notifyItemRemoved(position);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error deleting document", e);
                        }
                    });
        }
    }

    public Termine getTermine(String s) {
        for(Termine termine : list) {
            if(termine.getDatum().equals(s)) {
                return termine;
            }
        }
        return null;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

    }
}
