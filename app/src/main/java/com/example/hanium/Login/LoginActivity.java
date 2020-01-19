package com.example.hanium.Login;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hanium.HomeActivity;
import com.example.hanium.R;
import com.example.hanium.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {
    FirebaseFirestore db= FirebaseFirestore.getInstance();
    private static final String TAG = "LoginActivity";

    TextView idpwFindBtn;
    TextView signUpBtn;

    Button loginBtn;

    EditText idEt;
    EditText pwEt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_interface);
        idEt = (EditText) findViewById(R.id.login_id_Et);
        pwEt = (EditText) findViewById(R.id.login_pw_Et);

        loginBtn = (Button) findViewById(R.id.login_login_btn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(idEt.getText().toString().length()*pwEt.getText().toString().length()<=0){
                    Toast.makeText(LoginActivity.this, "로그인 실패 : 모든 정보를 기입하세요!!!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG,"no write all info");
                }
                else {
                    db.collection("User").document(idEt.getText().toString())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            if (document.get("PW").toString().equals(pwEt.getText().toString())) {
                                                Intent home_intent = new Intent(LoginActivity.this, HomeActivity.class);
                                                User user = new User(idEt.getText().toString());
                                                user.updateMark();
                                                home_intent.putExtra("user",(Parcelable)user);
                                                startActivity(home_intent);
                                                Log.d(TAG, "success Login");
                                            } else {
                                                Toast.makeText(LoginActivity.this, "로그인 실패 : 비밀번호를 확인해주세요!!!", Toast.LENGTH_SHORT).show();
                                                Log.d(TAG, "no match pw " + document.get("PW").toString() + "   " + pwEt.getText().toString());
                                            }
                                        } else {
                                            Log.d(TAG, "Error getting documents: ", task.getException());
                                            Toast.makeText(LoginActivity.this, "로그인 실패 : 아이디를 확인해주세요!!!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });

                }
            }
        });

        signUpBtn = (TextView) findViewById(R.id.login_signUp_Btn);
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent SignUpActivtiy_intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(SignUpActivtiy_intent);
            }
        });

        idpwFindBtn = (TextView) findViewById(R.id.login_idpwfind_Btn);
        idpwFindBtn.setOnClickListener(new View.OnClickListener() {
            private AlertDialog alertDialog = null;
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder findIdPwDialog = new AlertDialog.Builder(LoginActivity.this);
                findIdPwDialog.setTitle("ID/PW 찾기");
                final View idpwfindView=getLayoutInflater().inflate(R.layout.activity_alert_dialog_custom,null);
                findIdPwDialog.setView(idpwfindView);
                Button okBt=(Button)idpwfindView.findViewById(R.id.okBt);
                Button nokBt=(Button)idpwfindView.findViewById(R.id.nokBt);
                final EditText email_Et=(EditText)idpwfindView.findViewById(R.id.findEmail);
                final EditText phnum_Et=(EditText)idpwfindView.findViewById(R.id.findPhone);
                final EditText name_Et=(EditText)idpwfindView.findViewById(R.id.findName);

                okBt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(email_Et.getText().toString().length()*name_Et.getText().toString().length()*phnum_Et.getText().toString().length()<=0){
                            Toast.makeText(LoginActivity.this, "ID/PW 찾기 실패 : 모든 정보를 기입하세요!!!", Toast.LENGTH_SHORT).show();
                            Log.d(TAG,"no write all info");
                        }
                        else {
                            db.collection("User")
                                    .whereEqualTo("Email",email_Et.getText().toString())
                                    .whereEqualTo("NAME",name_Et.getText().toString())
                                    .whereEqualTo("Phone",phnum_Et.getText().toString())
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for(QueryDocumentSnapshot document:task.getResult()) {
                                                    alertDialog.dismiss();
                                                    Log.d(TAG, "success find IDPW");
                                                    new AlertDialog.Builder(LoginActivity.this)
                                                            .setNegativeButton("확인", new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                                    dialogInterface.dismiss();
                                                                }
                                                            })
                                                            .setTitle("ID / PW")
                                                            .setMessage("ID : " + document.getId() + "\nPW : " + document.get("PW"))
                                                            .show();

                                                }

                                            }else {
                                                Log.d(TAG, "Error finding IDPW ");
                                                Toast.makeText(LoginActivity.this, "정보가 맞지 않습니다.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }
                });
                nokBt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });
                findIdPwDialog.setCancelable(true);
                alertDialog=findIdPwDialog.create();
                alertDialog.show();

            }
        });


    }


}