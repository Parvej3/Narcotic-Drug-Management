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
import com.project.drugmanagement.Models.ReadWriteProductsArray;
import com.project.drugmanagement.Models.ReadWriteTransactionDepoDetails;
import com.project.drugmanagement.Models.ReadWriteTransactionDetails;
import com.project.drugmanagement.R;
import com.project.drugmanagement.databinding.FragmentDoDepoPurchaseBinding;
import com.project.drugmanagement.databinding.FragmentDoDepoSellBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_do_depo_sell#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_do_depo_sell extends Fragment {
    private static final String TAG = "do_depo_sell";
    private static String depoName, prodName, month, year;
    //private static ArrayAdapter<String> packingAdapter;
    private FragmentDoDepoSellBinding sellBinding;
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

    public fragment_do_depo_sell() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_do_depo_sell.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_do_depo_sell newInstance(String param1, String param2) {
        fragment_do_depo_sell fragment = new fragment_do_depo_sell();
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
        //return inflater.inflate(R.layout.fragment_do_depo_sell, container, false);
        sellBinding = FragmentDoDepoSellBinding.inflate(inflater, container, false);
        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        return sellBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // load DepoNames
        loadDepo();

        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(getActivity(), R.layout.drop_down_item, getResources().getStringArray(R.array.month));
        sellBinding.autoCompleteTextViewMonth.setAdapter(monthAdapter);
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(getActivity(), R.layout.drop_down_item, getResources().getStringArray(R.array.year));
        sellBinding.autoCompleteTextViewYear.setAdapter(yearAdapter);

        //getSelected depo name from autoCompleteTextView
        sellBinding.autoCompleteTextViewDepo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                depoName = adapterView.getItemAtPosition(i).toString();
                // load productNames into autoCompleteTextView
                loadProductNames(depoName);
            }
        });
        //getSelected product name from autoCompleteTextView
        sellBinding.autoCompleteTextViewProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                prodName = adapterView.getItemAtPosition(i).toString();
            }
        });
        //getSelected product name from autoCompleteTextView
        sellBinding.autoCompleteTextViewMonth.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                month = adapterView.getItemAtPosition(i).toString();
            }
        });
        //getSelected product name from autoCompleteTextView
        sellBinding.autoCompleteTextViewYear.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                year = adapterView.getItemAtPosition(i).toString();
            }
        });

        sellBinding.btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(depoName)) {
                    Toast.makeText(getActivity(), "Please select depo Name ..", Toast.LENGTH_SHORT).show();
                    sellBinding.autoCompleteTextViewDepo.setError("manufacturerName required");
                } else if (TextUtils.isEmpty(prodName)) {
                    Toast.makeText(getActivity(), "Please select product.", Toast.LENGTH_SHORT).show();
                    sellBinding.autoCompleteTextViewProduct.setError("productName required");
                } else if (TextUtils.isEmpty(month)) {
                    Toast.makeText(getActivity(), "Please select month", Toast.LENGTH_SHORT).show();
                    sellBinding.autoCompleteTextViewMonth.setError("month required");
                } else if (TextUtils.isEmpty(year)) {
                    Toast.makeText(getActivity(), "Please select year", Toast.LENGTH_SHORT).show();
                    sellBinding.autoCompleteTextViewMonth.setError("year required");
                } else {
                    getDetails();
                }
            }
        });
    }

    private void getDetails() {

        String date = month + year;
        CollectionReference productionReference = db.collection("Transaction");

        productionReference.document("Depo").collection(depoName)
                .document("outward").collection(prodName)
                .whereEqualTo("invoiceDate", date)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        String data = "";
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            if (documentSnapshot.exists()) {
                                ReadWriteTransactionDepoDetails readTransactionDetails = documentSnapshot.toObject(ReadWriteTransactionDepoDetails.class);
                                data += "Invoice No :" + readTransactionDetails.getInvoiceNo() + "\n"
                                        +"Wholesaler Name :" + readTransactionDetails.getWholesalerName() +"\n"
                                        + "Batch No :" + readTransactionDetails.getBatch() + "\n"
                                        + "Quantity :" + readTransactionDetails.getQuantity() + "\n"
                                        + "Pack :" + readTransactionDetails.getPack() + "\n"
                                        + "Total :" + readTransactionDetails.getTotal();
                                data += " \n\n    ________________ ";
                            }
                            else {
                                data += "no details found";
                            }

                        }
                        sellBinding.textViewDetails.setText(data);
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
    private void loadProductNames(String manufacName) {
        CollectionReference collectionReference = db.collection("Transaction");
        collectionReference.document("Depo").collection(depoName)
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
                                sellBinding.autoCompleteTextViewProduct.setAdapter(prodAdapter);
                            } else {
                                sellBinding.autoCompleteTextViewProduct.setError("no transaction yet");
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
}