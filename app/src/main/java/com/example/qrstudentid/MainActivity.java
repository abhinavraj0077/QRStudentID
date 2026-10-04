package com.example.qrstudentid;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private LinearLayout root;

    private EditText nameInput;
    private EditText studentIdInput;
    private EditText courseInput;
    private EditText yearInput;
    private EditText phoneInput;
    private EditText emailInput;
    private EditText passwordInput;

    private Button registerButton;

    private static final int PURPLE = Color.rgb(190, 150, 255);
    private static final int DARK_BG = Color.rgb(18, 17, 24);
    private static final int CARD_BG = Color.rgb(35, 34, 44);
    private static final int WHITE = Color.WHITE;
    private static final int GRAY = Color.rgb(190, 188, 198);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        showRegistrationScreen();
    }

    private void showRegistrationScreen() {

        ScrollView scrollView = new ScrollView(this);
        scrollView.setBackgroundColor(DARK_BG);

        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(24), dp(30), dp(24), dp(30));

        scrollView.addView(root);

        // -----------------------------------------
        // APP TITLE
        // -----------------------------------------

        TextView title = new TextView(this);

        title.setText("QR STUDENT ID");
        title.setTextSize(30);
        title.setTextColor(PURPLE);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);

        root.addView(
                title,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );

        addSpace(root, 8);

        // -----------------------------------------
        // SUBTITLE
        // -----------------------------------------

        TextView subtitle = new TextView(this);

        subtitle.setText(
                "Digital Identity & Attendance System"
        );

        subtitle.setTextSize(16);
        subtitle.setTextColor(GRAY);
        subtitle.setGravity(Gravity.CENTER);

        root.addView(
                subtitle,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );

        addSpace(root, 30);

        // -----------------------------------------
        // REGISTRATION CARD
        // -----------------------------------------

        LinearLayout card = new LinearLayout(this);

        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(
                dp(20),
                dp(20),
                dp(20),
                dp(20)
        );

        card.setBackgroundColor(CARD_BG);

        root.addView(
                card,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );

        // -----------------------------------------
        // CARD TITLE
        // -----------------------------------------

        TextView registerTitle = new TextView(this);

        registerTitle.setText("Student Registration");
        registerTitle.setTextSize(24);
        registerTitle.setTextColor(WHITE);
        registerTitle.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        card.addView(registerTitle);

        addSpace(card, 20);

        // -----------------------------------------
        // STUDENT NAME
        // -----------------------------------------

        nameInput = createInput(
                "Student Name"
        );

        card.addView(nameInput);

        addSpace(card, 12);

        // -----------------------------------------
        // STUDENT ID
        // -----------------------------------------

        studentIdInput = createInput(
                "Student ID  e.g. STU001"
        );

        card.addView(studentIdInput);

        addSpace(card, 12);

        // -----------------------------------------
        // COURSE
        // -----------------------------------------

        courseInput = createInput(
                "Course  e.g. Information Technology"
        );

        card.addView(courseInput);

        addSpace(card, 12);

        // -----------------------------------------
        // YEAR
        // -----------------------------------------

        yearInput = createInput(
                "Year  e.g. 4"
        );

        card.addView(yearInput);

        addSpace(card, 12);

        // -----------------------------------------
        // PHONE
        // -----------------------------------------

        phoneInput = createInput(
                "Phone Number"
        );

        phoneInput.setInputType(
                InputType.TYPE_CLASS_PHONE
        );

        card.addView(phoneInput);

        addSpace(card, 12);

        // -----------------------------------------
        // EMAIL
        // -----------------------------------------

        emailInput = createInput(
                "Email Address"
        );

        emailInput.setInputType(
                InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        );

        card.addView(emailInput);

        addSpace(card, 12);

        // -----------------------------------------
        // PASSWORD
        // -----------------------------------------

        passwordInput = createInput(
                "Password"
        );

        passwordInput.setInputType(
                InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD
        );

        card.addView(passwordInput);

        addSpace(card, 24);

        // -----------------------------------------
        // REGISTER BUTTON
        // -----------------------------------------

        registerButton = new Button(this);

        registerButton.setText(
                "REGISTER STUDENT"
        );

        registerButton.setTextSize(16);
        registerButton.setTextColor(Color.rgb(45, 25, 65));
        registerButton.setBackgroundColor(PURPLE);
        registerButton.setAllCaps(false);

        card.addView(
                registerButton,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(55)
                )
        );

        registerButton.setOnClickListener(
                v -> registerStudent()
        );

        addSpace(root, 25);

        // -----------------------------------------
        // LOGIN TEXT
        // -----------------------------------------

        TextView loginText = new TextView(this);

        loginText.setText(
                "Already registered?  LOGIN"
        );

        loginText.setTextSize(17);
        loginText.setTextColor(PURPLE);
        loginText.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );
        loginText.setGravity(Gravity.CENTER);
        loginText.setPadding(
                0,
                dp(15),
                0,
                dp(15)
        );

        root.addView(
                loginText,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );

        loginText.setOnClickListener(
                v -> openLogin()
        );

        addSpace(root, 20);

        // -----------------------------------------
        // FOOTER
        // -----------------------------------------

        TextView footer = new TextView(this);

        footer.setText(
                "QR Student ID • Digital Identity System"
        );

        footer.setTextSize(13);
        footer.setTextColor(GRAY);
        footer.setGravity(Gravity.CENTER);

        root.addView(
                footer,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );

        setContentView(scrollView);
    }

    // =========================================================
    // CREATE INPUT
    // =========================================================

    private EditText createInput(String hint) {

        EditText input = new EditText(this);

        input.setHint(hint);
        input.setTextSize(16);
        input.setTextColor(WHITE);
        input.setHintTextColor(GRAY);

        input.setSingleLine(true);

        input.setPadding(
                dp(16),
                dp(10),
                dp(16),
                dp(10)
        );

        return input;
    }

    // =========================================================
    // REGISTER STUDENT
    // =========================================================

    private void registerStudent() {

        String name =
                nameInput.getText()
                        .toString()
                        .trim();

        String studentId =
                studentIdInput.getText()
                        .toString()
                        .trim();

        String course =
                courseInput.getText()
                        .toString()
                        .trim();

        String year =
                yearInput.getText()
                        .toString()
                        .trim();

        String phone =
                phoneInput.getText()
                        .toString()
                        .trim();

        String email =
                emailInput.getText()
                        .toString()
                        .trim();

        String password =
                passwordInput.getText()
                        .toString()
                        .trim();

        // -----------------------------------------
        // VALIDATION
        // -----------------------------------------

        if (name.isEmpty()) {
            nameInput.setError("Enter student name");
            nameInput.requestFocus();
            return;
        }

        if (studentId.isEmpty()) {
            studentIdInput.setError("Enter student ID");
            studentIdInput.requestFocus();
            return;
        }

        if (course.isEmpty()) {
            courseInput.setError("Enter course");
            courseInput.requestFocus();
            return;
        }

        if (year.isEmpty()) {
            yearInput.setError("Enter year");
            yearInput.requestFocus();
            return;
        }

        if (phone.isEmpty()) {
            phoneInput.setError("Enter phone number");
            phoneInput.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            emailInput.setError("Enter email");
            emailInput.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            passwordInput.setError("Enter password");
            passwordInput.requestFocus();
            return;
        }

        if (password.length() < 6) {
            passwordInput.setError(
                    "Password must contain at least 6 characters"
            );
            passwordInput.requestFocus();
            return;
        }

        registerButton.setEnabled(false);
        registerButton.setText("REGISTERING...");

        // -----------------------------------------
        // FIREBASE AUTHENTICATION
        // -----------------------------------------

        auth.createUserWithEmailAndPassword(
                        email,
                        password
                )
                .addOnCompleteListener(
                        this,
                        task -> {

                            if (task.isSuccessful()) {

                                if (auth.getCurrentUser() == null) {

                                    showRegistrationError(
                                            "User creation failed"
                                    );

                                    return;
                                }

                                String uid =
                                        auth.getCurrentUser()
                                                .getUid();

                                // -----------------------------------------
                                // STUDENT DATA
                                // -----------------------------------------

                                Map<String, Object> student =
                                        new HashMap<>();

                                student.put(
                                        "name",
                                        name
                                );

                                student.put(
                                        "studentId",
                                        studentId
                                );

                                student.put(
                                        "course",
                                        course
                                );

                                student.put(
                                        "year",
                                        year
                                );

                                student.put(
                                        "phone",
                                        phone
                                );

                                student.put(
                                        "email",
                                        email
                                );

                                // Alternate contact fields
                                student.put(
                                        "alternatePhone",
                                        ""
                                );

                                student.put(
                                        "alternateEmail",
                                        ""
                                );

                                // -----------------------------------------
                                // SAVE TO FIRESTORE
                                // -----------------------------------------

                                db.collection("students")
                                        .document(uid)
                                        .set(student)
                                        .addOnSuccessListener(
                                                unused -> {

                                                    Toast.makeText(
                                                            MainActivity.this,
                                                            "Registration successful",
                                                            Toast.LENGTH_LONG
                                                    ).show();

                                                    // Sign out so the
                                                    // student can login
                                                    // normally.
                                                    auth.signOut();

                                                    openLogin();
                                                }
                                        )
                                        .addOnFailureListener(
                                                e -> {

                                                    Toast.makeText(
                                                            MainActivity.this,
                                                            "Database error: "
                                                                    + e.getMessage(),
                                                            Toast.LENGTH_LONG
                                                    ).show();

                                                    registerButton
                                                            .setEnabled(true);

                                                    registerButton
                                                            .setText(
                                                                    "REGISTER STUDENT"
                                                            );
                                                }
                                        );

                            } else {

                                String errorMessage =
                                        "Registration failed";

                                if (task.getException() != null) {

                                    errorMessage =
                                            task.getException()
                                                    .getMessage();
                                }

                                Toast.makeText(
                                        MainActivity.this,
                                        errorMessage,
                                        Toast.LENGTH_LONG
                                ).show();

                                registerButton
                                        .setEnabled(true);

                                registerButton
                                        .setText(
                                                "REGISTER STUDENT"
                                        );
                            }
                        }
                );
    }

    // =========================================================
    // OPEN LOGIN
    // =========================================================

    private void openLogin() {

        android.content.Intent intent =
                new android.content.Intent(
                        MainActivity.this,
                        LoginActivity.class
                );

        startActivity(intent);
    }

    // =========================================================
    // ERROR
    // =========================================================

    private void showRegistrationError(
            String message
    ) {

        Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
        ).show();

        registerButton.setEnabled(true);
        registerButton.setText(
                "REGISTER STUDENT"
        );
    }

    // =========================================================
    // ADD SPACE
    // =========================================================

    private void addSpace(
            LinearLayout parent,
            int dp
    ) {

        View space = new View(this);

        parent.addView(
                space,
                new LinearLayout.LayoutParams(
                        1,
                        dp(dp)
                )
        );
    }

    // =========================================================
    // DP CONVERTER
    // =========================================================

    private int dp(int value) {

        return (int) (
                value *
                        getResources()
                                .getDisplayMetrics()
                                .density
        );
    }
}