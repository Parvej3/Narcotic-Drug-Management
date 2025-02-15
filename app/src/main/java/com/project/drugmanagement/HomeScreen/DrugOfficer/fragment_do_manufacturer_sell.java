package com.project.drugmanagement.HomeScreen.DrugOfficer;

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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.drugmanagement.Models.ReadWriteDepoArray;
import com.project.drugmanagement.Models.ReadWriteManufacturerDetails;
import com.project.drugmanagement.Models.ReadWriteMonthArray;
import com.project.drugmanagement.Models.ReadWriteProductionDetails;
import com.project.drugmanagement.Models.ReadWriteTransactionDetails;
import com.project.drugmanagement.R;
import com.project.drugmanagement.databinding.FragmentDoManfacturerProductionBinding;
import com.project.drugmanagement.databinding.FragmentDoManufacturerSellBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_do_manufacturer_sell#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_do_manufacturer_sell extends Fragment {
    private static final String TAG = "do_manufacturer_sell";
    private static String manufacturerName, prodName, month, depoName;
    //private static ArrayAdapter<String> packingAdapter;
    private FragmentDoManufacturerSellBinding sellBinding;
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

    public fragment_do_manufacturer_sell() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_do_manufacturer_sell.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_do_manufacturer_sell newInstance(String param1, String param2) {
        fragment_do_manufacturer_sell fragment = new fragment_do_manufacturer_sell();
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
        // return inflater.inflate(R.layout.fragment_do_manfacturer_production, container, false);
        sellBinding = FragmentDoManufacturerSellBinding.inflate(inflater, container, false);
        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        return sellBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //load manufacturer name into autoCompleteTextView
        loadManufacturerNames();


        //getSelected manufacturer name from autoCompleteTextView
        sellBinding.autoCompleteTextViewManufacturer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                manufacturerName = adapterView.getItemAtPosition(i).toString();
                // load productNames into autoCompleteTextView
                loadProductNames(manufacturerName);
            }
        });

        //getSelected productName
        sellBinding.autoCompleteTextViewProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                prodName = adapterView.getItemAtPosition(i).toString();
                //load months
                loadMonths();
            }
        });

        //getSelected Month
        sellBinding.autoCompleteTextViewMonth.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                month = adapterView.getItemAtPosition(i).toString();
                // load DepoNames
                loadDepo();
            }
        });

        //getSelected Month
        sellBinding.autoCompleteTextViewDepo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                depoName = adapterView.getItemAtPosition(i).toString();
            }
        });

        sellBinding.btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(manufacturerName)) {
                    Toast.makeText(getActivity(), "Please select manufacturerName ..", Toast.LENGTH_SHORT).show();
                    sellBinding.autoCompleteTextViewManufacturer.setError("manufacturerName required");
                } else if (TextUtils.isEmpty(prodName)) {
                    Toast.makeText(getActivity(), "Please select product.", Toast.LENGTH_SHORT).show();
                    sellBinding.autoCompleteTextViewProduct.setError("productName required");
                } else if (TextUtils.isEmpty(month)) {
                    Toast.makeText(getActivity(), "Please select month", Toast.LENGTH_SHORT).show();
                    sellBinding.autoCompleteTextViewMonth.setError("month required");
                } else if (TextUtils.isEmpty(depoName)) {
                    Toast.makeText(getActivity(), "Please select depo", Toast.LENGTH_SHORT).show();
                    sellBinding.autoCompleteTextViewMonth.setError("month required");
                } else {
                    getDetails();
                }
            }
        });

    }

    private void getDetails() {
        CollectionReference productionReference = db.collection("Transaction");

        productionReference.document("Manufacturer").collection(manufacturerName)
                .document(month).collection(depoName)
                .whereEqualTo("productName", prodName)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        String data ="";
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots ) {
                            if (documentSnapshot.exists()){
                                ReadWriteTransactionDetails readTransactionDetails = documentSnapshot.toObject(ReadWriteTransactionDetails.class);
                                data += "Invoice No :"+readTransactionDetails.getInvoiceNo() +"\n"
                                        +"Batch No :"+readTransactionDetails.getBatch()+"\n"
                                        +"Quantity :"+readTransactionDetails.getQuantity()+"\n"
                                        +"Pack :"+readTransactionDetails.getPack()+"\n"
                                        +"Total :"+readTransactionDetails.getTotal();
                                data += " \n\n    ________________ ";
                            }
                            else {
                                sellBinding.textViewDetails.setText("No details found");
                            }
                        }

                        sellBinding.textViewDetails.setText(data);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.toString());
                        Toast.makeText(getActivity(), "Something went wrong ..", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //method loads all depo names into dropdownlist2
    private void loadDepo() {
        CollectionReference collectionReference = db.collection("Transaction");
        collectionReference
                .document("Manufacturer").collection(manufacturerName).document(month)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            ReadWriteDepoArray readDepoArray = documentSnapshot.toObject(ReadWriteDepoArray.class);
                            ArrayAdapter<String> depoAdapter = new ArrayAdapter<>(getActivity(), R.layout.drop_down_item, readDepoArray.getDepoNames());
                            sellBinding.autoCompleteTextViewDepo.setAdapter(depoAdapter);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.toString());
                    }
                });
    }

    //load month names
    private void loadMonths() {

        CollectionReference collectionReference = db.collection("Transaction");
        collectionReference
                .document("Manufacturer").collection(manufacturerName)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<String> monthNames = new ArrayList<>();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            if (documentSnapshot.exists()) {
                                monthNames.add(documentSnapshot.getId());
                            }
                        }
                        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(getActivity(), R.layout.drop_down_item, monthNames);
                        sellBinding.autoCompleteTextViewMonth.setAdapter(monthAdapter);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.toString());
                    }
                });
    }


    //load product names
    private void loadProductNames(String manufacName) {
        CollectionReference collectionReference = db.collection("Manufacturer");
        collectionReference
                .whereEqualTo("name", manufacName)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            ReadWriteManufacturerDetails readManufacturerDetails = documentSnapshot.toObject(ReadWriteManufacturerDetails.class);
                            List<String> products = readManufacturerDetails.getProducts();
                            if (products != null) {
                                ArrayAdapter<String> prodAdapter = new ArrayAdapter<>(getActivity(), R.layout.drop_down_item, products);
                                sellBinding.autoCompleteTextViewProduct.setAdapter(prodAdapter);
                            } else {
                                sellBinding.autoCompleteTextViewProduct.setError("Products not created yet");
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

    //load manufacturer name into autoCompleteTextView
    private void loadManufacturerNames() {
        CollectionReference manufacturerReference = db.collection("Manufacturer");
        manufacturerReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<String> manufacturerList = new ArrayList<>();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    manufacturerList.add(documentSnapshot.getId());
                }
                ArrayAdapter<String> manufacturerAdapter = new ArrayAdapter<>(getActivity(), R.layout.drop_down_item, manufacturerList);
                sellBinding.autoCompleteTextViewManufacturer.setAdapter(manufacturerAdapter);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, e.toString());
            }
        });
    }
}