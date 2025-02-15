package com.project.drugmanagement.HomeScreen.Retailer;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.drugmanagement.R;
import com.project.drugmanagement.databinding.FragmentRetailerHomeBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_retailer_home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_retailer_home extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FragmentRetailerHomeBinding retailerHomeBinding;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragment_retailer_home() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_retailer_home.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_retailer_home newInstance(String param1, String param2) {
        fragment_retailer_home fragment = new fragment_retailer_home();
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
        //return inflater.inflate(R.layout.fragment_retailer_home, container, false);
        retailerHomeBinding = FragmentRetailerHomeBinding.inflate(inflater,container,false);

        return retailerHomeBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        retailerHomeBinding.btnAddUserDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_fragment_retailer_home3_to_addUserDetails4);
            }
        });
        retailerHomeBinding.btnFindPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_fragment_retailer_home3_to_fragment_get_patientInfo);
            }
        });
        retailerHomeBinding.btnRetailerSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_fragment_retailer_home3_to_fragment_retailer_sell2);
            }
        });
    }
}