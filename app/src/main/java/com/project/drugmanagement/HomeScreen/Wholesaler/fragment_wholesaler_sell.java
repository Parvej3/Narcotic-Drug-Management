package com.project.drugmanagement.HomeScreen.Wholesaler;

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
import com.project.drugmanagement.Models.ReadWriteTransactionWholesalerDetails;
import com.project.drugmanagement.R;
import com.project.drugmanagement.databinding.FragmentWholesalerSellBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_wholesaler_sell#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_wholesaler_sell extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private final String TAG = "wholesaler_sell";
    private final String SELECTED_ROLE ="Wholesaler";
    private String wholeSalerName , prodName , retailerName , batch , pack;
    private static ArrayAdapter<String> packingAdapter;
    private FragmentWholesalerSellBinding wholesalerSellBinding;
    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;

    public fragment_wholesaler_sell() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_wholesaler_sell.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_wholesaler_sell newInstance(String param1, String param2) {
        fragment_wholesaler_sell fragment = new fragment_wholesaler_sell();
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
        //return inflater.inflate(R.layout.fragment_wholesaler_sell, container, false);
        wholesalerSellBinding = FragmentWholesalerSellBinding.inflate(inflater,container,false);
        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        return wholesalerSellBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //load wholesaler name into autoCompleteTextView
        loadRetailerNames();
        //method loads currentDate into invoice Date
        loadInvoiceDate();
        // load products lists available to wholesaler
        LoadProducts();

        //getSelected wholesaler name from autoCompleteTextView
        wholesalerSellBinding.autoCompleteTextViewRetailer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                retailerName = adapterView.getItemAtPosition(i).toString();
            }
        });

        //getSelected ProductName from autoCompleteTextView
        wholesalerSellBinding.autoCompleteTextViewProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                prodName = adapterView.getItemAtPosition(i).toString();
                //load all batches of selected prodName into autoCompleteTextView
                loadProductBatches(prodName);
            }
        });
        // getSelected batchNo and pack details from respective autoCompleteTextView
        wholesalerSellBinding.autoCompleteTextViewProductBatch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                batch = adapterView.getItemAtPosition(i).toString();
                pack =  packingAdapter.getItem(i);
                wholesalerSellBinding.autoCompleteTextViewProductPacking.setText(pack,false);
            }
        });

        wholesalerSellBinding.btnSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String invoiceNo = wholesalerSellBinding.editTextInvoiceNo.getText().toString();
                String invoiceDate = wholesalerSellBinding.editTextInvoicedate.getText().toString();
                String quantity = wholesalerSellBinding.editTextQuantity.getText().toString();


                if (TextUtils.isEmpty(retailerName)) {
                    Toast.makeText(getActivity(),"Please select retailerName ..",Toast.LENGTH_SHORT).show();
                    wholesalerSellBinding.autoCompleteTextViewRetailer.setError("retailerName required");
                } else if (TextUtils.isEmpty(invoiceNo)) {
                    Toast.makeText(getActivity(),"Please enter invoice no.",Toast.LENGTH_SHORT).show();
                    wholesalerSellBinding.editTextInvoiceNo.setError("invoice no required");
                } else if (TextUtils.isEmpty(invoiceDate)) {
                    Toast.makeText(getActivity(),"Please enter invoice date.",Toast.LENGTH_SHORT).show();
                    wholesalerSellBinding.editTextInvoicedate.setError("invoice date required");
                } else if (TextUtils.isEmpty(quantity)) {
                    Toast.makeText(getActivity(),"Please enter quantity.",Toast.LENGTH_SHORT).show();
                    wholesalerSellBinding.editTextQuantity.setError("quantity required");
                } else if (TextUtils.isEmpty(prodName)) {
                    Toast.makeText(getActivity(),"Please select product.",Toast.LENGTH_SHORT).show();
                    wholesalerSellBinding.autoCompleteTextViewProduct.setError("Select product");
                }  else if (TextUtils.isEmpty(batch)) {
                    Toast.makeText(getActivity(), "Please select batch", Toast.LENGTH_SHORT).show();
                    wholesalerSellBinding.autoCompleteTextViewProductBatch.setError("select product batch");
                } else if (TextUtils.isEmpty(pack)) {
                    Toast.makeText(getActivity(),"select packing details",Toast.LENGTH_SHORT).show();
                    wholesalerSellBinding.autoCompleteTextViewProductPacking.setError("packing required");
                }  else {
                    sellProduct(wholeSalerName,retailerName,invoiceNo,invoiceDate,prodName,batch,quantity,pack);
                }
            }
        });
    }

    private void sellProduct(String wholeSalerName, String retailerName, String invoiceNo, String invoiceDate, String prodName, String batch, String quantity, String pack) {
        CollectionReference collectionReference = db.collection("Transaction");
        int total = Integer.parseInt(quantity) * Integer.parseInt(pack);

        ReadWriteTransactionWholesalerDetails writeTransactionDetails = new ReadWriteTransactionWholesalerDetails(wholeSalerName,retailerName,invoiceNo,invoiceDate,prodName,batch,quantity,pack,total);

        //create new outward transaction into new wholesaler subcollection in transaction
        collectionReference.document(SELECTED_ROLE).collection(wholeSalerName).document("outward").collection(prodName).document(invoiceNo).set(writeTransactionDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                collectionReference.document(SELECTED_ROLE).collection(wholeSalerName).document("outward").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Toast.makeText(getActivity(), "Transaction Successfully saved", Toast.LENGTH_SHORT).show();
                        wholesalerSellBinding.editTextQuantity.setText("");
                        wholesalerSellBinding.editTextInvoiceNo.setText("");
                        if (documentSnapshot.contains("productNames")){
                            collectionReference.document(SELECTED_ROLE).collection(wholeSalerName).document("outward").update("productNames", FieldValue.arrayUnion(prodName));
                        }
                        else {
                            collectionReference.document(SELECTED_ROLE).collection(wholeSalerName).document("outward").set(new ReadWriteProductsArray(), SetOptions.merge());
                            collectionReference.document(SELECTED_ROLE).collection(wholeSalerName).document("outward").update("productNames",FieldValue.arrayUnion(prodName));
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

        collectionReference.document("Retailer").collection(retailerName).document("inward").collection(prodName).document(invoiceNo).set(writeTransactionDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                collectionReference.document("Retailer").collection(retailerName).document("inward").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.contains("productNames")){
                            collectionReference.document("Retailer").collection(retailerName).document("inward").update("productNames",FieldValue.arrayUnion(prodName));
                        }
                        else {
                            collectionReference.document("Retailer").collection(retailerName).document("inward").set(new ReadWriteProductsArray(),SetOptions.merge());
                            collectionReference.document("Retailer").collection(retailerName).document("inward").update("productNames",FieldValue.arrayUnion(prodName));
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

    //load products list into autoCompleteTextView
    private void LoadProducts() {
        CollectionReference wholesalerReference = db.collection(SELECTED_ROLE);
        wholesalerReference.whereEqualTo("email", firebaseUser.getEmail())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for (QueryDocumentSnapshot documentSnapshot1 : queryDocumentSnapshots) {
                            wholeSalerName = documentSnapshot1.getId();
                            CollectionReference collectionReference = db.collection("Transaction");
                            collectionReference.document(SELECTED_ROLE).collection(wholeSalerName).document("inward").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot2) {
                                    ReadWriteProductsArray readProductsArray = documentSnapshot2.toObject(ReadWriteProductsArray.class);
                                    ArrayAdapter<String> productAdapter = new ArrayAdapter<>(getActivity(), R.layout.drop_down_item, readProductsArray.getProductNames());
                                    wholesalerSellBinding.autoCompleteTextViewProduct.setAdapter(productAdapter);


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
    //load all batches of selected prodName into autoCompleteTextView
    private void loadProductBatches(String prodName) {
        CollectionReference collectionReference = db.collection("Transaction");
        collectionReference.document(SELECTED_ROLE).collection(wholeSalerName)
                .document("inward").collection(prodName)
                .whereEqualTo("productName",prodName)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    List<String> batches = new ArrayList<>();
                    List<String> packing = new ArrayList<>();
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            ReadWriteTransactionDepoDetails readTransactionDetails = documentSnapshot.toObject(ReadWriteTransactionDepoDetails.class);
                            batches.add(readTransactionDetails.getBatch());
                            packing.add(readTransactionDetails.getPack());
                        }
                        ArrayAdapter<String> batchAdapter = new ArrayAdapter<>(getActivity(), R.layout.drop_down_item, batches);
                        wholesalerSellBinding.autoCompleteTextViewProductBatch.setAdapter(batchAdapter);

                        packingAdapter = new ArrayAdapter<>(getActivity(),R.layout.drop_down_item,packing);
                        wholesalerSellBinding.autoCompleteTextViewProductPacking.setAdapter(packingAdapter);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Error..something went wrong", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });
    }

    // load retailer name in autoCompleteTextView
    private void loadRetailerNames() {
        CollectionReference retailerReference = db.collection("Retailer");
        retailerReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<String> retailerList = new ArrayList<>();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    retailerList.add(documentSnapshot.getId());
                }
                ArrayAdapter<String> retailerAdapter = new ArrayAdapter<>(getActivity(), R.layout.drop_down_item, retailerList);
                wholesalerSellBinding.autoCompleteTextViewRetailer.setAdapter(retailerAdapter);
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
        wholesalerSellBinding.editTextInvoicedate.setText(month + "" + year);
        wholesalerSellBinding.editTextInvoicedate.setEnabled(false);
    }
}