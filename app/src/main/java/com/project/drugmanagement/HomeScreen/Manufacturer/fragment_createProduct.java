package com.project.drugmanagement.HomeScreen.Manufacturer;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.drugmanagement.R;
import com.project.drugmanagement.databinding.FragmentCreateProductBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_createProduct#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_createProduct extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
     private final String SELECTED_ROLE = "Manufacturer";
    private FragmentCreateProductBinding createProductBinding;
    private FirebaseFirestore db ;
    FirebaseAuth authProfile;
    FirebaseUser firebaseUser;

    public fragment_createProduct() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_createProduct.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_createProduct newInstance(String param1, String param2) {
        fragment_createProduct fragment = new fragment_createProduct();
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
        //return inflater.inflate(R.layout.fragment_create_product, container, false);
        createProductBinding = FragmentCreateProductBinding.inflate(inflater,container,false);
        db = FirebaseFirestore.getInstance();
        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();
        return createProductBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        createProductBinding.btnCreateProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String prodName = createProductBinding.editTextProductName.getText().toString();
                if (TextUtils.isEmpty(prodName)){
                    Toast.makeText(getActivity(),"Please Enter Product Name",Toast.LENGTH_SHORT).show();
                    createProductBinding.editTextProductName.setError("Required Product Name");
                } else {
                    CollectionReference collectionReference = db.collection(SELECTED_ROLE);
                    collectionReference
                            .whereEqualTo("email",firebaseUser.getEmail())
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                        String documentId = documentSnapshot.getId();
                                        collectionReference.document(documentId).update("products", FieldValue.arrayUnion(prodName));
                                        Toast.makeText(getActivity(), "Product Created Successfully", Toast.LENGTH_SHORT).show();
                                        createProductBinding.editTextProductName.setText("");
                                    }
                                }
                            });
                }

            }
        });
    }
}