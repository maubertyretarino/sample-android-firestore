package com.example.sample;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class Dashboard extends AppCompatActivity {

    FirebaseFirestore firestore;

    EditText etFirst, etLast, etDesc, etSearch;
    Button btnCreate, btnRead, btnSearch, btnUpdate, btnDelete;
    TextView tvUsers;

    String currentDocID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        FirebaseApp.initializeApp(this);

        firestore = FirebaseFirestore.getInstance();

        Map<String, Object> user = new HashMap<>();
        user.put("firstName", "testFirstname");
        user.put("lastName", "testLastname");
        user.put("description", "testDescription");

        firestore.collection("users")
                .add(user)
                .addOnSuccessListener(documentReference ->
                        Toast.makeText(getApplicationContext(), "Registration OK", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(getApplicationContext(), "Registration Failed", Toast.LENGTH_SHORT).show()
                );


        etFirst = findViewById(R.id.etFirst);
        etLast = findViewById(R.id.etLast);
        etDesc = findViewById(R.id.etDesc);
        etSearch = findViewById(R.id.etSearch);

        btnCreate = findViewById(R.id.btnCreate);
        btnSearch = findViewById(R.id.btnSearch);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);

        tvUsers = findViewById(R.id.tvUsers);

        // -------------------------------------------------
        // CREATE
        // -------------------------------------------------
        btnCreate.setOnClickListener(v -> {
            Map<String, Object> data = new HashMap<>();
            data.put("firstName", etFirst.getText().toString());
            data.put("lastName", etLast.getText().toString());
            data.put("description", etDesc.getText().toString());

            firestore.collection("users")
                    .add(data)
                    .addOnSuccessListener(doc ->
                            Toast.makeText(this, "User Created! ID: " + doc.getId(), Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Create Failed", Toast.LENGTH_SHORT).show());
        });

        // -------------------------------------------------
        // SEARCH USER BY DOC ID
        // -------------------------------------------------
        btnSearch.setOnClickListener(v -> {
            String searchID = etSearch.getText().toString().trim();

            if (searchID.isEmpty()) {
                Toast.makeText(this, "Enter a Document ID!", Toast.LENGTH_SHORT).show();
                return;
            }

            firestore.collection("users").document(searchID)
                    .get()
                    .addOnSuccessListener(doc -> {
                        if (doc.exists()) {
                            currentDocID = searchID;

                            etFirst.setText(doc.getString("firstName"));
                            etLast.setText(doc.getString("lastName"));
                            etDesc.setText(doc.getString("description"));

                            Toast.makeText(this, "User Found!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "No user found", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Search Failed", Toast.LENGTH_SHORT).show());
        });

        // -------------------------------------------------
        // UPDATE USER --- does not function
        // -------------------------------------------------
        btnUpdate.setOnClickListener(v -> {
            if (currentDocID.isEmpty()) {
                Toast.makeText(this, "Search a user first!", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> updateData = new HashMap<>();
            updateData.put("firstName", etFirst.getText().toString());
            updateData.put("lastName", etLast.getText().toString());
            updateData.put("description", etDesc.getText().toString());

            firestore.collection("users").document(currentDocID)
                    .update(updateData)
                    .addOnSuccessListener(aVoid ->
                            Toast.makeText(this, "Updated!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Update Failed", Toast.LENGTH_SHORT).show());
        });

        // -------------------------------------------------
        // DELETE USER
        // -------------------------------------------------
        btnDelete.setOnClickListener(v -> {
            if (currentDocID.isEmpty()) {
                Toast.makeText(this, "Search a user first!", Toast.LENGTH_SHORT).show();
                return;
            }

            firestore.collection("users").document(currentDocID)
                    .delete()
                    .addOnSuccessListener(aVoid ->
                            Toast.makeText(this, "Deleted!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Delete Failed", Toast.LENGTH_SHORT).show());
        });

    }
}
