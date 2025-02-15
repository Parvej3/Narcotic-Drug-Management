package com.project.drugmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.project.drugmanagement.Models.ReadWriteDoctorDetails;
import com.project.drugmanagement.Models.ReadWriteManufacturerDetails;
import com.project.drugmanagement.Models.ReadWriteUserDetails;
import com.project.drugmanagement.databinding.ActivityAddUserDetailsBinding;

public class AddUserDetails extends AppCompatActivity {

    String selectedRole;
    private final String TAG = "AddUserDetails";
    ActivityAddUserDetailsBinding userDetailsBinding;
    FirebaseAuth authProfile;
    FirebaseUser firebaseUser;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userDetailsBinding = ActivityAddUserDetailsBinding.inflate(getLayoutInflater());
        setContentView(userDetailsBinding.getRoot());

        userDetailsBinding.editTextQualification.setEnabled(false);
        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        selectedRole = firebaseUser.getDisplayName();

        //set user role to textview
        userDetailsBinding.textViewRole.setText(selectedRole);
        if (selectedRole.equals("Doctor")) {
            userDetailsBinding.editTextQualification.setEnabled(true);
            userDetailsBinding.editTextDlno1.setEnabled(false);
        }


        userDetailsBinding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textName = userDetailsBinding.editTextName.getText().toString();
                String textDlno = userDetailsBinding.editTextDlno1.getText().toString();
                String textAddress = userDetailsBinding.editTextAddress.getText().toString();
                String textContact = userDetailsBinding.editTextContact.getText().toString();
                String textQualification = userDetailsBinding.editTextQualification.getText().toString();

