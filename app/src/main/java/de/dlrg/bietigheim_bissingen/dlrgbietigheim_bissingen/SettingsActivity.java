package de.dlrg.bietigheim_bissingen.dlrgbietigheim_bissingen;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private CollectionReference nutzer;

    private Boolean first = true;
    private Boolean first2 = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        db = FirebaseFirestore.getInstance();
        nutzer = db.collection("Nutzer");
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        final EditText editText = (EditText) findViewById(R.id.editText);
        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.abzeichen_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        final Spinner spinner2 = (Spinner) findViewById(R.id.spinner2);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.sanitätsausbildung_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);
        final TextView textView = (TextView) findViewById(R.id.rollenValue);
        DocumentReference documentReference = db.collection("Nutzer").document(user.getUid());
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.get("abzeichen") != null) {
                    spinner.setSelection(Integer.parseInt(String.valueOf(documentSnapshot.get("abzeichen"))));
                }
                if(documentSnapshot.get("sanitätsausbildung") != null) {
                    spinner2.setSelection(Integer.parseInt(String.valueOf(documentSnapshot.get("sanitätsausbildung"))));
                }
                if(documentSnapshot.get("name") != null) {
                    editText.setText(String.valueOf(documentSnapshot.get("name")));
                }
                if(documentSnapshot.get("rolle") != null) {
                    textView.setText(getResources().getStringArray(R.array.rolle_array)[Integer.parseInt(String.valueOf(documentSnapshot.get("rolle")))]);
                }
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!first) {
                    Map<String, Object> abzeichenMap = new HashMap<>();
                    abzeichenMap.put("abzeichen", position);
                    db.collection("Nutzer").document(user.getUid())
                            .update(abzeichenMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("SETTINGS", "DocumentSnapshot successfully written!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("SETTINGS", "Error writing document", e);
                                    Toast.makeText(getApplicationContext(), "Fehler beim Aktualisieren des Abzeichens", Toast.LENGTH_LONG).show();
                                }
                            });
                } else {
                    first = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!first2) {
                    Map<String, Object> saniMap = new HashMap<>();
                    saniMap.put("sanitätsausbildung", position);
                    db.collection("Nutzer").document(user.getUid())
                            .update(saniMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("SETTINGS", "DocumentSnapshot successfully written!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("SETTINGS", "Error writing document", e);
                                    Toast.makeText(getApplicationContext(), "Fehler beim Aktualisieren der Sanitätsausbildung", Toast.LENGTH_LONG).show();
                                }
                            });
                } else {
                    first2 = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Map<String, Object> nameMap = new HashMap<>();
                nameMap.put("name", s.toString());
                db.collection("Nutzer").document(user.getUid())
                        .update(nameMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("SETTINGS", "DocumentSnapshot successfully written!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("SETTINGS", "Error writing document", e);
                                Toast.makeText(getApplicationContext(), "Fehler beim Aktualisieren des Abzeichens", Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }
}
