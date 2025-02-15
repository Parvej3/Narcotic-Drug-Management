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
import com.project.drugmanagement.Models.ReadWriteDepoArray;
import com.project.drugmanagement.Models.ReadWriteManufacturerDetails;
import com.project.drugmanagement.Models.ReadWriteMonthArray;
import com.project.drugmanagement.Models.ReadWriteProductionDetails;
import com.project.drugmanagement.Models.ReadWriteProductsArray;
import com.project.drugmanagement.Models.ReadWriteTransactionDetails;
import com.project.drugmanagement.R;
import com.project.drugmanagement.databinding.FragmentSellBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_sell#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_sell extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;
    private static final String TAG = "Sell_manufacturer";
    private static final String SELECTED_ROLE = "Manufacturer";
    String prodName, month ,depoName ,batch , pack , companyName;
    FragmentSellBinding sellBinding;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ArrayAdapter<String> packingAdapter;
    public fragment_sell() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_sell.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_sell newInstance(String param1, String param2) {
        fragment_sell fragment = new fragment_sell();
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
        //return inflater.inflate(R.layout.fragment_sell, container, false);

        sellBinding = FragmentSellBinding.inflate(inflater, container, false);
        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        return sellBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //method load company specific products into dropdownList
        loadProductDetails();
        //method loads currentDate into invoice Date
        loadMfgDate();
        //method loads all depo names into dropdownlist2
        loadDepo();

        // get selected  depoName from dropdown list
        sellBinding.autoCompleteTextViewDepo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                depoName = adapterView.getItemAtPosition(i).toString();
            }
        });

        //get selected ProductName from dropdown list
        sellBinding.autoCompleteTextViewProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                prodName = adapterView.getItemAtPosition(i).toString();

                //method loads all months when manufacturing has done
                loadMonthOfMfg();
            }
        });

        //get selected MfgMonth from dropdown list
        sellBinding.autoCompleteTextViewMfg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                month = adapterView.getItemAtPosition(i).toString();

                //method loads all batches of specific product
                loadProductBatch(month, prodName);
            }
        });

        // get BatchNo from dropdown list
        sellBinding.autoCompleteTextViewProductBatch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                batch = adapterView.getItemAtPosition(i).toString();
                pack =  packingAdapter.getItem(i);
                sellBinding.autoCompleteTextViewProductPacking.setText(pack,false);
            }
        });


        sellBinding.btnSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String invoiceNo = sellBinding.editTextInvoiceNo.getText().toString();
                String invoiceDate = sellBinding.editTextInvoicedate.getText().toString();
                String quantity = sellBinding.editTextQuantity.getText().toString();


                if (TextUtils.isEmpty(depoName)) {
                    Toast.makeText(getActivity(),"Please select depoName ..",Toast.LENGTH_SHORT).show();
                    sellBinding.autoCompleteTextViewDepo.setError("depoName required");
                } else if (TextUtils.isEmpty(invoiceNo)) {
                    Toast.makeText(getActivity(),"Please enter invoice no.",Toast.LENGTH_SHORT).show();
                    sellBinding.editTextInvoiceNo.setError("invoice no required");
                } else if (TextUtils.isEmpty(invoiceDate)) {
                    Toast.makeText(getActivity(),"Please enter invoice date.",Toast.LENGTH_SHORT).show();
                    sellBinding.editTextInvoicedate.setError("invoice date required");
                } else if (TextUtils.isEmpty(quantity)) {
                    Toast.makeText(getActivity(),"Please enter quantity.",Toast.LENGTH_SHORT).show();
                    sellBinding.editTextQuantity.setError("quantity required");
                } else if (TextUtils.isEmpty(prodName)) {
                    Toast.makeText(getActivity(),"Please select product.",Toast.LENGTH_SHORT).show();
                    sellBinding.autoCompleteTextViewProduct.setError("Select product");
                } else if (TextUtils.isEmpty(month)) {
                    Toast.makeText(getActivity(),"Please select mfg month.",Toast.LENGTH_SHORT).show();
                    sellBinding.autoCompleteTextViewMfg.setError("select mfg month");
                } else if (TextUtils.isEmpty(batch)) {
                    Toast.makeText(getActivity(), "Please select batch", Toast.LENGTH_SHORT).show();
                    sellBinding.autoCompleteTextViewProductBatch.setError("select product batch");
                } else if (TextUtils.isEmpty(pack)) {
                    Toast.makeText(getActivity(),"select packing details",Toast.LENGTH_SHORT).show();
                    sellBinding.autoCompleteTextViewProductPacking.setError("packing required");
                } else if (TextUtils.isEmpty(companyName)) {
                    Toast.makeText(getActivity(),"Company Name is empty",Toast.LENGTH_SHORT).show();
                } else {
                    sellProduct(depoName,invoiceNo,invoiceDate,prodName,month,batch,quantity,pack);
                }
            }
        });


    }

    private void sellProduct(String depoName, String invoiceNo, String invoiceDate, String prodName, String month, String batch, String quantity, String pack) {
        CollectionReference collectionReference = db.collection("Transaction");
       int total = Integer.parseInt(quantity) * Integer.parseInt(pack);

        ReadWriteTransactionDetails writeTransactionDetails = new ReadWriteTransactionDetails(depoName,invoiceNo,invoiceDate,prodName,companyName,month,batch,quantity,pack,total);

        //creates dopoNames array if it not exist or add new value in it if exists
        collectionReference.document(firebaseUser.getDisplayName()).collection(companyName).document(month).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.contains("depoNames")){
                        collectionReference.document(firebaseUser.getDisplayName()).collection(companyName).document(month).update("depoNames",FieldValue.arrayUnion(depoName));
                    }
                    else {
                        collectionReference.document(firebaseUser.getDisplayName()).collection(companyName).document(month).set(new ReadWriteDepoArray(), SetOptions.merge());
                        collectionReference.document(firebaseUser.getDisplayName()).collection(companyName).document(month).update("depoNames",FieldValue.arrayUnion(depoName));
                    }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,e.toString());
                Toast.makeText(getActivity(), "Something went wrong..", Toast.LENGTH_SHORT).show();
            }
        });

        //create a new transaction in manufacturer
        collectionReference.document(firebaseUser.getDisplayName()).collection(companyName)
                .document(month).collection(depoName).document(invoiceNo).set(writeTransactionDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getActivity(), "Transaction Successfully saved", Toast.LENGTH_SHORT).show();
                        sellBinding.editTextQuantity.setText("");
                        sellBinding.editTextInvoiceNo.setText("");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(),"something went wrong ",Toast.LENGTH_SHORT).show();
                        Log.d(TAG,e.toString());
                    }
                });

        //create new inward transaction into new depo subcollection in transaction
        collectionReference.document("Depo").collection(depoName).document("inward").collection(prodName).document(invoiceNo).set(writeTransactionDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                collectionReference.document("Depo").collection(depoName).document("inward").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.contains("productNames")){
                            collectionReference.document("Depo").collection(depoName).document("inward").update("productNames",FieldValue.arrayUnion(prodName));
                        }
                        else {
                            collectionReference.document("Depo").collection(depoName).document("inward").set(new ReadWriteProductsArray(),SetOptions.merge());
                            collectionReference.document("Depo").collection(depoName).document("inward").update("productNames",FieldValue.arrayUnion(prodName));
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

    private void loadMonthOfMfg() {
        final String[] companyName = new String[1];
        CollectionReference collectionReference = db.collection(SELECTED_ROLE);
        collectionReference
                .whereEqualTo("email", firebaseUser.getEmail())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            companyName[0] = documentSnapshot.getId();
                            CollectionReference monthReference = db.collection("Production");
                            // get months list from manufacturer_name document
                            monthReference.document(companyName[0]).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    ReadWriteMonthArray readMonthArray = documentSnapshot.toObject(ReadWriteMonthArray.class);
                                    ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(getActivity(), R.layout.drop_down_item, readMonthArray.getMonths());
                                    sellBinding.autoCompleteTextViewMfg.setAdapter(monthAdapter);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, e.toString());
                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.toString());
                    }
                });
    }

    private void loadProductBatch(String month, String prodName) {
        final String[] companyName = new String[1];
        CollectionReference collectionReference = db.collection(SELECTED_ROLE);
        collectionReference
                .whereEqualTo("email", firebaseUser.getEmail())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                            companyName[0] = documentSnapshot.getId();
                            CollectionReference monthReference = db.collection("Production");
                            monthReference.document(companyName[0]).collection(month)
                                    .whereEqualTo("name", prodName)
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            List<String> batches = new ArrayList<>();
                                            List<String> packing = new ArrayList<>();
                                            for (QueryDocumentSnapshot documentSnapshot1 : queryDocumentSnapshots) {
                                                //add each batch no of specific product in array
                                                ReadWriteProductionDetails readProductionDetails = documentSnapshot1.toObject(ReadWriteProductionDetails.class);
                                                batches.add(readProductionDetails.getBatchNo());
                                                packing.add(readProductionDetails.getPack());
                                            }
                                            ArrayAdapter<String> batchAdapter = new ArrayAdapter<>(getActivity(), R.layout.drop_down_item, batches);
                                            sellBinding.autoCompleteTextViewProductBatch.setAdapter(batchAdapter);

                                            packingAdapter = new ArrayAdapter<>(getActivity(),R.layout.drop_down_item,packing);
                                            sellBinding.autoCompleteTextViewProductPacking.setAdapter(packingAdapter);
                                        }
                                    });


                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.toString());
                    }
                });

    }

    //method loads all depo names into dropdownlist2
    private void loadDepo() {
        CollectionReference depoReference = db.collection("Depo");
        depoReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<String> depoList = new ArrayList<>();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    depoList.add(documentSnapshot.getId());
                }
                ArrayAdapter<String> depoAdapter = new ArrayAdapter<>(getActivity(), R.layout.drop_down_item, depoList);
                sellBinding.autoCompleteTextViewDepo.setAdapter(depoAdapter);
            }
        });
    }

    //method loads currentDate into invoice Date
    private void loadMfgDate() {
        Calendar calendar = Calendar.getInstance();
        String[] monthName = {"jan", "feb", "mar", "apr", "may", "jun", "jul",
                "aug", "sep", "oct", "nov", "dec"};
        String month = monthName[calendar.get(Calendar.MONTH)];
        int year = calendar.get(Calendar.YEAR);
        sellBinding.editTextInvoicedate.setText(month + "" + year);

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
                            companyName = documentSnapshot.getId();
                            ReadWriteManufacturerDetails readManufacturerDetails = documentSnapshot.toObject(ReadWriteManufacturerDetails.class);
                            List<String> products = readManufacturerDetails.getProducts();
                            if (products != null) {
                                ArrayAdapter<String> prodAdapter = new ArrayAdapter<>(getActivity(), R.layout.drop_down_item, products);
                                sellBinding.autoCompleteTextViewProduct.setAdapter(prodAdapter);
                            }else {
                                sellBinding.autoCompleteTextViewProduct.setError("First create a new product");
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