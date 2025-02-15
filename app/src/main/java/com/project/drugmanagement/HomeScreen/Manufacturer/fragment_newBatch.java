package com.project.drugmanagement.HomeScreen.Manufacturer;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.project.drugmanagement.Models.ReadWriteManufacturerDetails;
import com.project.drugmanagement.Models.ReadWriteMonthArray;
import com.project.drugmanagement.Models.ReadWriteProductionDetails;
import com.project.drugmanagement.R;
import com.project.drugmanagement.databinding.FragmentNewBatchBinding;

import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_newBatch#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_newBatch extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String SELECTED_ROLE = "Manufacturer";
    private static final String TAG = "Fragment_newBatch";
    private static final String PRODUCTION = "Production";

    private FragmentNewBatchBinding newBatchBinding;
    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragment_newBatch() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_newBatch.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_newBatch newInstance(String param1, String param2) {
        fragment_newBatch fragment = new fragment_newBatch();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_new_batch, container, false);
        newBatchBinding = FragmentNewBatchBinding.inflate(inflater, container, false);
        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        return newBatchBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //method load company specific products into dropdownList
        loadProductDetails();
        //method loads currentDate into mfg Date
        loadMfgDate();


        newBatchBinding.btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = newBatchBinding.autoCompleteTextViewProduct.getText().toString();
                String batchNo = newBatchBinding.editTextBatch.getText().toString();
                String mfgDate = newBatchBinding.editTextMfgyear.getText().toString();
                String expiryDate = newBatchBinding.editTextExpDate.getText().toString();
                String quantity = newBatchBinding.editTextQuantity.getText().toString();
                String pack = newBatchBinding.editTextPack.getText().toString();

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(getActivity(), "Please select product ", Toast.LENGTH_SHORT).show();
                    newBatchBinding.autoCompleteTextViewProduct.setError("Required");
                } else if (TextUtils.isEmpty(batchNo)) {
                    Toast.makeText(getActivity(), "Please enter batch no. ", Toast.LENGTH_SHORT).show();
                    newBatchBinding.autoCompleteTextViewProduct.setError("Batch no. Required");
                } else if (TextUtils.isEmpty(mfgDate)) {
                    Toast.makeText(getActivity(), "Please enter mfg date. ", Toast.LENGTH_SHORT).show();
                    newBatchBinding.autoCompleteTextViewProduct.setError("Batch no. Required");
                } else if (TextUtils.isEmpty(expiryDate)) {
                    Toast.makeText(getActivity(), "Please enter expiry date . ", Toast.LENGTH_SHORT).show();
                    newBatchBinding.editTextExpDate.setError("expiry date. Required");
                } else if (TextUtils.isEmpty(quantity)) {
                    Toast.makeText(getActivity(), "Please enter no. of quantity. ", Toast.LENGTH_SHORT).show();
                    newBatchBinding.editTextQuantity.setError("quantity Required");
                } else if (TextUtils.isEmpty(pack)) {
                    Toast.makeText(getActivity(), "Please enter packing details. ", Toast.LENGTH_SHORT).show();
                    newBatchBinding.editTextQuantity.setError("packing details Required");
                } else {
                    //creates production collection in firestore
                    createProduction(name, batchNo, mfgDate, expiryDate, quantity, pack);
                    newBatchBinding.progressBar.setVisibility(View.VISIBLE);
                }

            }
        });


    }


    //creates production collection in firestore
    private void createProduction(String name, String batchNo, String mfgDate, String expiryDate, String quantity, String pack) {
        final String[] companyName = new String[1];
        //first get company name from manufacturer collection
        CollectionReference collectionReference = db.collection(SELECTED_ROLE);
        collectionReference
                .whereEqualTo("email", firebaseUser.getEmail())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            companyName[0] = documentSnapshot.getId();

                            int total = Integer.parseInt(quantity) * Integer.parseInt(pack);
                            ReadWriteProductionDetails writeProductionDetails = new ReadWriteProductionDetails(batchNo, name, mfgDate, expiryDate, quantity, pack, total);

                            CollectionReference productionCollection = db.collection(PRODUCTION);

                            // create an array in production->manufacturer_name document to get all subcollection _names
                            productionCollection.document(companyName[0]).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    //first check array is created or not if created then update fields
                                        if(documentSnapshot.contains("months")) {

                                            productionCollection.document(companyName[0]).update("months", FieldValue.arrayUnion(mfgDate)).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d(TAG, e.toString());
                                                    newBatchBinding.progressBar.setVisibility(View.GONE);
                                                }
                                            });
                                        } else {
                                            // else create months array and assign new value
                                            productionCollection.document(companyName[0]).set(new ReadWriteMonthArray(), SetOptions.merge());
                                            productionCollection.document(companyName[0]).update("months", FieldValue.arrayUnion(mfgDate)).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d(TAG, e.toString());
                                                    newBatchBinding.progressBar.setVisibility(View.GONE);
                                                }
                                            });
                                        }
                                }
                            });

                            //add all production details in collection
                            productionCollection.document(companyName[0]).collection(mfgDate).document(batchNo).set(writeProductionDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(getActivity(), "New product details are added successfully", Toast.LENGTH_SHORT).show();
                                    newBatchBinding.progressBar.setVisibility(View.GONE);

                                    //reset all fields
                                    newBatchBinding.editTextBatch.setText("");
                                    newBatchBinding.editTextPack.setText("");
                                    newBatchBinding.editTextQuantity.setText("");
                                    newBatchBinding.editTextMfgyear.setText("");
                                    newBatchBinding.editTextExpDate.setText("");

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), "Something went wrong ", Toast.LENGTH_LONG);
                                    Log.d(TAG, e.toString());
                                    newBatchBinding.progressBar.setVisibility(View.GONE);
                                }
                            });


                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.toString());
                        newBatchBinding.progressBar.setVisibility(View.GONE);
                    }
                });
    }

    //method loads currentDate into mfg Date
    private void loadMfgDate() {
        Calendar calendar = Calendar.getInstance();
        String[] monthName = {"jan", "feb", "mar", "apr", "may", "jun", "jul",
                "aug", "sep", "oct", "nov", "dec"};
        String month = monthName[calendar.get(Calendar.MONTH)];
        int year = calendar.get(Calendar.YEAR);
        newBatchBinding.editTextMfgyear.setText(month + "" + year);

    }

    //method load company specific products into dropdownList
    private void loadProductDetails() {
        CollectionReference collectionReference = db.collection(SELECTED_ROLE);
        collectionReference
                .whereEqualTo("email", firebaseUser.getEmail())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                                ReadWriteManufacturerDetails readManufacturerDetails = documentSnapshot.toObject(ReadWriteManufacturerDetails.class);
                                List<String> products = readManufacturerDetails.getProducts();
                                if (products != null) {
                                    ArrayAdapter<String> prodAdapter = new ArrayAdapter<>(getActivity(), R.layout.drop_down_item, products);
                                    newBatchBinding.autoCompleteTextViewProduct.setAdapter(prodAdapter);
                                }else {
                                    newBatchBinding.autoCompleteTextViewProduct.setError("First create a new product");
                                }


                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Error..something went wrong", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });
    }
}