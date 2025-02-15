package com.project.drugmanagement.HomeScreen.Depo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.project.drugmanagement.Models.ReadWriteProductsArray;
import com.project.drugmanagement.Models.ReadWriteTransactionDepoDetails;
import com.project.drugmanagement.Models.ReadWriteTransactionDetails;
import com.project.drugmanagement.R;
import com.project.drugmanagement.databinding.FragmentFragmnetDepoSellBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragmnet_depo_sell#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragmnet_depo_sell extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "depo_sell";
    private static final String SELECTED_ROLE = "Depo";
    private static String depoName , prodName , wholesalerName , batch , pack;
    private static ArrayAdapter<String> packingAdapter;
    private FragmentFragmnetDepoSellBinding depoSellBinding;
    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragmnet_depo_sell() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragmnet_depo_sell.
     */
    // TODO: Rename and change types and number of parameters
    public static fragmnet_depo_sell newInstance(String param1, String param2) {
        fragmnet_depo_sell fragment = new fragmnet_depo_sell();
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
        //return inflater.inflate(R.layout.fragment_fragmnet_depo_sell, container, false);
        depoSellBinding = FragmentFragmnetDepoSellBinding.inflate(inflater, container, false);
        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        return depoSellBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //load wholesaler name into autoCompleteTextView
        loadWholesalerNames();
        //method loads currentDate into invoice Date
        loadInvoiceDate();
        // load products lists available to depo
        LoadProducts();

        //getSelected wholesaler name from autoCompleteTextView
        depoSellBinding.autoCompleteTextViewWholesaler.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                wholesalerName = adapterView.getItemAtPosition(i).toString();
            }
        });

        //getSelected ProductName from autoCompleteTextView
        depoSellBinding.autoCompleteTextViewProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                prodName = adapterView.getItemAtPosition(i).toString();
                //load all batches of selected prodName into autoCompleteTextView
                loadProductBatches(prodName);
            }
        });
        // getSelected batchNo and pack details from respective autoCompleteTextView
        depoSellBinding.autoCompleteTextViewProductBatch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                batch = adapterView.getItemAtPosition(i).toString();
                pack =  packingAdapter.getItem(i);
                depoSellBinding.autoCompleteTextViewProductPacking.setText(pack,false);
            }
        });

        depoSellBinding.btnSell.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            String invoiceNo = depoSellBinding.editTextInvoiceNo.getText().toString();
            String invoiceDate = depoSellBinding.editTextInvoicedate.getText().toString();
            String quantity = depoSellBinding.editTextQuantity.getText().toString();


            if (TextUtils.isEmpty(wholesalerName)) {
                Toast.makeText(getActivity(),"Please select wholesalerName ..",Toast.LENGTH_SHORT).show();
                depoSellBinding.autoCompleteTextViewWholesaler.setError("wholesalerName required");
            } else if (TextUtils.isEmpty(invoiceNo)) {
                Toast.makeText(getActivity(),"Please enter invoice no.",Toast.LENGTH_SHORT).show();
                depoSellBinding.editTextInvoiceNo.setError("invoice no required");
            } else if (TextUtils.isEmpty(invoiceDate)) {
                Toast.makeText(getActivity(),"Please enter invoice date.",Toast.LENGTH_SHORT).show();
                depoSellBinding.editTextInvoicedate.setError("invoice date required");
            } else if (TextUtils.isEmpty(quantity)) {
                Toast.makeText(getActivity(),"Please enter quantity.",Toast.LENGTH_SHORT).show();
                depoSellBinding.editTextQuantity.setError("quantity required");
            } else if (TextUtils.isEmpty(prodName)) {
                Toast.makeText(getActivity(),"Please select product.",Toast.LENGTH_SHORT).show();
                depoSellBinding.autoCompleteTextViewProduct.setError("Select product");
            }  else if (TextUtils.isEmpty(batch)) {
                Toast.makeText(getActivity(), "Please select batch", Toast.LENGTH_SHORT).show();
                depoSellBinding.autoCompleteTextViewProductBatch.setError("select product batch");
            } else if (TextUtils.isEmpty(pack)) {
                Toast.makeText(getActivity(),"select packing details",Toast.LENGTH_SHORT).show();
                depoSellBinding.autoCompleteTextViewProductPacking.setError("packing required");
            }  else {
                sellProduct(depoName,wholesalerName,invoiceNo,invoiceDate,prodName,batch,quantity,pack);
            }
        }
    });
}

    private void sellProduct(String depoNm, String wholesalerName, String invoiceNo, String invoiceDate, String prodName, String batch, String quantity, String pack) {
        CollectionReference collectionReference = db.collection("Transaction");
        int total = Integer.parseInt(quantity) * Integer.parseInt(pack);

        ReadWriteTransactionDepoDetails writeTransactionDetails = new ReadWriteTransactionDepoDetails(depoNm,wholesalerName,invoiceNo,invoiceDate,prodName,batch,quantity,pack ,total);

        //create new outward transaction into new depo subcollection in transaction
        collectionReference.document("Depo").collection(fragmnet_depo_sell.depoName).document("outward").collection(prodName).document(invoiceNo).set(writeTransactionDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                collectionReference.document("Depo").collection(fragmnet_depo_sell.depoName).document("outward").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Toast.makeText(getActivity(), "Transaction Successfully saved", Toast.LENGTH_SHORT).show();
                        depoSellBinding.editTextQuantity.setText("");
                        depoSellBinding.editTextInvoiceNo.setText("");
                        if (documentSnapshot.contains("productNames")){
                            collectionReference.document("Depo").collection(fragmnet_depo_sell.depoName).document("outward").update("productNames",FieldValue.arrayUnion(prodName));
                        }
                        else {
                            collectionReference.document("Depo").collection(fragmnet_depo_sell.depoName).document("outward").set(new ReadWriteProductsArray(),SetOptions.merge());
                            collectionReference.document("Depo").collection(fragmnet_depo_sell.depoName).document("outward").update("productNames",FieldValue.arrayUnion(prodName));
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(),"something went wrong ",Toast.LENGTH_SHORT).show();
                Log.d(TAG,e.toString());
            }
        });

        collectionReference.document("Wholesaler").collection(wholesalerName).document("inward").collection(prodName).document(invoiceNo).set(writeTransactionDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                collectionReference.document("Wholesaler").collection(wholesalerName).document("inward").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.contains("productNames")){
                            collectionReference.document("Wholesaler").collection(wholesalerName).document("inward").update("productNames",FieldValue.arrayUnion(prodName));
                        }
                        else {
                            collectionReference.document("Wholesaler").collection(wholesalerName).document("inward").set(new ReadWriteProductsArray(),SetOptions.merge());
                            collectionReference.document("Wholesaler").collection(wholesalerName).document("inward").update("productNames",FieldValue.arrayUnion(prodName));
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(),"something went wrong ",Toast.LENGTH_SHORT).show();
                Log.d(TAG,e.toString());
            }
        });
    }

    //load all batches of selected prodName into autoCompleteTextView
    private void loadProductBatches(String prodName) {
        CollectionReference collectionReference = db.collection("Transaction");
        collectionReference.document(SELECTED_ROLE).collection(depoName)
                .document("inward").collection(prodName)
                .whereEqualTo("productName",prodName)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    List<String> batches = new ArrayList<>();
                    List<String> packing = new ArrayList<>();
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            ReadWriteTransactionDetails readTransactionDetails = documentSnapshot.toObject(ReadWriteTransactionDetails.class);
                            batches.add(readTransactionDetails.getBatch());
                            packing.add(readTransactionDetails.getPack());
                        }
                        ArrayAdapter<String> batchAdapter = new ArrayAdapter<>(getActivity(), R.layout.drop_down_item, batches);
                        depoSellBinding.autoCompleteTextViewProductBatch.setAdapter(batchAdapter);

                        packingAdapter = new ArrayAdapter<>(getActivity(),R.layout.drop_down_item,packing);
                        depoSellBinding.autoCompleteTextViewProductPacking.setAdapter(packingAdapter);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Error..something went wrong", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });
    }

    //load products list into autoCompleteTextView
    private void LoadProducts() {
        CollectionReference depoReference = db.collection(SELECTED_ROLE);
        depoReference.whereEqualTo("email", firebaseUser.getEmail())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for (QueryDocumentSnapshot documentSnapshot1 : queryDocumentSnapshots) {
                            depoName = documentSnapshot1.getId();
                            CollectionReference collectionReference = db.collection("Transaction");
                            collectionReference.document(SELECTED_ROLE).collection(depoName).document("inward").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot2) {
                                    ReadWriteProductsArray readProductsArray = documentSnapshot2.toObject(ReadWriteProductsArray.class);
                                    ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(getActivity(), R.layout.drop_down_item, readProductsArray.getProductNames());
                                    depoSellBinding.autoCompleteTextViewProduct.setAdapter(monthAdapter);


                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, e.toString());
                                }
                            });
                        }
                    }
                });
    }

    // load wholesalers name in autoCompleteTextView
    private void loadWholesalerNames() {
        CollectionReference wholesalerReference = db.collection("Wholesaler");
        wholesalerReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<String> wholesalerList = new ArrayList<>();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    wholesalerList.add(documentSnapshot.getId());
                }
                ArrayAdapter<String> depoAdapter = new ArrayAdapter<>(getActivity(), R.layout.drop_down_item, wholesalerList);
                depoSellBinding.autoCompleteTextViewWholesaler.setAdapter(depoAdapter);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, e.toString());
            }
        });
    }

    //set Invoice Date
    private void loadInvoiceDate() {
        Calendar calendar = Calendar.getInstance();
        String[] monthName = {"jan", "feb", "mar", "apr", "may", "jun", "jul",
                "aug", "sep", "oct", "nov", "dec"};
        String month = monthName[calendar.get(Calendar.MONTH)];
        int year = calendar.get(Calendar.YEAR);
        depoSellBinding.editTextInvoicedate.setText(month + "" + year);
        depoSellBinding.editTextInvoicedate.setEnabled(false);
    }
}