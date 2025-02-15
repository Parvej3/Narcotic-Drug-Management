package com.project.drugmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.project.drugmanagement.HomeScreen.Depo.DepoHome;
import com.project.drugmanagement.HomeScreen.Doctor.DoctorHome;
import com.project.drugmanagement.HomeScreen.DrugOfficer.DrugOfficerHome;
import com.project.drugmanagement.HomeScreen.Manufacturer.ManufacturerHome;
import com.project.drugmanagement.HomeScreen.Retailer.RetailerHome;
import com.project.drugmanagement.HomeScreen.Wholesaler.WholesalerHome;
import com.project.drugmanagement.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth authProfile;
    ActivityLoginBinding loginBinding ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(loginBinding.getRoot());

        authProfile = FirebaseAuth.getInstance();



        loginBinding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textEmail = loginBinding.editTextEmail.getText().toString();
                String textPwd = loginBinding.editTextPassword.getText().toString();

                if (TextUtils.isEmpty(textEmail)){
                    Toast.makeText(LoginActivity.this, "Please enter email address ..", Toast.LENGTH_LONG).show();
                    loginBinding.editTextEmail.setError("Email address required");
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    Toast.makeText(LoginActivity.this, "Please re-enter valid email address ..", Toast.LENGTH_LONG).show();
                    loginBinding.editTextEmail.setError("valid email address required");
                } else if (TextUtils.isEmpty(textPwd)) {
                    Toast.makeText(LoginActivity.this, "Please enter password..", Toast.LENGTH_LONG).show();
                    loginBinding.editTextPassword.setError("password required");
                } else {
                    loginBinding.progressBar.setVisibility(View.VISIBLE);
                    loginUser(textEmail,textPwd);
                }
            }
        });

    }


    private void loginUser(String textEmail, String textPwd) {
        authProfile.signInWithEmailAndPassword(textEmail,textPwd).addOnCompleteListener(LoginActivity.this ,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(LoginActivity.this,"You are logged in now",Toast.LENGTH_SHORT).show();

                    if (authProfile.getCurrentUser().getDisplayName().equals("Manufacturer")){
                        startActivity(new Intent(LoginActivity.this, ManufacturerHome.class));
                        finish();
                    } else if (authProfile.getCurrentUser().getDisplayName().equals("Depo")) {
                        startActivity(new Intent(LoginActivity.this, DepoHome.class));
                        finish();
                    }else if (authProfile.getCurrentUser().getDisplayName().equals("Wholesaler")) {
                        startActivity(new Intent(LoginActivity.this, WholesalerHome.class));
                        finish();
                    }else if (authProfile.getCurrentUser().getDisplayName().equals("Retailer")) {
                        startActivity(new Intent(LoginActivity.this, RetailerHome.class));
                        finish();
                    } else if (authProfile.getCurrentUser().getDisplayName().equals("Doctor")) {
                        startActivity(new Intent(LoginActivity.this, DoctorHome.class));
                        finish();
                    } else {
                        startActivity(new Intent(LoginActivity.this, DrugOfficerHome.class));
                        finish();
                    }
                    loginBinding.progressBar.setVisibility(View.GONE);
                }
                else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e){
                        Toast.makeText(LoginActivity.this,"user does not exist or is no longer valid. Please register again",Toast.LENGTH_SHORT).show();
                    } catch (FirebaseAuthInvalidCredentialsException e){
                        Toast.makeText(LoginActivity.this,"Invalid credentials. kindly , check and re-enter ",Toast.LENGTH_SHORT).show();
                    } catch (Exception e){
                        Toast.makeText(LoginActivity.this,"Something went wrong "+e,Toast.LENGTH_SHORT).show();
                    }
                    loginBinding.progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    // check user is already logged in.In such case opens UserProfile activity
    @Override
    protected void onStart() {
        super.onStart();
//        if(authProfile.getCurrentUser() != null) {
//            Toast.makeText(LoginActivity.this,"You are already logged in"+authProfile.getCurrentUser().getEmail(),Toast.LENGTH_SHORT).show();
//
//            // open user profile activity
////            Intent intent = new Intent(LoginActivity.this ,AddUserDetails.class);
////            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
////            startActivity(intent);
//            overridePendingTransition(0,0);
//            finish();
//        }
    }
}