package de.dlrg.bietigheim_bissingen.dlrgbietigheim_bissingen;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class FreibadActivity extends AppCompatActivity {
    private final String TAG = "FreibadActivity";
    private FirebaseFirestore db;
    private CollectionReference nutzer;
    private TextView wachgangerValue;
    private CheckBox cb;
    private Boolean cbManual = false;

    private TextView freibadZeit;

    private Handler handler;
    public static FreibadActivity freibadActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_freibad);

        freibadActivity = this;

        db = FirebaseFirestore.getInstance();
        nutzer = db.collection("Nutzer");
        wachgangerValue = findViewById(R.id.wachgangerValue);
        freibadZeit = findViewById(R.id.freibadZeit);

        ImageButton ib = (ImageButton) findViewById(R.id.backButton);
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainMenu();
            }
        });

        ImageButton wachplan = (ImageButton) findViewById(R.id.wachdienst);
        wachplan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(freibadActivity, Wachplan.class);
                startActivity(intent);
            }
        });

        ImageButton wachstunde = (ImageButton) findViewById(R.id.wachstundenButton);
        wachstunde.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(freibadActivity, Wachstunden.class);
                startActivity(intent);
            }
        });

        ImageButton nachfordernButton = findViewById(R.id.nachfordernButton);
        nachfordernButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wachgangerAnfordern();
            }
        });
        cb = (CheckBox) findViewById(R.id.checkBoxFreibad);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!cbManual) {
                    freibadZeit.setText((DateUtils.getRelativeTimeSpanString(Timestamp.now().getSeconds() * 1000)));
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    Map<String, Object> tokenMap = new HashMap<>();
                    if (cb.isChecked()) {
                        tokenMap.put("freibad", true);
                        tokenMap.put("freibadInZeit", Timestamp.now());
                        nutzer.document(user.getUid())
                                .update(tokenMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "Freibad rein: positive");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "Freibad rein: negative");
                                    }
                                });
                    } else {
                        tokenMap.put("freibad", false);
                        tokenMap.put("freibadOutZeit", Timestamp.now());
                        nutzer.document(user.getUid())
                                .update(tokenMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "Freibad raus: positive");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "Freibad raus: negative");
                                    }
                                });
                    }
                }
                cbManual = false;
                updateData(false);
            }
        });

        setImageButtonEnabled(this, false, (ImageButton) findViewById(R.id.putzplan), R.drawable.ic_cleaning);

        handler = new Handler();
        handler.post(runnableCode);

        updateData(true);
    }

    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            updateData(true);
            Log.d(TAG, "UpdatedData");
            handler.postDelayed(runnableCode, 60000);
        }
    };

    public void updateData(Boolean first) {
        DocumentReference docRef = db.collection("Freibad").document("main");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        updateValues(document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        Query wachgangerImFreibad = nutzer.whereEqualTo("freibad", true);
        wachgangerImFreibad.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                wachgangerValue = (TextView) findViewById(R.id.wachgangerValue);
                wachgangerValue.setText(String.valueOf(queryDocumentSnapshots.size()));
            }
        });
        if(first) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                nutzer.document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Boolean x = Boolean.parseBoolean(String.valueOf(documentSnapshot.get("freibad")));

                        if (x) {
                            cbManual = true;
                            cb.setChecked(true);
                            cbManual = false;
                            Timestamp time = (Timestamp) documentSnapshot.get("freibadInZeit");
                            if (time != null) {
                                freibadZeit.setText((DateUtils.getRelativeTimeSpanString(time.getSeconds() * 1000)));
                            }
                        } else {
                            Timestamp time = (Timestamp) documentSnapshot.get("freibadOutZeit");
                            if(time != null) {
                                freibadZeit.setText((DateUtils.getRelativeTimeSpanString(time.getSeconds() * 1000)));
                            } else {
                                freibadZeit.setText("Noch nicht im Freibad gewesen!");
                            }
                        }
                    }
                });
            }
        }
    }

    public void updateValues(Map<String, Object> data) {
        VisitorIndicator v = findViewById(R.id.visitorIndicator);
        v.setPercentValue(Integer.valueOf(String.valueOf(data.get("Besucheranteil"))));
        TextView besucherZeit = findViewById(R.id.besucherZeit);
        Timestamp time = (Timestamp) data.get("Besucherzeit");
        besucherZeit.setText((DateUtils.getRelativeTimeSpanString(time.getSeconds() * 1000, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS, DateUtils.FORMAT_NUMERIC_DATE) + " von " + data.get("Besuchermelder")));
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler = new Handler();
        handler.post(runnableCode);
    }

    public void openMainMenu() {
        handler.removeCallbacksAndMessages(null);
        finish();
    }

    public void besucherUpdateDialog(final float x, final float y) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        besucherUpdaten(x, y);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Bist du sicher, dass du den Besucheranteil updaten möchtest?").setPositiveButton("Ja", dialogClickListener)
                .setNegativeButton("Abbrechen", dialogClickListener).show();

    }

    public void besucherUpdaten(float x, float y) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        VisitorIndicator visitorIndicator = findViewById(R.id.visitorIndicator);
        Map<String, Object> update = new HashMap<>();

        update.put("Besucherzeit", Timestamp.now());
        update.put("Besucheranteil", Math.round(x / visitorIndicator.getViewWidth() * 100));
        update.put("Besuchermelder", user.getDisplayName());
        db.collection("Freibad").document("main").update(update).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("LOGINACTIVITY", "Besucher geupdatet!");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("LOGINACTIVITY", "Error writing visitor", e);
                    }
                });
        visitorIndicator.setPercentValue(Math.round(x / visitorIndicator.getViewWidth() * 100));

        updateData(false);
    }

    public void wachgangerAnfordern() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        sendRequest();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Bist du sicher, dass du alle Wachgänger alamieren möchtest?").setPositiveButton("Ja", dialogClickListener)
                .setNegativeButton("Abbrechen", dialogClickListener).show();

    }

    public void sendRequest() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://us-central1-dlrgbibi.cloudfunctions.net/leuteAnfordern";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.networkResponse.allHeaders.toString());
            }
        });
        queue.add(stringRequest);
    }

    public static void setImageButtonEnabled(Context ctxt, boolean enabled, ImageButton item, int iconResId) {
        item.setEnabled(enabled);
        Drawable originalIcon = ctxt.getResources().getDrawable(iconResId);
        Drawable icon = enabled ? originalIcon : convertDrawableToGrayScale(originalIcon);
        item.setImageDrawable(icon);
    }

    public static Drawable convertDrawableToGrayScale(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        Drawable res = drawable.mutate();
        res.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        return res;
    }
}
