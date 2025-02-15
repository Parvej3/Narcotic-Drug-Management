package com.project.drugmanagement.HomeScreen.DrugOfficer;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
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
import com.project.drugmanagement.Models.ReadWriteManufacturerDetails;
import com.project.drugmanagement.Models.ReadWriteMonthArray;
import com.project.drugmanagement.Models.ReadWritePatientDetails;
import com.project.drugmanagement.Models.ReadWriteProductionDetails;
import com.project.drugmanagement.R;
import com.project.drugmanagement.databinding.FragmentDoManfacturerProductionBinding;
import com.project.drugmanagement.databinding.FragmentFragmnetDepoSellBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_do_manfacturer_production#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_do_manfacturer_production extends Fragment {
    private static final String TAG = "do_manufacturer_production";
    private static String manufacturerName, prodName ,month;
    //private static ArrayAdapter<String> packingAdapter;
    private FragmentDoManfacturerProductionBinding productionBinding;
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

    public fragment_do_manfacturer_production() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_do_manfacturer_production.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_do_manfacturer_production newInstance(String param1, String param2) {
        fragment_do_manfacturer_production fragment = new fragment_do_manfacturer_production();
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
        productionBinding = FragmentDoManfacturerProductionBinding.inflate(inflater, container, false);
        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        return productionBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //load manufacturer name into autoCompleteTextView
        loadManufacturerNames();



        //getSelected manufacturer name from autoCompleteTextView
        productionBinding.autoCompleteTextViewManufacturer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                manufacturerName = adapterView.getItemAtPosition(i).toString();
                // load productNames into autoCompleteTextView
                loadProductNames(manufacturerName);
            }
        });

        productionBinding.autoCompleteTextViewProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                prodName = adapterView.getItemAtPosition(i).toString();
                //load months
                loadMonths();
            }
        });

        productionBinding.autoCompleteTextViewMonth.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                month = adapterView.getItemAtPosition(i).toString();
            }
        });

        productionBinding.btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (TextUtils.isEmpty(manufacturerName)) {
                    Toast.makeText(getActivity(),"Please select manufacturerName ..",Toast.LENGTH_SHORT).show();
                    productionBinding.autoCompleteTextViewManufacturer.setError("manufacturerName required");
                } else if (TextUtils.isEmpty(prodName)) {
                    Toast.makeText(getActivity(),"Please select product.",Toast.LENGTH_SHORT).show();
                    productionBinding.autoCompleteTextViewProduct.setError("productName required");
                } else if (TextUtils.isEmpty(month)) {
                    Toast.makeText(getActivity(),"Please select month",Toast.LENGTH_SHORT).show();
                    productionBinding.autoCompleteTextViewMonth.setError("month required");
                } else {
                    getDetails();
                }
            }
        });

    }

    private void getDetails() {
        CollectionReference productionReference = db.collection("Production");

        productionReference.document(manufacturerName).collection(month)
                .whereEqualTo("name",prodName)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        String data ="";
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            if (documentSnapshot.exists()) {
                                ReadWriteProductionDetails readProductionDetails = documentSnapshot.toObject(ReadWriteProductionDetails.class);
                                data += "batchNo :"+readProductionDetails.getBatchNo()+"\n"
                                            +"Quantity :"+readProductionDetails.getQuantity()+"\n"
                                            +"Pack :"+readProductionDetails.getPack()+"\n"
                                            +"Total :"+readProductionDetails.getTotal();
                                data += "\n\n";
                            } else {
                                productionBinding.textViewDetails.setText("No details found");
                            }
                        }
                        productionBinding.textViewDetails.setText(data);
                        //productionBinding.textViewDetails.setMovementMethod(new ScrollingMovementMethod());

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                            Log.d(TAG,e.toString());
                        Toast.makeText(getActivity(), "Something went wrong ..", Toast.LENGTH_SHORT).show();
                    }
                });


    }

    private void loadMonths() {

        CollectionReference collectionReference = db.collection("Production");
        collectionReference
                .document(manufacturerName)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()) {
                            ReadWriteMonthArray readMonthArray = documentSnapshot.toObject(ReadWriteMonthArray.class);
                            ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(getActivity(), R.layout.drop_down_item, readMonthArray.getMonths());
                            productionBinding.autoCompleteTextViewMonth.setAdapter(monthAdapter);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.toString());
                    }
                });
    }

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
                                productionBinding.autoCompleteTextViewProduct.setAdapter(prodAdapter);
                            } else {
                                productionBinding.autoCompleteTextViewProduct.setError("Products not created yet");
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
                productionBinding.autoCompleteTextViewManufacturer.setAdapter(manufacturerAdapter);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, e.toString());
            }
        });
    }

}