                if (TextUtils.isEmpty(textName)) {
                    Toast.makeText(AddUserDetails.this, "Please enter your name !", Toast.LENGTH_SHORT).show();
                    userDetailsBinding.editTextName.setError("Name is required");
                    userDetailsBinding.editTextName.requestFocus();
                } else if (TextUtils.isEmpty(textAddress)) {
                    Toast.makeText(AddUserDetails.this, "Please enter city/villege name !", Toast.LENGTH_SHORT).show();
                    userDetailsBinding.editTextAddress.setError("City/Villege name is required");
                    userDetailsBinding.editTextAddress.requestFocus();
                } else if (TextUtils.isEmpty(textContact)) {
                    Toast.makeText(AddUserDetails.this, "Please enter contact no. !", Toast.LENGTH_SHORT).show();
                    userDetailsBinding.editTextContact.setError("Contact no. is required");
                    userDetailsBinding.editTextContact.requestFocus();
                } else if (selectedRole.equals("Doctor")) {
                    if (TextUtils.isEmpty(textQualification)) {
                        Toast.makeText(AddUserDetails.this, "Please enter Qualification details. !", Toast.LENGTH_SHORT).show();
                        userDetailsBinding.editTextQualification.setError("Qualification is required");
                        userDetailsBinding.editTextQualification.requestFocus();
                    }
                    addUserInfo(textName, textDlno, textQualification, textAddress, textContact);
                    userDetailsBinding.progressBar.setVisibility(View.VISIBLE);
                } else if (TextUtils.isEmpty(textDlno)) {
                    Toast.makeText(AddUserDetails.this, "Please enter Dl/Registration no. !", Toast.LENGTH_SHORT).show();
                    userDetailsBinding.editTextDlno1.setError("Dl/registration no. is required");
                    userDetailsBinding.editTextDlno1.requestFocus();
                } else {
                    addUserInfo(textName, textDlno, textQualification, textAddress, textContact);
                    userDetailsBinding.progressBar.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        CollectionReference collectionReference = db.collection(selectedRole);
        collectionReference
                .whereEqualTo("email", firebaseUser.getEmail())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            if (firebaseUser.getDisplayName().equals("Doctor")) {
                                ReadWriteDoctorDetails readDoctorDetails = documentSnapshot.toObject(ReadWriteDoctorDetails.class);
                                userDetailsBinding.editTextName.setText(readDoctorDetails.getName());
                                userDetailsBinding.editTextContact.setText(readDoctorDetails.getContact());
                                userDetailsBinding.editTextAddress.setText(readDoctorDetails.getAddress());
                                userDetailsBinding.editTextQualification.setText(readDoctorDetails.getQualification());
                            } else if (firebaseUser.getDisplayName().equals("Manufacturer")) {
                                ReadWriteManufacturerDetails readManufacturerDetails = documentSnapshot.toObject(ReadWriteManufacturerDetails.class);
                                userDetailsBinding.editTextName.setText(readManufacturerDetails.getName());
                                userDetailsBinding.editTextContact.setText(readManufacturerDetails.getContact());
                                userDetailsBinding.editTextAddress.setText(readManufacturerDetails.getAddress());
                                userDetailsBinding.editTextDlno1.setText(readManufacturerDetails.getDlno());
                            } else {
                                ReadWriteUserDetails readUserDetails = documentSnapshot.toObject(ReadWriteUserDetails.class);
                                userDetailsBinding.editTextName.setText(readUserDetails.getName());
                                userDetailsBinding.editTextContact.setText(readUserDetails.getContact());
                                userDetailsBinding.editTextAddress.setText(readUserDetails.getAddress());
                                userDetailsBinding.editTextDlno1.setText(readUserDetails.getDlno());
                            }
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.toString());
                        Toast.makeText(AddUserDetails.this, "User not registered , Please register first..", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addUserInfo(String textName, String textDlno, String textQualification, String textAddress, String textContact) {

        if (selectedRole.equals("Doctor")) {
            ReadWriteDoctorDetails writeDoctorDetails = new ReadWriteDoctorDetails(firebaseUser.getEmail(), textName, textContact, textAddress, textQualification);
            db.collection(selectedRole).document(textName).set(writeDoctorDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(AddUserDetails.this, "Doctor data saved successfully..", Toast.LENGTH_SHORT).show();
                    userDetailsBinding.progressBar.setVisibility(View.GONE);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddUserDetails.this, "Error..", Toast.LENGTH_SHORT).show();
                    userDetailsBinding.progressBar.setVisibility(View.GONE);
                    Log.d(TAG, e.toString());
                }
            });
            ;
        } else if (selectedRole.equals("Manufacturer")) {

            CollectionReference checkRefrence = db.collection(selectedRole);
            checkRefrence.document(textName).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        Toast.makeText(AddUserDetails.this, "User already registered", Toast.LENGTH_SHORT).show();
                        userDetailsBinding.progressBar.setVisibility(View.GONE);
                        return;
                    } else {
                        ReadWriteManufacturerDetails writeManufacturerDetailsDetails = new ReadWriteManufacturerDetails(firebaseUser.getEmail(), textName, textContact, textAddress, textDlno);
                        db.collection(selectedRole).document(textName).set(writeManufacturerDetailsDetails, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(AddUserDetails.this, "Manufacturer data saved successfully..", Toast.LENGTH_SHORT).show();
                                userDetailsBinding.progressBar.setVisibility(View.GONE);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddUserDetails.this, "Error..", Toast.LENGTH_SHORT).show();
                                userDetailsBinding.progressBar.setVisibility(View.GONE);
                                Log.d(TAG, e.toString());
                            }
                        });
                        ;
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddUserDetails.this, "Something went wrong ..try again", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, e.toString());
                }
            });


        } else if (selectedRole.equals("Wholesaler")) {

            CollectionReference checkRefrence = db.collection(selectedRole);
            checkRefrence.document(textName).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        Toast.makeText(AddUserDetails.this, "User already registered", Toast.LENGTH_SHORT).show();
                        userDetailsBinding.progressBar.setVisibility(View.GONE);
                        return;
                    } else {
                        ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(firebaseUser.getEmail(), textName, textDlno, textAddress, textContact);
                        db.collection(selectedRole).document(textName).set(writeUserDetails, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(AddUserDetails.this, " data saved successfully..", Toast.LENGTH_SHORT).show();
                                userDetailsBinding.progressBar.setVisibility(View.GONE);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddUserDetails.this, "Error..", Toast.LENGTH_SHORT).show();
                                userDetailsBinding.progressBar.setVisibility(View.GONE);
                                Log.d(TAG, e.toString());
                            }
                        });
                        ;
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddUserDetails.this, "Something went wrong ..try again", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, e.toString());
                }
            });


        } else {
            CollectionReference checkDepoReference = db.collection(selectedRole);
            checkDepoReference.document(textName).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        Toast.makeText(AddUserDetails.this, "User already registered", Toast.LENGTH_SHORT).show();
                        userDetailsBinding.progressBar.setVisibility(View.GONE);
                        return;
                    } else {
                        ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(firebaseUser.getEmail(), textName, textDlno, textAddress, textContact);
                        db.collection(selectedRole).document(textName).set(writeUserDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(AddUserDetails.this, "User data saved successfully..", Toast.LENGTH_SHORT).show();
                                userDetailsBinding.progressBar.setVisibility(View.GONE);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddUserDetails.this, "Error..", Toast.LENGTH_SHORT).show();
                                userDetailsBinding.progressBar.setVisibility(View.GONE);
                                Log.d(TAG, e.toString());
                            }
                        });
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddUserDetails.this, "Something went wrong ..try again", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, e.toString());
                }
            });
        }

    }

}