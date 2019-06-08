package de.dlrg.bietigheim_bissingen.dlrgbietigheim_bissingen;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class Wachstunden extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private FirebaseFirestore db;
    private FirebaseUser user;

    private List<DocumentSnapshot> documentSnapshots = new LinkedList<>();
    private List<Termine> list = new LinkedList<>();

    private WachstundenTimePicker timePickerDialogAnfang;
    private WachstundenTimePicker timePickerDialogEnde;
    private WachstundenDatePicker datePickerDialog;
    private Calendar pickedAnfang;
    private Calendar pickedEnde;

    private String TAG = "WachstundenTAG";

    public static Wachstunden wachstunden;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wachstunden);

        wachstunden = this;

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        CollectionReference collectionReference = db.collection("Nutzer").document(user.getUid()).collection("Wachstunden");
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
                calculateTotalDuration(list);
                mAdapter.notifyDataSetChanged();
            }
        });


        Button button = findViewById(R.id.stunden_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);
                datePickerDialog = new WachstundenDatePicker(wachstunden, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        pickedAnfang = Calendar.getInstance();
                        pickedEnde = Calendar.getInstance();
                        pickedAnfang.set(year, month, dayOfMonth);
                        pickedEnde.set(year, month, dayOfMonth);
                        timePickerDialogAnfang.show();
                    }
                }, mYear, mMonth, mDay);
                timePickerDialogAnfang = new WachstundenTimePicker(wachstunden, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        pickedAnfang.set(pickedAnfang.get(Calendar.YEAR), pickedAnfang.get(Calendar.MONTH), pickedAnfang.get(Calendar.DAY_OF_MONTH), hourOfDay, minute, 0);
                        timePickerDialogEnde.show();
                    }
                }, mHour, mMinute, true);
                timePickerDialogEnde = new WachstundenTimePicker(wachstunden, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        pickedEnde.set(pickedEnde.get(Calendar.YEAR), pickedEnde.get(Calendar.MONTH), pickedEnde.get(Calendar.DAY_OF_MONTH), hourOfDay, minute, 0);
                        if(pickedEnde.getTimeInMillis() > pickedAnfang.getTimeInMillis()) {
                            addWachstunde(pickedAnfang, pickedEnde);
                        } else {
                            Toast.makeText(Wachstunden.wachstunden, "Die Endzeit darf nicht vor der Anfangszeit liegen!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, mHour, mMinute, true);
                datePickerDialog.setPermanentTitle("Wachtag");
                timePickerDialogAnfang.setPermanentTitle("Anfangszeit");
                timePickerDialogEnde.setPermanentTitle("Endzeit");
                datePickerDialog.show();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.wachstundenListe);

        mAdapter = new WachstundenAdapter(list);
        recyclerView.setAdapter(mAdapter);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    public void addWachstunde(final Calendar calAnfang, final Calendar calEnde) {
        final Map<String, Object> map = new HashMap<>();
        map.put("anfangsZeit", new Timestamp(calAnfang.getTime()));
        map.put("endZeit", new Timestamp(calEnde.getTime()));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");

        final String s = calAnfang.get(Calendar.DAY_OF_MONTH) + ". " + Termine.Monate[calAnfang.get(Calendar.MONTH)] + " " + simpleDateFormat.format(calAnfang.getTime()) + " - " + simpleDateFormat.format(calEnde.getTime());


        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        db.collection("Nutzer").document(user.getUid()).collection("Wachstunden").document(s).set(map)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully written!");
                                        Termine termine = new Termine(new Timestamp(calAnfang.getTime()), new Timestamp(calEnde.getTime()));
                                        list.add(termine);
                                        Collections.sort(list);
                                        calculateTotalDuration(list);
                                        mAdapter.notifyDataSetChanged();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error writing document", e);
                                        Toast.makeText(getApplicationContext(), "Fehler beim Hinzufügen!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Bist du sicher, dass du eine Wachstunde am " + calAnfang.get(Calendar.DAY_OF_MONTH) + ". " + Termine.Monate[calAnfang.get(Calendar.MONTH)] + " von " + simpleDateFormat.format(calAnfang.getTime()) + " Uhr bis " + simpleDateFormat.format(calEnde.getTime()) + " Uhr hinzufügen möchtest?").setPositiveButton("Ja", dialogClickListener)
                .setNegativeButton("Abbrechen", dialogClickListener).show();
    }

    public void deleteWachstunde(final String t) {
        final Termine termine = getTermine(t);
        if(termine != null) {
            Date date = termine.getAnfang().toDate();
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"));
            cal.setTime(date);
            db.collection("Nutzer").document(user.getUid()).collection("Wachstunden").document(t)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully deleted!");
                            int position = list.indexOf(termine);
                            list.remove(position);
                            calculateTotalDuration(list);
                            mAdapter.notifyItemRemoved(position);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error deleting document", e);
                            Toast.makeText(getApplicationContext(), "Fehler beim Löschen!", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public Termine getTermine(String s) {
        for(Termine termine : list) {
            if(termine.getDatumZeit().equals(s)) {
                return termine;
            }
        }
        return null;
    }

    private long abzeichen = -1;

    public void calculateTotalDuration(List<Termine> list) {
        if(list == null) return;
        Long total = 0L;
        for(Termine termine:list) {
            total += termine.duration;
        }
        TextView val = findViewById(R.id.wachstundenValue);
        final TextView mon = findViewById(R.id.wachstundenMoney);
        final double hours = Math.round(total / 3600.0 * 2.0) / 2.0;

        val.setText(("Insgesamt " + hours + " Stunden"));

        DocumentReference documentReference = db.collection("Nutzer").document(user.getUid());
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                abzeichen = (long) documentSnapshot.get("abzeichen");
                if(abzeichen == 0) {
                    if(hours >= 5 && hours < 30) {
                        mon.setText(("Aktuelle Vergütung: " + hours * 3.5 + "0€"));
                    } else if(hours < 50) {
                        mon.setText(("Aktuelle Vergütung: " + hours * 4.5 + "0€"));
                    } else {
                        mon.setText(("Aktuelle Vergütung: " + hours * 5.5 + "0€"));
                    }
                } else if(abzeichen == 1 || abzeichen == 2) {
                    if(hours >= 5 && hours < 30) {
                        mon.setText(("Aktuelle Vergütung: " + hours * 6.5 + "0€"));
                    } else if(hours < 50) {
                        mon.setText(("Aktuelle Vergütung: " + hours * 7.5 + "0€"));
                    } else {
                        mon.setText(("Aktuelle Vergütung: " + hours * 8 + "0€"));
                    }
                }
            }
        });
    }
}
