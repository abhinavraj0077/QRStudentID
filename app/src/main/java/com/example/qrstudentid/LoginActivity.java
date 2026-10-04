package com.example.qrstudentid;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private EditText emailInput;
    private EditText passwordInput;
    private Button loginButton;

    // =========================================================
    // COLORS
    // =========================================================

    private final int BG = Color.rgb(20, 19, 24);
    private final int CARD = Color.rgb(31, 30, 39);
    private final int INPUT = Color.rgb(45, 44, 56);
    private final int PURPLE = Color.rgb(170, 125, 255);
    private final int DARK_PURPLE = Color.rgb(45, 25, 80);
    private final int WHITE = Color.WHITE;
    private final int GRAY = Color.rgb(175, 171, 185);
    private final int GREEN = Color.rgb(70, 210, 140);
    private final int RED = Color.rgb(245, 100, 110);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        showLoginScreen();
    }

    // =========================================================
    // LOGIN SCREEN
    // =========================================================

    private void showLoginScreen() {

        ScrollView scrollView = new ScrollView(this);
        scrollView.setBackgroundColor(BG);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);

        root.setPadding(
                dp(25),
                dp(45),
                dp(25),
                dp(30)
        );

        scrollView.addView(root);

        // LOGO
        TextView logo = new TextView(this);

        logo.setText("QR");
        logo.setTextColor(WHITE);
        logo.setTextSize(22);
        logo.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );
        logo.setGravity(Gravity.CENTER);
        logo.setBackgroundColor(
                Color.rgb(125, 75, 220)
        );

        root.addView(
                logo,
                new LinearLayout.LayoutParams(
                        dp(70),
                        dp(70)
                )
        );

        addSpace(root, 18);

        // TITLE
        TextView title = new TextView(this);

        title.setText("Welcome Back");
        title.setTextColor(WHITE);
        title.setTextSize(31);
        title.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );
        title.setGravity(Gravity.CENTER);

        root.addView(title);

        addSpace(root, 6);

        // SUBTITLE
        TextView subtitle = new TextView(this);

        subtitle.setText(
                "Login to access your digital student ID"
        );

        subtitle.setTextColor(GRAY);
        subtitle.setTextSize(16);
        subtitle.setGravity(Gravity.CENTER);

        root.addView(subtitle);

        addSpace(root, 30);

        // LOGIN CARD
        LinearLayout card = new LinearLayout(this);

        card.setOrientation(
                LinearLayout.VERTICAL
        );

        card.setPadding(
                dp(23),
                dp(27),
                dp(23),
                dp(27)
        );

        card.setBackgroundColor(CARD);

        root.addView(
                card,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );

        TextView heading = new TextView(this);

        heading.setText("Student Login");
        heading.setTextColor(WHITE);
        heading.setTextSize(25);
        heading.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        card.addView(heading);

        addSpace(card, 5);

        TextView description = new TextView(this);

        description.setText(
                "Enter your registered account details"
        );

        description.setTextColor(GRAY);
        description.setTextSize(16);

        card.addView(description);

        addSpace(card, 25);

        // EMAIL
        TextView emailLabel =
                createLabel("EMAIL ADDRESS");

        card.addView(emailLabel);

        addSpace(card, 6);

        emailInput =
                createInput("student@example.com");

        emailInput.setInputType(
                InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        );

        card.addView(emailInput);

        addSpace(card, 18);

        // PASSWORD
        TextView passwordLabel =
                createLabel("PASSWORD");

        card.addView(passwordLabel);

        addSpace(card, 6);

        passwordInput =
                createInput("Enter your password");

        passwordInput.setInputType(
                InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD
        );

        card.addView(passwordInput);

        addSpace(card, 25);

        // LOGIN BUTTON
        loginButton = new Button(this);

        loginButton.setText("LOGIN");
        loginButton.setTextSize(17);
        loginButton.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        loginButton.setTextColor(DARK_PURPLE);
        loginButton.setAllCaps(false);
        loginButton.setBackgroundColor(PURPLE);

        card.addView(
                loginButton,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(58)
                )
        );

        addSpace(card, 25);

        // REGISTER
        TextView registerText =
                new TextView(this);

        registerText.setText(
                "New student?  CREATE ACCOUNT"
        );

        registerText.setTextColor(PURPLE);
        registerText.setTextSize(16);

        registerText.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        registerText.setGravity(Gravity.CENTER);

        card.addView(registerText);

        registerText.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            LoginActivity.this,
                            MainActivity.class
                    );

            startActivity(intent);
            finish();
        });

        loginButton.setOnClickListener(
                v -> loginStudent()
        );

        setContentView(scrollView);
    }

    // =========================================================
    // LOGIN
    // =========================================================

    private void loginStudent() {

        String email =
                emailInput
                        .getText()
                        .toString()
                        .trim();

        String password =
                passwordInput
                        .getText()
                        .toString()
                        .trim();

        if (email.isEmpty()) {

            emailInput.setError(
                    "Enter email"
            );

            return;
        }

        if (password.isEmpty()) {

            passwordInput.setError(
                    "Enter password"
            );

            return;
        }

        loginButton.setEnabled(false);
        loginButton.setText("LOGGING IN...");

        auth.signInWithEmailAndPassword(
                        email,
                        password
                )
                .addOnCompleteListener(
                        this,
                        task -> {

                            if (task.isSuccessful()) {

                                String uid =
                                        auth.getCurrentUser()
                                                .getUid();

                                db.collection("students")
                                        .document(uid)
                                        .get()
                                        .addOnSuccessListener(
                                                document -> {

                                                    if (document.exists()) {

                                                        Toast.makeText(
                                                                this,
                                                                "Login successful",
                                                                Toast.LENGTH_SHORT
                                                        ).show();

                                                        showHomePage(
                                                                document
                                                        );

                                                    } else {

                                                        Toast.makeText(
                                                                this,
                                                                "Student profile not found",
                                                                Toast.LENGTH_LONG
                                                        ).show();

                                                        auth.signOut();

                                                        resetLoginButton();
                                                    }
                                                }
                                        )
                                        .addOnFailureListener(
                                                e -> {

                                                    Toast.makeText(
                                                            this,
                                                            "Database error: " +
                                                                    e.getMessage(),
                                                            Toast.LENGTH_LONG
                                                    ).show();

                                                    resetLoginButton();
                                                }
                                        );

                            } else {

                                String message =
                                        "Login failed";

                                if (task.getException() != null) {

                                    message +=
                                            ": " +
                                                    task.getException()
                                                            .getMessage();
                                }

                                Toast.makeText(
                                        this,
                                        message,
                                        Toast.LENGTH_LONG
                                ).show();

                                resetLoginButton();
                            }
                        }
                );
    }

    private void resetLoginButton() {

        loginButton.setEnabled(true);
        loginButton.setText("LOGIN");
    }

    // =========================================================
    // HOME PAGE
    // =========================================================

    private void showHomePage(
            DocumentSnapshot student
    ) {

        ScrollView scrollView =
                new ScrollView(this);

        scrollView.setBackgroundColor(BG);

        LinearLayout root =
                new LinearLayout(this);

        root.setOrientation(
                LinearLayout.VERTICAL
        );

        root.setPadding(
                dp(20),
                dp(30),
                dp(20),
                dp(30)
        );

        scrollView.addView(root);

        // -----------------------------------------------------
        // WELCOME
        // -----------------------------------------------------

        String studentName =
                student.getString("name");

        if (studentName == null) {
            studentName = "Student";
        }

        TextView welcome =
                new TextView(this);

        welcome.setText(
                "WELCOME, " +
                        studentName.toUpperCase()
        );

        welcome.setTextColor(WHITE);
        welcome.setTextSize(26);

        welcome.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        welcome.setGravity(
                Gravity.CENTER
        );

        root.addView(welcome);

        addSpace(root, 5);

        TextView welcomeSub =
                new TextView(this);

        welcomeSub.setText(
                "Your Student ID Dashboard"
        );

        welcomeSub.setTextColor(GRAY);
        welcomeSub.setTextSize(15);
        welcomeSub.setGravity(Gravity.CENTER);

        root.addView(welcomeSub);

        addSpace(root, 25);

        // -----------------------------------------------------
        // STUDENT CARD
        // -----------------------------------------------------

        LinearLayout studentCard =
                new LinearLayout(this);

        studentCard.setOrientation(
                LinearLayout.VERTICAL
        );

        studentCard.setPadding(
                dp(22),
                dp(22),
                dp(22),
                dp(22)
        );

        studentCard.setBackgroundColor(CARD);

        root.addView(
                studentCard,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );

        TextView cardTitle =
                new TextView(this);

        cardTitle.setText(
                "DIGITAL STUDENT ID"
        );

        cardTitle.setTextColor(PURPLE);
        cardTitle.setTextSize(20);

        cardTitle.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        cardTitle.setGravity(
                Gravity.CENTER
        );

        studentCard.addView(cardTitle);

        addSpace(studentCard, 15);

        // STUDENT NAME
        addHomeDetail(
                studentCard,
                "NAME",
                student.getString("name")
        );

        // STUDENT ID
        addHomeDetail(
                studentCard,
                "STUDENT ID",
                student.getString("studentId")
        );

        // COURSE
        addHomeDetail(
                studentCard,
                "COURSE",
                student.getString("course")
        );

        // YEAR
        addHomeDetail(
                studentCard,
                "YEAR",
                student.getString("year")
        );

        addSpace(studentCard, 10);

        // QR CODE
        ImageView qrImage =
                new ImageView(this);

        String studentId =
                student.getString("studentId");

        if (studentId != null &&
                !studentId.isEmpty()) {

            Bitmap qrBitmap =
                    generateQRCode(studentId);

            if (qrBitmap != null) {

                qrImage.setImageBitmap(
                        qrBitmap
                );

                qrImage.setPadding(
                        dp(10),
                        dp(10),
                        dp(10),
                        dp(10)
                );

                LinearLayout.LayoutParams qrParams =
                        new LinearLayout.LayoutParams(
                                dp(210),
                                dp(210)
                        );

                qrParams.gravity =
                        Gravity.CENTER;

                studentCard.addView(
                        qrImage,
                        qrParams
                );
            }
        }

        addSpace(studentCard, 5);

        TextView qrText =
                new TextView(this);

        qrText.setText(
                "Scan this QR code to verify the student"
        );

        qrText.setTextColor(GRAY);
        qrText.setTextSize(13);
        qrText.setGravity(
                Gravity.CENTER
        );

        studentCard.addView(qrText);

        addSpace(root, 22);

        // -----------------------------------------------------
        // QUICK ACTIONS TITLE
        // -----------------------------------------------------

        TextView quickTitle =
                new TextView(this);

        quickTitle.setText(
                "QUICK ACTIONS"
        );

        quickTitle.setTextColor(WHITE);
        quickTitle.setTextSize(21);

        quickTitle.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        root.addView(quickTitle);

        addSpace(root, 12);

        // -----------------------------------------------------
        // SCAN QR
        // -----------------------------------------------------

        Button scanButton =
                createActionButton(
                        "📷  SCAN QR CODE"
                );

        root.addView(scanButton);

        scanButton.setOnClickListener(
                v -> openScanner()
        );

        addSpace(root, 12);

        // -----------------------------------------------------
        // ATTENDANCE
        // -----------------------------------------------------

        Button attendanceButton =
                createActionButton(
                        "📊  ATTENDANCE"
                );

        root.addView(attendanceButton);

        attendanceButton.setOnClickListener(
                v -> openAttendance()
        );

        addSpace(root, 12);

        // -----------------------------------------------------
        // IN TIME
        // -----------------------------------------------------

        Button inTimeButton =
                createActionButton(
                        "🟢  IN TIME"
                );

        root.addView(inTimeButton);

        inTimeButton.setOnClickListener(
                v -> openTimeLog()
        );

        addSpace(root, 12);

        // -----------------------------------------------------
        // OUT TIME
        // -----------------------------------------------------

        Button outTimeButton =
                createActionButton(
                        "🔴  OUT TIME"
                );

        root.addView(outTimeButton);

        outTimeButton.setOnClickListener(
                v -> openTimeLog()
        );

        addSpace(root, 12);

        // -----------------------------------------------------
        // UPDATE PROFILE
        // -----------------------------------------------------

        Button profileButton =
                createActionButton(
                        "👤  UPDATE PROFILE"
                );

        root.addView(profileButton);

        profileButton.setOnClickListener(
                v -> openProfile()
        );

        addSpace(root, 12);

        // -----------------------------------------------------
        // LOGOUT
        // -----------------------------------------------------

        Button logoutButton =
                createActionButton(
                        "🚪  LOGOUT"
                );

        root.addView(logoutButton);

        logoutButton.setOnClickListener(
                v -> logoutStudent()
        );

        addSpace(root, 20);

        // -----------------------------------------------------
        // FOOTER
        // -----------------------------------------------------

        TextView footer =
                new TextView(this);

        footer.setText(
                "QR Student ID • Secure Digital Identity"
        );

        footer.setTextColor(GRAY);
        footer.setTextSize(13);
        footer.setGravity(
                Gravity.CENTER
        );

        root.addView(footer);

        setContentView(scrollView);
    }

    // =========================================================
    // OPEN SCANNER
    // =========================================================

    private void openScanner() {

        try {

            Intent intent =
                    new Intent(
                            LoginActivity.this,
                            ScannerActivity.class
                    );

            startActivity(intent);

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Scanner screen not available",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    // =========================================================
    // OPEN ATTENDANCE
    // =========================================================

    private void openAttendance() {

        try {

            Intent intent =
                    new Intent(
                            LoginActivity.this,
                            AttendanceActivity.class
                    );

            startActivity(intent);

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Attendance screen not available",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    // =========================================================
    // OPEN TIME LOG
    // =========================================================

    private void openTimeLog() {

        try {

            Intent intent =
                    new Intent(
                            LoginActivity.this,
                            TimeLogActivity.class
                    );

            startActivity(intent);

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Time log screen not available",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    // =========================================================
    // OPEN PROFILE
    // =========================================================

    private void openProfile() {

        try {

            Intent intent =
                    new Intent(
                            LoginActivity.this,
                            ProfileUpdateActivity.class
                    );

            startActivity(intent);

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Profile screen not available",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    // =========================================================
    // LOGOUT
    // =========================================================

    private void logoutStudent() {

        auth.signOut();

        Toast.makeText(
                this,
                "Logged out successfully",
                Toast.LENGTH_SHORT
        ).show();

        showLoginScreen();
    }

    // =========================================================
    // HOME DETAIL
    // =========================================================

    private void addHomeDetail(
            LinearLayout parent,
            String label,
            String value
    ) {

        if (value == null) {
            value = "";
        }

        TextView text =
                new TextView(this);

        text.setText(
                label +
                        "\n" +
                        value
        );

        text.setTextColor(WHITE);
        text.setTextSize(16);

        text.setPadding(
                0,
                dp(6),
                0,
                dp(6)
        );

        parent.addView(text);
    }

    // =========================================================
    // ACTION BUTTON
    // =========================================================

    private Button createActionButton(
            String text
    ) {

        Button button =
                new Button(this);

        button.setText(text);
        button.setTextSize(16);

        button.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        button.setTextColor(DARK_PURPLE);
        button.setAllCaps(false);
        button.setBackgroundColor(PURPLE);

        button.setLayoutParams(
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(58)
                )
        );

        return button;
    }

    // =========================================================
    // LOGIN LABEL
    // =========================================================

    private TextView createLabel(
            String text
    ) {

        TextView label =
                new TextView(this);

        label.setText(text);
        label.setTextColor(
                Color.rgb(220, 216, 230)
        );

        label.setTextSize(14);

        label.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        return label;
    }

    // =========================================================
    // INPUT
    // =========================================================

    private EditText createInput(
            String hint
    ) {

        EditText input =
                new EditText(this);

        input.setHint(hint);

        input.setHintTextColor(
                Color.rgb(150, 147, 160)
        );

        input.setTextColor(WHITE);
        input.setTextSize(17);
        input.setSingleLine(true);

        input.setPadding(
                dp(18),
                0,
                dp(18),
                0
        );

        input.setBackgroundColor(INPUT);

        input.setLayoutParams(
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(52)
                )
        );

        return input;
    }

    // =========================================================
    // QR GENERATOR
    // =========================================================

    private Bitmap generateQRCode(
            String text
    ) {

        try {

            int width = 600;
            int height = 600;

            BitMatrix matrix =
                    new MultiFormatWriter()
                            .encode(
                                    text,
                                    BarcodeFormat.QR_CODE,
                                    width,
                                    height
                            );

            Bitmap bitmap =
                    Bitmap.createBitmap(
                            width,
                            height,
                            Bitmap.Config.ARGB_8888
                    );

            for (int x = 0; x < width; x++) {

                for (int y = 0; y < height; y++) {

                    bitmap.setPixel(
                            x,
                            y,
                            matrix.get(x, y)
                                    ? Color.BLACK
                                    : Color.WHITE
                    );
                }
            }

            return bitmap;

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "QR generation failed: " +
                            e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();

            return null;
        }
    }

    // =========================================================
    // SPACE
    // =========================================================

    private void addSpace(
            LinearLayout parent,
            int value
    ) {

        TextView space =
                new TextView(this);

        parent.addView(
                space,
                new LinearLayout.LayoutParams(
                        1,
                        dp(value)
                )
        );
    }

    // =========================================================
    // DP
    // =========================================================

    private int dp(
            int value
    ) {

        return (int) (
                value *
                        getResources()
                                .getDisplayMetrics()
                                .density
        );
    }
}