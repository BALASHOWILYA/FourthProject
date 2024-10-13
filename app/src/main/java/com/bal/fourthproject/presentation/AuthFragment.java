package com.bal.fourthproject.presentation;

import static android.app.Activity.RESULT_OK;
import static android.widget.Toast.makeText;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bal.fourthproject.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;


public class AuthFragment extends Fragment {

    private ShapeableImageView imageView;
    private TextView name, mail;
    FirebaseAuth auth;


    private final ActivityResultLauncher<Intent> activityResultLauncher=
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK){
                        Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        try {
                            GoogleSignInAccount signInAccount = accountTask.getResult(ApiException.class);

                            AuthCredential authCredential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(), null);

                            auth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        auth = FirebaseAuth.getInstance();

                                        Glide.with(requireContext()).load(Objects.requireNonNull(auth.getCurrentUser()).getPhotoUrl()).into(imageView);
                                        name.setText(auth.getCurrentUser().getDisplayName());
                                        mail.setText(auth.getCurrentUser().getEmail());

                                        makeText(requireContext(), "Signed in successfully!", Toast.LENGTH_LONG).show();
                                    }
                                    else{
                                        makeText(requireContext(), "Signed in failed!", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                        } catch (ApiException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_auth, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imageView = view.findViewById(R.id.imageView);
        name = view.findViewById(R.id.nameTV);
        mail = view.findViewById(R.id.emailTV);


        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();


        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(requireContext(), options);
        auth = FirebaseAuth.getInstance();

        SignInButton signInButton = view.findViewById(R.id.SignIn);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = googleSignInClient.getSignInIntent();
                activityResultLauncher.launch(intent);
            }
        });

        MaterialButton signOut = view.findViewById(R.id.SignOut);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
                    @Override
                    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                        if (firebaseAuth.getCurrentUser() == null) {
                            googleSignInClient.signOut().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    makeText(requireContext(), "Signed out successfully", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(requireContext(), MainActivity.class));
                                }
                            });
                        }
                    }
                });

                FirebaseAuth.getInstance().signOut();
            }
        });

        if (auth.getCurrentUser() != null) {
            Glide.with(requireContext()).load(Objects.requireNonNull(auth.getCurrentUser().getDisplayName())).into(imageView);
            name.setText(auth.getCurrentUser().getDisplayName());
            mail.setText(auth.getCurrentUser().getEmail());
        }


    }
}