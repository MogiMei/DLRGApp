package de.dlrg.bietigheim_bissingen.dlrgbietigheim_bissingen;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.LinkedList;
import java.util.List;

public class Wachplan extends AppCompatActivity {
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

                recyclerView = (RecyclerView) findViewById(R.id.pflichttermineListe);

                mAdapter = new PflichtterminAdapter(list);
                recyclerView.setAdapter(mAdapter);

                layoutManager = new LinearLayoutManager(wachplan);
                recyclerView.setLayoutManager(layoutManager);
            }
        });
    }
}
