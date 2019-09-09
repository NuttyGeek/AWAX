package com.example.bhati.routeapplication.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.bhati.routeapplication.Pojo.Users;
import com.example.bhati.routeapplication.R;

public class SignupActivity extends AppCompatActivity {

    private Button btnLogin;
    private Button btnSignUp;
    private EditText edTxtUserName;
    private EditText edTxtEmail;
    private EditText edTxtPassword;
    private EditText edTxtConfirm;
    private LinearLayout ll;

    String email , username , password , confirm_pasword;

    //Fire base Variables
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_signup);
//        auth = FirebaseAuth.getInstance();
//        mDatabase = FirebaseDatabase.getInstance();
//        mReference = mDatabase.getReference("users");
        btnLogin = findViewById(R.id.btnLogin);
        edTxtUserName = findViewById(R.id.edTxtUserName);
        edTxtPassword = findViewById(R.id.edTxtPassword);
        edTxtEmail = findViewById(R.id.edTxtEmail);
        edTxtConfirm = findViewById(R.id.edTxtConfirmPassword);
        btnSignUp = findViewById(R.id.btnSignup);
        ll = findViewById(R.id.ll);
        btnSignUp.setOnClickListener(v -> {
            username = edTxtUserName.getText().toString();
            email = edTxtEmail.getText().toString();
            password = edTxtPassword.getText().toString();
            confirm_pasword = edTxtConfirm.getText().toString();
            if (TextUtils.isEmpty(username) && TextUtils.isEmpty(email) && TextUtils.isEmpty(password) && TextUtils.isEmpty(confirm_pasword))
            {
                Snackbar.make(ll,"Fill all the fields to proceed",Snackbar.LENGTH_SHORT).show();
                return;
            }
            final ProgressDialog mProgressDialog = new ProgressDialog(SignupActivity.this);
            mProgressDialog.setMessage("Please Wait........");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
//            auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
//                if (task.isSuccessful())
//                {
//                    String UID = auth.getUid();
//                    Users users = new Users(email , username);
//                    mReference.child(UID).setValue(users).addOnCompleteListener(task1 -> {
//                        if (task1.isSuccessful())
//                        {
//                            mProgressDialog.dismiss();
//                            Snackbar.make(ll,"Signup successfully" , Snackbar.LENGTH_SHORT).show();
//                            startActivity(new Intent(SignupActivity.this , LoginActivity.class));
//                            finish();
//                        }
//                        else
//                        {
//                            mProgressDialog.dismiss();
//                            Snackbar.make(ll,"Something went wrong, please try again later.", Snackbar.LENGTH_SHORT).show();
//                        }
//                    });
//                }
//                else
//                {
//                    mProgressDialog.dismiss();
//                    Toast.makeText(this, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            });

        });
        btnLogin.setOnClickListener(v -> {
            startActivity(new Intent(SignupActivity.this , LoginActivity.class));
            finish();
        });
    }
}
