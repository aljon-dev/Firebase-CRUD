package com.example.noteapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Firebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(MainActivity.this);
        FirebaseDatabase database = FirebaseDatabase.getInstance();




        FloatingActionButton  add = findViewById(R.id.add);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View view1 = LayoutInflater.from(MainActivity.this).inflate(R.layout.add_note_dialog,null);


                TextInputLayout titleLayout, contentLayout;

                titleLayout = view1.findViewById(R.id.titlelayout);
                contentLayout = view1.findViewById(R.id.contentLayout);

                TextInputEditText titles, content;

                titles = view1.findViewById(R.id.titleText);
                content = view1.findViewById(R.id.ContentText);

                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("Add") .setView(view1) .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(Objects.requireNonNull(titles.getText().toString().isEmpty())){

                                    Toast.makeText(MainActivity.this, "This title field is Require", Toast.LENGTH_SHORT).show();
                                }
                                else if(Objects.requireNonNull(content.getText().toString().isEmpty())){

                                    Toast.makeText(MainActivity.this, "This content field is Require", Toast.LENGTH_SHORT).show();

                                } else {
                                    ProgressDialog Dialog  = new ProgressDialog(MainActivity.this);
                                            Dialog.setMessage("Storing in database..");
                                            Dialog.show();
                                      Note note = new Note();
                                      note.setTitle(titles.getText().toString());
                                      note.setContent(content.getText().toString());

                                      database.getReference().child("notes").push().setValue(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                                          @Override
                                          public void onSuccess(Void unused) {
                                              Dialog.dismiss();
                                              dialogInterface.dismiss();
                                              Toast.makeText(MainActivity.this, "Save Success", Toast.LENGTH_SHORT).show();


                                          }


                                      }).addOnFailureListener(new OnFailureListener() {
                                          @Override
                                          public void onFailure(@NonNull Exception e) {
                                                Dialog.dismiss();
                                              Toast.makeText(MainActivity.this, "There is an error while saving note", Toast.LENGTH_SHORT).show();
                                          }
                                      });



                                }

                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .create();
                alertDialog.show();



            }
        });


        TextView empty = findViewById(R.id.empty);
        RecyclerView recyclerView = findViewById(R.id.Recycler);

        database.getReference().child("notes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Note> arrayList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Note note = dataSnapshot.getValue(Note.class);
                    Objects.requireNonNull(note).setKey(dataSnapshot.getKey());
                    arrayList.add(note);

                }
                if (arrayList.isEmpty()) {
                    empty.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {

                    empty.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);

                }
                NoteAdapter adapter = new NoteAdapter(MainActivity.this, arrayList);
                recyclerView.setAdapter(adapter);

                adapter.setOnitemClickListener(new NoteAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(Note note) {
                        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.add_note_dialog, null);

                        TextInputLayout titleLayout, contentLayout;
                        TextInputEditText title, content;

                        titleLayout = view.findViewById(R.id.titlelayout);
                        contentLayout = view.findViewById(R.id.contentLayout);

                        title = view.findViewById(R.id.titleText);
                        content = view.findViewById(R.id.ContentText);

                        title.setText(note.getTitle());
                        content.setText(note.getContent());

                        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);

                        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Edit")
                                .setView(view)
                                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (Objects.requireNonNull(title.getText()).toString().isEmpty()) {
                                            titleLayout.setError("This Field is Required")
                                            ;
                                        } else if (Objects.requireNonNull(content.getText()).toString().isEmpty()) {

                                            contentLayout.setError("This Field is Required");
                                        } else {
     progressDialog.setMessage("Saving..");
                              progressDialog.show();
                                            Note note1 = new Note();

                                            note1.setTitle(title.getText().toString());
                                            note1.setContent(content.getText().toString());
                                            database.getReference().child("notes").child(note.getKey()).setValue(note1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    progressDialog.show();
                                                    dialogInterface.dismiss();
                                                    Toast.makeText(MainActivity.this, "Save Successfully", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    dialogInterface.dismiss();
                                                    Toast.makeText(MainActivity.this, "There was an error", Toast.LENGTH_SHORT).show();
                                                }


                                            });

                                        }
                                    }
                                }).setNeutralButton("Close", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                }).setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        progressDialog.setTitle("Deleting..");
                                        progressDialog.show();
                                        database.getReference().child("notes").child(note.getKey()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.show();
                                                dialogInterface.dismiss();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                dialogInterface.dismiss();

                                            }
                                        });
                                    }
                                }).create();
                        alertDialog.show();
                    }
                });

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


 //View data







    }
}