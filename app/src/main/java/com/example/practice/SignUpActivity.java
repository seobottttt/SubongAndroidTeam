package com.example.practice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {
    private DatabaseReference mDatabaseRef; // 실시간 데이터베이스
    private EditText mEtID, mEtPwd, mEtRePwd, mEtName, mEtPhone; // 회원가입 입력필드
    private boolean conditionID, conditionPWD, conditionRePWD, conditionName, conditionPhone;
    private String checkPwd = "";
    public com.example.practice.UserAccount account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        account = new com.example.practice.UserAccount();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Dlife");
        Button btn_next_to_sign_up; // 회원가입 버튼
        Button btn_back_to_login; // 뒤로가기 버튼(상단 액션바)
        // 입력필드 edittext
        mEtID = findViewById(R.id.text_id);
        mEtPwd = findViewById(R.id.text_pw);
        mEtRePwd = findViewById(R.id.text_pw_re);
        mEtName = findViewById(R.id.text_nickname);
        mEtPhone = findViewById(R.id.text_pnum);
        // 액션바 뒤로가기 버튼, 회원가입 버튼
        btn_back_to_login = findViewById(R.id.btn_back_to_login);
        btn_next_to_sign_up = findViewById(R.id.btn_next_to_sign_up);

        btn_next_to_sign_up.setEnabled(false); // 조건 충족 전 버튼클릭 X

        btn_back_to_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        mEtID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() >= 8){ // ID가 8글자 이상이도록 조건 설정
                    conditionID = true;
                } else {
                    conditionID = false;
                }
                if(conditionRegister()) {
                    btn_next_to_sign_up.setEnabled(true);
                } else{
                    btn_next_to_sign_up.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mEtPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence != null){ // pwd와 rePwd 비교하기 위한 변수 입력
                    checkPwd = charSequence.toString();
                }
                if(charSequence.length() >= 8){ // pwd 조건 추후 추가 예정
                    conditionPWD = true;
                } else {
                    conditionPWD = false;
                }
                if(conditionRegister()) {
                    btn_next_to_sign_up.setEnabled(true);
                } else{
                    btn_next_to_sign_up.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mEtRePwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String rePwd = ""; // string 객체 이용하기 위한 변수선언
                if(charSequence != null) { // nullpointer 에러 방지
                    rePwd = charSequence.toString();
                } else {
                    rePwd = "";
                }

                if(rePwd.equals(checkPwd)){
                    conditionRePWD = true;
                } else {
                    conditionRePWD = false;
                }

                if(conditionRegister()) {
                    btn_next_to_sign_up.setEnabled(true);
                } else{
                    btn_next_to_sign_up.setEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mEtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() >= 3){
                    conditionName = true;
                } else {
                    conditionName = false;
                }

                if(conditionRegister()) {
                    btn_next_to_sign_up.setEnabled(true);
                } else{
                    btn_next_to_sign_up.setEnabled(false);
                }


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mEtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(charSequence != null) {
                    String phone = charSequence.toString();
                    if (true) { // phoneNumberREG -> 010으로 시작하는지 확인
                        if (charSequence.length()==11) { // 전화번호 형식 010XXXXXXXX -> 11자리
                            conditionPhone = true;
                        } else {
                            conditionPhone = false;
                        }
                    }
                }
                if(conditionRegister()) {
                    btn_next_to_sign_up.setEnabled(true);
                } else{
                    btn_next_to_sign_up.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        btn_next_to_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // 클릭
                // 회원가입 처리 시작
                String strID = mEtID.getText().toString();
                String strPwd = mEtPwd.getText().toString();
                String strName = mEtName.getText().toString();
                String strPhone = mEtPhone.getText().toString();


                // SignUpCarNumActivity에 가입정보 put 후 화면 전환

                Log.d("bug", ""+strID+strPwd+strName+strPhone);
                Intent intent = new Intent(SignUpActivity.this, SignUpCarNumActivity.class);
                intent.putExtra("ID", strID);
                intent.putExtra("Pwd", strPwd);
                intent.putExtra("Nickname", strName);
                intent.putExtra("Phone", strPhone);
                startActivity(intent);

            }
        });
    }

    void showDialog() {
        AlertDialog.Builder msgBuilder = new AlertDialog.Builder(this)
                .setTitle("회원가입을 취소하시겠습니까?")
                .setMessage("입력한 정보는 모두 지워집니다.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(SignUpActivity.this,MainActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        AlertDialog msgDlg = msgBuilder.create();
        msgDlg.show();
    } // 뒤로가기 버튼 누를 시 호출되는 다이얼로그

    boolean conditionRegister(){
        Log.d("bug", ""+conditionID+conditionPWD+conditionRePWD+conditionName+conditionPhone);
        return (conditionID && conditionPWD && conditionRePWD && conditionName && conditionPhone);
    } // 각 입력부분 조건 모두 맞을 시 true 반환




}