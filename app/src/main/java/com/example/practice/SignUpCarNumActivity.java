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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpCarNumActivity extends AppCompatActivity {
    private DatabaseReference mDatabaseRef; // 실시간 데이터베이스 -> 번호판 DB에 저장하기 위해
    private FirebaseAuth mFirebaseAuth; // 파이어베이스 인증처리
    private String strID, strPwd, strNickName, strPhone;
    private com.example.practice.UserAccount account;
    private Intent intentPrev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_carnumber);
        intentPrev = getIntent(); // SignUpActivity에서 put된 자료들 처리
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Dlife");
        mFirebaseAuth = FirebaseAuth.getInstance();
        account = new com.example.practice.UserAccount(); // UserAccount 객체 생성

        setInfo(); // SignUpActivity에서 넘어온 정보를 처리하는 메서드

        Button btn_back_to_login = findViewById(R.id.btn_back_to_login);
        btn_back_to_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        EditText et_input_num = findViewById(R.id.et_input_car_number);
        Button btn_go_to_next = findViewById(R.id.btn_finish_signup);

        btn_go_to_next.setEnabled(false); // 번호판 제대로 입력 전 비활성화

        et_input_num.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String eCarNum = charSequence.toString();
                if(isValidCarNumber(eCarNum)){
                    btn_go_to_next.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        btn_go_to_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // UserAccount 클래스에서 ID,PW 호출하여 파이어베이스 Auth에 사용

                String strCarNum = et_input_num.getText().toString();
                // Firebase Auth 진행
                mFirebaseAuth.createUserWithEmailAndPassword(strID,strPwd).addOnCompleteListener(SignUpCarNumActivity.this,
                        new OnCompleteListener<AuthResult>() {

                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                                    account.setIdToken(firebaseUser.getUid());
                                    account.setCarNum(strCarNum);
                                    // setValue : database에 insert하는 행위
                                    mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).setValue(account);
                                    Toast.makeText(SignUpCarNumActivity.this, "회원가입 성공!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(SignUpCarNumActivity.this, MainActivity.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(SignUpCarNumActivity.this, "오류 발생!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
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
                        Intent intent = new Intent(SignUpCarNumActivity.this,MainActivity.class);
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
    } // 뒤로가기 버튼 누를 시 다이얼로그 호출
    /**
     * 차량 번호 유효 여부 판단
     * 1번째 패턴 12조1234 =>숫자2,한글1,숫자4
     * 2번째 패턴 123가1234 -> 숫자3, 한글1, 숫자4
     * @param carNum
     * @return
     */
    public static boolean isValidCarNumber(String carNum){

        boolean returnValue = false;

        try{
            String regex = "^\\d{2}[가|나|다|라|마|거|너|더|러|머|버|서|어|저|고|노|도|로|모|보|소|오|조|구|누|두|루|무|부|수|우|주|바|사|아|자|허|배|호|하\\x20]\\d{4}/*$";

            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(carNum);
            if (m.matches()) {
                returnValue = true;
            }else{
                //2번째 패턴 처리

                regex = "^\\d{3}[가|나|다|라|마|거|너|더|러|머|버|서|어|저|고|노|도|로|모|보|소|오|조|구|누|두|루|무|부|수|우|주|바|사|아|자|허|배|호|하\\x20]\\d{4}/*$";
                p = Pattern.compile(regex);
                m = p.matcher(carNum);
                if (m.matches()) {
                    returnValue = true;
                }
            }

            return returnValue;

        }catch(Exception e){
            return false;
        }
    } // Google 참고 정규표현식
    public void setInfo(){
        strID = intentPrev.getStringExtra("ID");
        strPwd = intentPrev.getStringExtra("Pwd");
        strNickName = intentPrev.getStringExtra("Nickname");
        strPhone = intentPrev.getStringExtra("Phone");
        account.setID(strID);
        account.setNickname(strNickName);
        account.setPhone(strPhone);
    }



}