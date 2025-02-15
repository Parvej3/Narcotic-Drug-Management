package com.project.drugmanagement.HomeScreen.Retailer;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.project.drugmanagement.Models.ReadWritePatientDetails;
import com.project.drugmanagement.R;
import com.project.drugmanagement.databinding.FragmentGetPatientInfoBinding;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_get_patientInfo#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_get_patientInfo extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final String TAG = "patient_info";
    private FragmentGetPatientInfoBinding patientInfoBinding;
    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragment_get_patientInfo() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_get_patientInfo.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_get_patientInfo newInstance(String param1, String param2) {
        fragment_get_patientInfo fragment = new fragment_get_patientInfo();
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
        //return inflater.inflate(R.layout.fragment_get_patient_info, container, false);

        patientInfoBinding = FragmentGetPatientInfoBinding.inflate(inflater,container,false);
        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        return patientInfoBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        patientInfoBinding.btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String aadharNo = patientInfoBinding.editTextAadhar.getText().toString();
                patientInfoBinding.progressBar.setVisibility(View.VISIBLE);

                if (TextUtils.isEmpty(aadharNo)) {
                    Toast.makeText(getActivity(), "Please Enter Aadhar No.", Toast.LENGTH_SHORT).show();
                    patientInfoBinding.editTextAadhar.setError("Required");
                    patientInfoBinding.progressBar.setVisibility(View.GONE);
                }

                CollectionReference patientReference = db.collection("Patient");

//                Map<String,String> patientDetailsMap = new HashMap<>();
//                patientDetailsMap.put("doctor",doctorName);
//                patientDetailsMap.put("date",date);
//                patientDetailsMap.put("product",prodName);
//                patientDetailsMap.put("total", String.valueOf(total));
//                patientDetailsMap.put("Retailer",retailerName);


                patientReference.document(aadharNo).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            patientInfoBinding.progressBar.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), "Patient Details Found", Toast.LENGTH_SHORT).show();
                            ReadWritePatientDetails readPatientDetails = documentSnapshot.toObject(ReadWritePatientDetails.class);
                            String data ="";
                            for (int i = 0; i < readPatientDetails.getPrescriptions().size(); i++) {
                                data += readPatientDetails.getPrescriptions().get(i).toString();
                                data += "\n\n";
                            }
                            patientInfoBinding.txtviewPatientInfo.setText(data);

                        } else {
                            Toast.makeText(getActivity(), "Patient not registered", Toast.LENGTH_SHORT).show();
                            patientInfoBinding.progressBar.setVisibility(View.GONE);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,e.toString());
                    }
                });


            }
        });
    }
}