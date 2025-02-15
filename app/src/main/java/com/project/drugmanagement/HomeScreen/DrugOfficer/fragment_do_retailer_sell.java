package com.project.drugmanagement.HomeScreen.DrugOfficer;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
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
import com.project.drugmanagement.Models.ReadWriteProductsArray;
import com.project.drugmanagement.Models.ReadWriteTransactionRetailerDetails;
import com.project.drugmanagement.R;
import com.project.drugmanagement.databinding.FragmentDoRetailerSellBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_do_retailer_sell#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_do_retailer_sell extends Fragment {
    private static final String TAG = "do_reatiler_sell";
    private DatePickerDialog picker;
    private static String retailerName, prodName, month, year;
    //private static ArrayAdapter<String> packingAdapter;
    private FragmentDoRetailerSellBinding retailerSellBinding;
    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragment_do_retailer_sell() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_do_retailer_sell.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_do_retailer_sell newInstance(String param1, String param2) {
        fragment_do_retailer_sell fragment = new fragment_do_retailer_sell();
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
        //        return inflater.inflate(R.layout.fragment_do_depo_purchase, container, false);
        retailerSellBinding = FragmentDoRetailerSellBinding.inflate(inflater, container, false);
        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        return retailerSellBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // load retailer name in autoCompleteTextView
        loadRetailerNames();

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

        //getSelected retailer name from autoCompleteTextView
        retailerSellBinding.autoCompleteTextViewRetailer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                retailerName = adapterView.getItemAtPosition(i).toString();
                // load productNames into autoCompleteTextView
                loadProductNames(retailerName);
            }
        });
        //getSelected product name from autoCompleteTextView
        retailerSellBinding.autoCompleteTextViewProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                prodName = adapterView.getItemAtPosition(i).toString();
            }
        });


        retailerSellBinding.btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String date = retailerSellBinding.editTextDate.getText().toString();

                if (TextUtils.isEmpty(retailerName)) {
                    Toast.makeText(getActivity(), "Please select retailer Name ..", Toast.LENGTH_SHORT).show();
                    retailerSellBinding.autoCompleteTextViewRetailer.setError("retailerName required");
                } else if (TextUtils.isEmpty(prodName)) {
                    Toast.makeText(getActivity(), "Please select product.", Toast.LENGTH_SHORT).show();
                    retailerSellBinding.autoCompleteTextViewProduct.setError("productName required");
                } else if (TextUtils.isEmpty(date)) {
                    Toast.makeText(getActivity(), "Please select date", Toast.LENGTH_SHORT).show();
                    retailerSellBinding.editTextDate.setError("date required");
                } else {
                    getDetails(date);
                }
            }
        });
    }
    private void getDetails(String date) {


        CollectionReference productionReference = db.collection("Transaction");
        productionReference.document("Retailer").collection(retailerName)
                .document("outward").collection(prodName)
                //.whereEqualTo("invoiceDate", date)
                .whereEqualTo("date",date)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        String data = "";
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            if (documentSnapshot.exists()) {
                                ReadWriteTransactionRetailerDetails readTransactionDetails = documentSnapshot.toObject(ReadWriteTransactionRetailerDetails.class);
                                data += "Invoice No :" + readTransactionDetails.getInvoiceNo() + "\n"
                                        +"Date :"+readTransactionDetails.getDate()+"\n"
                                        +"Patient Name :"+readTransactionDetails.getPatientName()+"\n"
                                        +"Aadhar No :"+readTransactionDetails.getAadharNo()+"\n"
                                        +"Address :"+readTransactionDetails.getAddress()+"\n"
                                        +"Doctor Name :"+readTransactionDetails.getDoctor()+"\n"
                                        + "Batch No :" + readTransactionDetails.getBatch() + "\n"
                                        + "Quantity :" + readTransactionDetails.getQuantity() + "\n"
                                        + "Pack :" + readTransactionDetails.getPack() + "\n"
                                        + "Total :" + readTransactionDetails.getTotal();
                                data += " \n   ________________ \n\n";
                            }
                            else {
                                data += "no details found";
                            }

                        }
                        retailerSellBinding.textViewDetails.setText(data);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,e.toString());
                        Toast.makeText(getActivity(), "Error..something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    //load product names
    private void loadProductNames(String retailerNm) {
        CollectionReference collectionReference = db.collection("Transaction");
        collectionReference.document("Retailer").collection(retailerNm)
                .document("outward")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            ReadWriteProductsArray readProductsArray = documentSnapshot.toObject(ReadWriteProductsArray.class);
                            List<String> products = readProductsArray.getProductNames();
                            if (products != null) {
                                ArrayAdapter<String> prodAdapter = new ArrayAdapter<>(getActivity(), R.layout.drop_down_item, products);
                                retailerSellBinding.autoCompleteTextViewProduct.setAdapter(prodAdapter);
                            } else {
                                retailerSellBinding.autoCompleteTextViewProduct.setError("no transaction yet");
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
                retailerSellBinding.autoCompleteTextViewRetailer.setAdapter(retailerAdapter);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, e.toString());
            }
        });
    }
}