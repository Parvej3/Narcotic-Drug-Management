package com.project.drugmanagement.HomeScreen.Retailer;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
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
import com.project.drugmanagement.Models.ReadWritePatientDetails;
import com.project.drugmanagement.Models.ReadWriteProductsArray;
import com.project.drugmanagement.Models.ReadWriteTransactionRetailerDetails;
import com.project.drugmanagement.Models.ReadWriteTransactionWholesalerDetails;
import com.project.drugmanagement.R;
import com.project.drugmanagement.databinding.FragmentRetailerSellBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_retailer_sell#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_retailer_sell extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "retailer_sell";
    private static final String SELECTED_ROLE = "Retailer";
    private DatePickerDialog picker;
    private static String  prodName ,  batch , pack ,retailerName , doctorName;
    private static ArrayAdapter<String> packingAdapter;
    private FragmentRetailerSellBinding retailerSellBinding;
    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragment_retailer_sell() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_retailer_sell.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_retailer_sell newInstance(String param1, String param2) {
        fragment_retailer_sell fragment = new fragment_retailer_sell();
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
       // return inflater.inflate(R.layout.fragment_retailer_sell, container, false);
        retailerSellBinding = FragmentRetailerSellBinding.inflate(inflater,container,false);
        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        return retailerSellBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //load doctors name
        loadDoctorName();
        // load products lists available to retailer
        LoadProducts();




        //getSelected doctor name from autoCompleteTextView
        retailerSellBinding.autoCompleteTextViewDoctor.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                doctorName = adapterView.getItemAtPosition(i).toString();
            }
        });

        //getSelected ProductName from autoCompleteTextView
        retailerSellBinding.autoCompleteTextViewProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                prodName = adapterView.getItemAtPosition(i).toString();
                //load all batches of selected prodName into autoCompleteTextView
                loadProductBatches(prodName);
            }
        });

        // getSelected batchNo and pack details from respective autoCompleteTextView
        retailerSellBinding.autoCompleteTextViewProductBatch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                batch = adapterView.getItemAtPosition(i).toString();
                pack =  packingAdapter.getItem(i);
                retailerSellBinding.autoCompleteTextViewProductPacking.setText(pack,false);
            }
        });

        retailerSellBinding.editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                picker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        retailerSellBinding.editTextDate.setText(i2+"/"+(i1+1)+"/"+i);
                    }
                },year,month,day);
                picker.show();
            }
        });
        retailerSellBinding.btnSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String invoiceNo = retailerSellBinding.editTextInvoiceNo.getText().toString();
                String aadharNo = retailerSellBinding.editTextAadharNo.getText().toString();
                String patientName = retailerSellBinding.editTextPatientName.getText().toString();
                String address = retailerSellBinding.editTextPatientAddr.getText().toString();
                String contact = retailerSellBinding.editTextPatientConatact.getText().toString();
                String quantity = retailerSellBinding.editTextQuantity.getText().toString();
                String date = retailerSellBinding.editTextDate.getText().toString();



                if (TextUtils.isEmpty(invoiceNo)) {
                    Toast.makeText(getActivity(),"Please enter invoice no.",Toast.LENGTH_SHORT).show();
                    retailerSellBinding.editTextInvoiceNo.setError("invoice no required");
                } else if (TextUtils.isEmpty(aadharNo)) {
                    Toast.makeText(getActivity(),"Please enter aadhar no.",Toast.LENGTH_SHORT).show();
                    retailerSellBinding.editTextInvoiceNo.setError("aadhar no required");
                } else if (TextUtils.isEmpty(patientName)) {
                    Toast.makeText(getActivity(),"Please enter patient Name.",Toast.LENGTH_SHORT).show();
                    retailerSellBinding.editTextPatientName.setError("patient Name required");
                } else if (TextUtils.isEmpty(address)) {
                    Toast.makeText(getActivity(),"Please enter address.",Toast.LENGTH_SHORT).show();
                    retailerSellBinding.editTextPatientAddr.setError("address required");
                } else if (TextUtils.isEmpty(contact)) {
                    Toast.makeText(getActivity(),"Please enter contact no.",Toast.LENGTH_SHORT).show();
                    retailerSellBinding.editTextPatientConatact.setError("patient contact no. required");
                } else if (TextUtils.isEmpty(quantity)) {
                    Toast.makeText(getActivity(),"Please enter quantity.",Toast.LENGTH_SHORT).show();
                    retailerSellBinding.editTextQuantity.setError("quantity required");
                } else if (TextUtils.isEmpty(date)) {
                    Toast.makeText(getActivity(),"Please enter Date.",Toast.LENGTH_SHORT).show();
                    retailerSellBinding.editTextQuantity.setError("Date required");
                } else if (TextUtils.isEmpty(doctorName)) {
                    Toast.makeText(getActivity(),"Please select doctor name.",Toast.LENGTH_SHORT).show();
                    retailerSellBinding.autoCompleteTextViewDoctor.setError("Select product");
                } else if (TextUtils.isEmpty(prodName)) {
                    Toast.makeText(getActivity(),"Please select product name.",Toast.LENGTH_SHORT).show();
                    retailerSellBinding.autoCompleteTextViewProduct.setError("Select product");
                } else if (TextUtils.isEmpty(batch)) {
                    Toast.makeText(getActivity(), "Please select batch", Toast.LENGTH_SHORT).show();
                    retailerSellBinding.autoCompleteTextViewProductBatch.setError("select product batch");
                } else if (TextUtils.isEmpty(pack)) {
                    Toast.makeText(getActivity(),"select packing details",Toast.LENGTH_SHORT).show();
                    retailerSellBinding.autoCompleteTextViewProductPacking.setError("packing required");
                }  else {
                    sellProduct(invoiceNo,aadharNo,patientName,address,contact,doctorName,prodName,batch,pack,quantity,date,retailerName);
                }
            }
        });

    }

    private void sellProduct(String invoiceNo, String aadharNo, String patientName, String address, String contact, String doctorName, String prodName, String batch, String pack, String quantity, String date, String retailerName) {
        CollectionReference collectionReference = db.collection("Transaction");
        int total = Integer.parseInt(quantity) * Integer.parseInt(pack);

        ReadWriteTransactionRetailerDetails writeTransactionDetails = new ReadWriteTransactionRetailerDetails(invoiceNo,aadharNo,patientName,address,contact,date,doctorName,prodName,quantity,batch,pack,total);

        //create new outward transaction into new retailer subcollection in transaction
        collectionReference.document(SELECTED_ROLE).collection(retailerName).document("outward").collection(prodName).document(invoiceNo).set(writeTransactionDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                collectionReference.document(SELECTED_ROLE).collection(retailerName).document("outward").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Toast.makeText(getActivity(), "Transaction Successfully saved", Toast.LENGTH_SHORT).show();
                        retailerSellBinding.editTextQuantity.setText("");
                        retailerSellBinding.editTextInvoiceNo.setText("");
                        retailerSellBinding.editTextDate.setText("");
                        retailerSellBinding.editTextPatientName.setText("");
                        retailerSellBinding.editTextPatientAddr.setText("");
                        retailerSellBinding.editTextPatientConatact.setText("");
                        retailerSellBinding.editTextAadharNo.setText("");
                        if (documentSnapshot.contains("productNames")){
                            collectionReference.document(SELECTED_ROLE).collection(retailerName).document("outward").update("productNames", FieldValue.arrayUnion(prodName));
                        }
                        else {
                            collectionReference.document(SELECTED_ROLE).collection(retailerName).document("outward").set(new ReadWriteProductsArray(), SetOptions.merge());
                            collectionReference.document(SELECTED_ROLE).collection(retailerName).document("outward").update("productNames",FieldValue.arrayUnion(prodName));
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

        CollectionReference patientReference = db.collection("Patient");

        Map<String,String> patientDetailsMap = new HashMap<>();
        patientDetailsMap.put("doctor",doctorName);
        patientDetailsMap.put("date",date);
        patientDetailsMap.put("product",prodName);
        patientDetailsMap.put("total", String.valueOf(total));
        patientDetailsMap.put("Retailer",retailerName);

        ReadWritePatientDetails writePatientDetails = new ReadWritePatientDetails(aadharNo,patientName,address,contact);

        patientReference.document(aadharNo).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    patientReference.document(aadharNo).update("prescriptions",FieldValue.arrayUnion(patientDetailsMap));
                    Toast.makeText(getActivity(),"Done",Toast.LENGTH_LONG).show();
                } else {
                    patientReference.document(aadharNo).set(writePatientDetails,SetOptions.merge());
                    patientReference.document(aadharNo).update("prescriptions",FieldValue.arrayUnion(patientDetailsMap));
                    Toast.makeText(getActivity(),"Double Done",Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,e.toString());
            }
        });


    }



    private void LoadProducts() {
        CollectionReference depoReference = db.collection(SELECTED_ROLE);
        depoReference.whereEqualTo("email", firebaseUser.getEmail())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for (QueryDocumentSnapshot documentSnapshot1 : queryDocumentSnapshots) {
                            retailerName = documentSnapshot1.getId();
                            CollectionReference collectionReference = db.collection("Transaction");
                            collectionReference.document(SELECTED_ROLE).collection(retailerName).document("inward").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot2) {
                                    ReadWriteProductsArray readProductsArray = documentSnapshot2.toObject(ReadWriteProductsArray.class);
                                    ArrayAdapter<String> productAdapter = new ArrayAdapter<>(getActivity(), R.layout.drop_down_item, readProductsArray.getProductNames());
                                    retailerSellBinding.autoCompleteTextViewProduct.setAdapter(productAdapter);


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
        collectionReference.document(SELECTED_ROLE).collection(retailerName)
                .document("inward").collection(prodName)
                .whereEqualTo("productName",prodName)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    List<String> batches = new ArrayList<>();
                    List<String> packing = new ArrayList<>();
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            ReadWriteTransactionWholesalerDetails readTransactionDetails = documentSnapshot.toObject(ReadWriteTransactionWholesalerDetails.class);
                            batches.add(readTransactionDetails.getBatch());
                            packing.add(readTransactionDetails.getPack());
                        }
                        ArrayAdapter<String> batchAdapter = new ArrayAdapter<>(getActivity(), R.layout.drop_down_item, batches);
                        retailerSellBinding.autoCompleteTextViewProductBatch.setAdapter(batchAdapter);

                        packingAdapter = new ArrayAdapter<>(getActivity(),R.layout.drop_down_item,packing);
                        retailerSellBinding.autoCompleteTextViewProductPacking.setAdapter(packingAdapter);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Error..something went wrong", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });
    }
    private void loadDoctorName() {
        CollectionReference doctorReference = db.collection("Doctor");
        doctorReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<String> doctorList = new ArrayList<>();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    doctorList.add(documentSnapshot.getId());
                }
                ArrayAdapter<String> doctorAdapter = new ArrayAdapter<>(getActivity(), R.layout.drop_down_item, doctorList);
                retailerSellBinding.autoCompleteTextViewDoctor.setAdapter(doctorAdapter);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, e.toString());
            }
        });
    }
}