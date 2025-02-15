package com.project.drugmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.project.drugmanagement.databinding.ActivityRegistrationBinding;


public class RegistrationActivity extends AppCompatActivity {

    ActivityRegistrationBinding registerationBinding;
    String selectedRole;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerationBinding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(registerationBinding.getRoot());


        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.drop_down_item, getResources().getStringArray(R.array.roles));
        registerationBinding.autoCompleteTextViewRole.setAdapter(arrayAdapter);
        registerationBinding.autoCompleteTextViewRole.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedRole = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(RegistrationActivity.this,""+selectedRole,Toast.LENGTH_LONG).show();
            }
        });

        // validate entered value
        registerationBinding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textEmail = registerationBinding.editTextEmail.getText().toString();
                String textPwd = registerationBinding.editTextPassword.getText().toString();
                String textConfirmPwd = registerationBinding.editTextConfirmPassword.getText().toString();

                if (TextUtils.isEmpty(selectedRole)) {
                    Toast.makeText(RegistrationActivity.this, "First Select Your role !", Toast.LENGTH_SHORT).show();
                    registerationBinding.autoCompleteTextViewRole.setError("Role is Required");
                }else if (TextUtils.isEmpty(textEmail)) {
                    Toast.makeText(RegistrationActivity.this, "Please enter your Email !", Toast.LENGTH_SHORT).show();
                    registerationBinding.editTextEmail.setError("Email is required");
                    registerationBinding.editTextEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    Toast.makeText(RegistrationActivity.this, "Please re-enter your Email !", Toast.LENGTH_SHORT).show();
                    registerationBinding.editTextEmail.setError("Valid email is required");
                    registerationBinding.editTextEmail.requestFocus();
                } else if (TextUtils.isEmpty(textPwd)) {
                    Toast.makeText(RegistrationActivity.this, "Please enter your password !", Toast.LENGTH_SHORT).show();
                    registerationBinding.editTextPassword.setError("password is required");
                    registerationBinding.editTextPassword.requestFocus();
                } else if (textPwd.length() < 6) {
                    Toast.makeText(RegistrationActivity.this, "Password should be at least 6 digits!", Toast.LENGTH_SHORT).show();
                    registerationBinding.editTextPassword.setError("password too weak");
                    registerationBinding.editTextPassword.requestFocus();
                } else if (TextUtils.isEmpty(textConfirmPwd)) {
                    Toast.makeText(RegistrationActivity.this, "Please enter your password !", Toast.LENGTH_SHORT).show();
                    registerationBinding.editTextConfirmPassword.setError("password is required");
                    registerationBinding.editTextConfirmPassword.requestFocus();
                } else if (!textPwd.equals(textConfirmPwd)) {
                    Toast.makeText(RegistrationActivity.this, "Please save same password !", Toast.LENGTH_SHORT).show();
                    registerationBinding.editTextConfirmPassword.setError("password confirmation is required");
                    registerationBinding.editTextConfirmPassword.requestFocus();
                    // clear all editText fields
                    registerationBinding.editTextConfirmPassword.clearComposingText();
                    registerationBinding.editTextConfirmPassword.clearComposingText();
                }  else {
                    registerationBinding.progressBar.setVisibility(View.VISIBLE);
                    registerUser(textEmail, textPwd ,selectedRole);
                }
            }
        });
    }

    private void registerUser(String email, String pwd , String selectedRole) {
        
        FirebaseAuth authProfile = FirebaseAuth.getInstance();
        //create user with provided credentials
        authProfile.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //if user registration is successfull
                if (task.isSuccessful())
                {
                    Toast.makeText(RegistrationActivity.this, "User Registered Successfully...!", Toast.LENGTH_SHORT).show();
                    FirebaseUser firebaseUser = authProfile.getCurrentUser();
                    registerationBinding.progressBar.setVisibility(View.GONE);
                    //Enter UserRole data in authentication
                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                            .setDisplayName(selectedRole).build();
                    firebaseUser.updateProfile(profileChangeRequest);

                    Intent intent = new Intent(RegistrationActivity.this,MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    overridePendingTransition(0,0);
                    finish();

                }
                // if user registration is unsuccessfull
                else {
                    try {
                        throw task.getException();
                    }
                    catch (FirebaseAuthUserCollisionException e) {
                        Toast.makeText(RegistrationActivity.this,"User Already Exists.. , Use another Email",Toast.LENGTH_LONG).show();
                    }
                    catch (FirebaseAuthInvalidUserException e) {
                        Toast.makeText(RegistrationActivity.this,"the email is invalid kindly re-enter ",Toast.LENGTH_LONG).show();
                    }catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    registerationBinding.progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}