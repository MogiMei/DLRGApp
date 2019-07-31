package de.dlrg.bietigheim_bissingen.dlrgbietigheim_bissingen;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Anwesenheitsliste extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private List<DocumentSnapshot> documentSnapshots = new LinkedList<>();
    private List<Person> list = new LinkedList<>();

    private FirebaseFirestore db;
    private FirebaseUser user;

    public static Anwesenheitsliste anwesenheitsliste;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anwesenheitsliste);

        anwesenheitsliste = this;

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        CollectionReference collectionReference = db.collection("Nutzer");
        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                documentSnapshots = queryDocumentSnapshots.getDocuments();

                for(DocumentSnapshot documentSnapshot:documentSnapshots) {
                    if(Boolean.parseBoolean(String.valueOf(documentSnapshot.get("freibad")))) {
                        Person person = new Person(String.valueOf(documentSnapshot.get("name")), Integer.parseInt(String.valueOf(documentSnapshot.get("abzeichen"))), Integer.parseInt(String.valueOf((documentSnapshot.get("sanitätsausbildung")))), Integer.parseInt(String.valueOf(documentSnapshot.get("rolle"))));
                        list.add(person);
                    }
                }
                Collections.sort(list);
                TextView textView = findViewById(R.id.wachgängerValue);
                textView.setText(("Insgesamt " + list.size() + " Wachgänger"));
                mAdapter.notifyDataSetChanged();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.wachgangerListe);

        mAdapter = new AnwesenheitslisteAdapter(list);
        recyclerView.setAdapter(mAdapter);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        SwipeController swipeController = new SwipeController();

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeController);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
}
