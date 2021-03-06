package edu.uga.cs.roommateshopping;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * The class allows the user to register a account into
 * Google firebase. The user will provide an email for
 * the username and a password that is at least 6 digits long.
 */
public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText usernameField;
    private EditText passwordField;
    private Button registerButton;
    private Button changeToLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);

        usernameField = (EditText) findViewById(R.id.usernameField);
        passwordField = (EditText) findViewById(R.id.passwordField);
        registerButton = (Button) findViewById(R.id.registerButton);
        changeToLogin = (Button) findViewById(R.id.changeToLoginButton);

        mAuth = FirebaseAuth.getInstance();
        registerButton.setOnClickListener(new View.OnClickListener() { // creates a new user into the data base
            public void onClick(View v) {
                final String username = usernameField.getText().toString().trim(); // grabs the username that is a email
                final String password = passwordField.getText().toString().trim(); // grabs the passsword that is 6 characters long.
                if(username.matches("") || password.matches("")) { // error message for registering
                    Toast toast = Toast.makeText(getApplicationContext(), "Please enter a value",  Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    registerUser(username, password);
                }
            }
        });

        changeToLogin.setOnClickListener(new View.OnClickListener() {  // after registration login
            public void onClick(View v) {
                Intent intent = new Intent( RegisterActivity.this, LoginActivity.class );
                startActivity( intent );
            }
        });
    }


    private void registerUser(String user, String pass) { // this will present an error message.
        mAuth.createUserWithEmailAndPassword(user, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) { // making sure the credentials are good if not provide an error message
                            // Sign in success, update UI with the signed-in user's information
                            Toast toast = Toast.makeText(getApplicationContext(), "created",  Toast.LENGTH_SHORT);
                            toast.show();
                            FirebaseUser user = mAuth.getCurrentUser();

                            Intent intent = new Intent( RegisterActivity.this, MainActivity.class );
                            startActivity( intent );

                        } else { // bad message
                            String e = "fail: " + task.getException();
                            Toast.makeText(getApplicationContext(), "Failed Registration: "+e, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}