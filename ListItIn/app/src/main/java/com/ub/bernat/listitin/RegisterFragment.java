package com.ub.bernat.listitin;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ub.bernat.listitin.model.DrawerLocker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {
    private Button registerButton;
    private Button cancelButton;

    private EditText textEmail;
    private EditText textUsername;
    private EditText textPassword1;
    private EditText textPassword2;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        registerButton = view.findViewById(R.id.registerButton);
        cancelButton = view.findViewById(R.id.cancelRegistrationButton);
        textEmail = view.findViewById(R.id.textEmailRegister);
        textUsername = view.findViewById(R.id.textUsernameRegister);
        textPassword1 =  view.findViewById(R.id.textPasswordRegister1);
        textPassword2 = view.findViewById(R.id.textPasswordRegister2);
        progressDialog = new ProgressDialog(getActivity());
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().setTitle("Login");
                LoginFragment loginFragment = new LoginFragment();
                FragmentManager manager = getActivity().getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.mainLayout,loginFragment).commit();
            }
        });
        ((DrawerLocker) getActivity()).setDrawerEnabled(false);
        return view;
    }

    private void registerUser() {
        final String email = textEmail.getText().toString().trim();
        final String username = textUsername.getText().toString().trim();
        final String password1=textPassword1.getText().toString();
        String password2=textPassword2.getText().toString();
        if(!TextUtils.isEmpty(email)&&!TextUtils.isEmpty(password1)&&!TextUtils.isEmpty(password2)&&!TextUtils.isEmpty(username)){
            if(TextUtils.equals(password1,password2)){
                progressDialog.setMessage("Registering...");
                progressDialog.show();
                firebaseAuth.createUserWithEmailAndPassword(email,password1)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressDialog.hide();
                                if(task.isSuccessful()){
                                    Toast.makeText(getActivity(),"Successful register",Toast.LENGTH_LONG).show();
                                    firebaseAuth.signInWithEmailAndPassword(email,password1)
                                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    if(task.isSuccessful()){//TODO: ON LOGIN needs to delete login and only show logout.
                                                        progressDialog.dismiss();
                                                        Toast.makeText(getActivity(),"Successful login",Toast.LENGTH_LONG).show();
                                                        getActivity().setTitle("Discover");
                                                        DiscoverFragment discoverFragment = new DiscoverFragment();
                                                        FragmentManager manager = getActivity().getSupportFragmentManager();
                                                        manager.beginTransaction().replace(R.id.mainLayout,discoverFragment).commit();
                                                        String uID = task.getResult().getUser().getUid();
                                                        databaseReference.child(uID).child("username").setValue(username);
                                                    }
                                                }
                                            });
                                }else{
                                    Toast.makeText(getActivity(),"An error occurred",Toast.LENGTH_LONG).show(); //TODO: better explanation
                                }
                            }
                });

            }else{
                Toast.makeText(getActivity(),"Passwords don't match",Toast.LENGTH_LONG).show();
            }

        }else{
            Toast.makeText(getActivity(),"Please fill in all fields",Toast.LENGTH_LONG).show();
        }
    }

}
