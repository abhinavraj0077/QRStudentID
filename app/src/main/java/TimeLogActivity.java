package com.example.qrstudentid;

import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TimeLogActivity extends AppCompatActivity {

    // =========================================================
    // FIREBASE
    // =========================================================

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    // =========================================================
    // UI COLORS
    // =========================================================

    private final int BG = Color.rgb(20, 19, 24);
    private final int CARD = Color.rgb(31, 30, 39);
    private final int PURPLE = Color.rgb(190, 145, 255);
    private final int LIGHT_PURPLE = Color.rgb(215, 185, 255);
    private final int WHITE = Color.WHITE;
    private final int GRAY = Color.rgb(180, 176, 190);
    private final int GREEN = Color.rgb(80, 220, 140);

    // =========================================================
    // UI
    // =========================================================

    private TextView inTimeText;
    private TextView outTimeText;
    private TextView statusText;

    private Button checkInButton;
    private Button checkOutButton;

    // =========================================================
    // DATE
    // =========================================================

    private String todayDate;

    // =========================================================
    // ON CREATE
    // =========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        todayDate = new SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
        ).format(new Date());

        showTimeLogScreen();

        loadTodayAttendance();
    }

    // =========================================================
    // TIME LOG SCREEN
    // =========================================================

    private void showTimeLogScreen() {

        ScrollView scrollView = new ScrollView(this);
        scrollView.setBackgroundColor(BG);

        LinearLayout root = new LinearLayout(this);

        root.setOrientation(
                LinearLayout.VERTICAL
        );

        root.setPadding(
                dp(22),
                dp(25),
                dp(22),
                dp(35)
        );

        scrollView.addView(root);

        // =====================================================
        // BACK BUTTON
        // =====================================================

        Button backButton = new Button(this);

        backButton.setText("BACK");
        backButton.setTextSize(14);
        backButton.setTextColor(Color.rgb(55, 30, 90));
        backButton.setAllCaps(false);
        backButton.setBackgroundColor(PURPLE);

        LinearLayout.LayoutParams backParams =
                new LinearLayout.LayoutParams(
                        dp(80),
                        dp(48)
                );

        root.addView(
                backButton,
                backParams
        );

        backButton.setOnClickListener(v -> finish());

        addSpace(root, 20);

        // =====================================================
        // TITLE
        // =====================================================

        TextView title = new TextView(this);

        title.setText("TIME LOG");
        title.setTextColor(WHITE);
        title.setTextSize(30);
        title.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );
        title.setGravity(Gravity.CENTER);

        root.addView(title);

        addSpace(root, 5);

        // =====================================================
        // SUBTITLE
        // =====================================================

        TextView subtitle = new TextView(this);

        subtitle.setText(
                "Daily check-in and check-out time"
        );

        subtitle.setTextColor(GRAY);
        subtitle.setTextSize(15);
        subtitle.setGravity(Gravity.CENTER);

        root.addView(subtitle);

        addSpace(root, 25);

        // =====================================================
        // TODAY CARD
        // =====================================================

        LinearLayout todayCard =
                createCard();

        root.addView(todayCard);

        TextView todayTitle = new TextView(this);

        todayTitle.setText("TODAY");
        todayTitle.setTextColor(PURPLE);
        todayTitle.setTextSize(16);
        todayTitle.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        todayCard.addView(todayTitle);

        addSpace(todayCard, 8);

        TextView dateText = new TextView(this);

        String displayDate =
                new SimpleDateFormat(
                        "EEEE, dd MMMM yyyy",
                        Locale.getDefault()
                ).format(new Date());

        dateText.setText(displayDate);
        dateText.setTextColor(WHITE);
        dateText.setTextSize(18);

        todayCard.addView(dateText);

        addSpace(todayCard, 20);

        // =====================================================
        // IN TIME
        // =====================================================

        LinearLayout inCard =
                createSmallCard();

        todayCard.addView(inCard);

        TextView inLabel = new TextView(this);

        inLabel.setText("IN TIME");
        inLabel.setTextColor(GREEN);
        inLabel.setTextSize(14);
        inLabel.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        inCard.addView(inLabel);

        addSpace(inCard, 5);

        inTimeText = new TextView(this);

        inTimeText.setText("--");
        inTimeText.setTextColor(WHITE);
        inTimeText.setTextSize(25);
        inTimeText.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        inCard.addView(inTimeText);

        addSpace(todayCard, 12);

        // =====================================================
        // OUT TIME
        // =====================================================

        LinearLayout outCard =
                createSmallCard();

        todayCard.addView(outCard);

        TextView outLabel = new TextView(this);

        outLabel.setText("OUT TIME");
        outLabel.setTextColor(PURPLE);
        outLabel.setTextSize(14);
        outLabel.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        outCard.addView(outLabel);

        addSpace(outCard, 5);

        outTimeText = new TextView(this);

        outTimeText.setText("--");
        outTimeText.setTextColor(WHITE);
        outTimeText.setTextSize(25);
        outTimeText.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        outCard.addView(outTimeText);

        addSpace(todayCard, 12);

        // =====================================================
        // STATUS
        // =====================================================

        LinearLayout statusCard =
                createSmallCard();

        todayCard.addView(statusCard);

        TextView statusLabel = new TextView(this);

        statusLabel.setText("TODAY'S STATUS");
        statusLabel.setTextColor(LIGHT_PURPLE);
        statusLabel.setTextSize(14);
        statusLabel.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        statusCard.addView(statusLabel);

        addSpace(statusCard, 5);

        statusText = new TextView(this);

        statusText.setText("Loading...");
        statusText.setTextColor(WHITE);
        statusText.setTextSize(17);

        statusCard.addView(statusText);

        addSpace(root, 25);

        // =====================================================
        // CHECK IN
        // =====================================================

        checkInButton =
                createActionButton("CHECK IN");

        root.addView(checkInButton);

        checkInButton.setOnClickListener(
                v -> checkIn()
        );

        addSpace(root, 12);

        // =====================================================
        // CHECK OUT
        // =====================================================

        checkOutButton =
                createActionButton("CHECK OUT");

        root.addView(checkOutButton);

        checkOutButton.setOnClickListener(
                v -> checkOut()
        );

        addSpace(root, 25);

        // =====================================================
        // INFORMATION
        // =====================================================

        TextView info = new TextView(this);

        info.setText(
                "Your attendance time is stored securely in Firebase.\n\n" +
                        "Check in when you arrive at college and check out when you leave."
        );

        info.setTextColor(GRAY);
        info.setTextSize(13);
        info.setGravity(Gravity.CENTER);
        info.setPadding(
                dp(15),
                dp(10),
                dp(15),
                dp(10)
        );

        root.addView(info);

        setContentView(scrollView);
    }

    // =========================================================
    // LOAD TODAY ATTENDANCE
    // =========================================================

    private void loadTodayAttendance() {

        if (auth.getCurrentUser() == null) {

            statusText.setText(
                    "Please login again"
            );

            return;
        }

        String uid =
                auth.getCurrentUser().getUid();

        /*
         * Attendance structure used:
         *
         * attendance
         *    └── USER_UID
         *          └── daily
         *                └── 2026-10-04
         *
         * Fields:
         * inTime
         * outTime
         * status
         */

        db.collection("attendance")
                .document(uid)
                .collection("daily")
                .document(todayDate)
                .get()
                .addOnSuccessListener(
                        document -> {

                            if (document.exists()) {

                                displayAttendance(
                                        document
                                );

                            } else {

                                inTimeText.setText("--");
                                outTimeText.setText("--");
                                statusText.setText(
                                        "Not marked"
                                );
                            }
                        }
                )
                .addOnFailureListener(
                        e -> {

                            statusText.setText(
                                    "Unable to load attendance"
                            );

                            Toast.makeText(
                                    this,
                                    "Firebase error: " +
                                            e.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                );
    }

    // =========================================================
    // DISPLAY ATTENDANCE
    // =========================================================

    private void displayAttendance(
            DocumentSnapshot document
    ) {

        String inTime =
                document.getString("inTime");

        String outTime =
                document.getString("outTime");

        String status =
                document.getString("status");

        if (inTime == null ||
                inTime.isEmpty()) {

            inTimeText.setText("--");

        } else {

            inTimeText.setText(inTime);
        }

        if (outTime == null ||
                outTime.isEmpty()) {

            outTimeText.setText("--");

        } else {

            outTimeText.setText(outTime);
        }

        if (status == null ||
                status.isEmpty()) {

            statusText.setText(
                    "Not marked"
            );

        } else {

            statusText.setText(
                    "● " + status
            );
        }
    }

    // =========================================================
    // CHECK IN
    // =========================================================

    private void checkIn() {

        if (auth.getCurrentUser() == null) {

            Toast.makeText(
                    this,
                    "Please login first",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String uid =
                auth.getCurrentUser().getUid();

        String currentTime =
                new SimpleDateFormat(
                        "hh:mm a",
                        Locale.getDefault()
                ).format(new Date());

        checkInButton.setEnabled(false);

        Map<String, Object> data =
                new HashMap<>();

        data.put(
                "inTime",
                currentTime
        );

        data.put(
                "date",
                todayDate
        );

        data.put(
                "status",
                "Present"
        );

        db.collection("attendance")
                .document(uid)
                .collection("daily")
                .document(todayDate)
                .set(
                        data,
                        com.google.firebase.firestore.SetOptions.merge()
                )
                .addOnSuccessListener(
                        unused -> {

                            inTimeText.setText(
                                    currentTime
                            );

                            statusText.setText(
                                    "● Present"
                            );

                            Toast.makeText(
                                    this,
                                    "Check-in successful",
                                    Toast.LENGTH_SHORT
                            ).show();

                            checkInButton.setEnabled(
                                    true
                            );
                        }
                )
                .addOnFailureListener(
                        e -> {

                            Toast.makeText(
                                    this,
                                    "Check-in failed: " +
                                            e.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();

                            checkInButton.setEnabled(
                                    true
                            );
                        }
                );
    }

    // =========================================================
    // CHECK OUT
    // =========================================================

    private void checkOut() {

        if (auth.getCurrentUser() == null) {

            Toast.makeText(
                    this,
                    "Please login first",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String uid =
                auth.getCurrentUser().getUid();

        String currentTime =
                new SimpleDateFormat(
                        "hh:mm a",
                        Locale.getDefault()
                ).format(new Date());

        checkOutButton.setEnabled(false);

        Map<String, Object> data =
                new HashMap<>();

        data.put(
                "outTime",
                currentTime
        );

        data.put(
                "date",
                todayDate
        );

        data.put(
                "status",
                "Present"
        );

        db.collection("attendance")
                .document(uid)
                .collection("daily")
                .document(todayDate)
                .set(
                        data,
                        com.google.firebase.firestore.SetOptions.merge()
                )
                .addOnSuccessListener(
                        unused -> {

                            outTimeText.setText(
                                    currentTime
                            );

                            statusText.setText(
                                    "● Attendance completed"
                            );

                            Toast.makeText(
                                    this,
                                    "Check-out successful",
                                    Toast.LENGTH_SHORT
                            ).show();

                            checkOutButton.setEnabled(
                                    true
                            );
                        }
                )
                .addOnFailureListener(
                        e -> {

                            Toast.makeText(
                                    this,
                                    "Check-out failed: " +
                                            e.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();

                            checkOutButton.setEnabled(
                                    true
                            );
                        }
                );
    }

    // =========================================================
    // CREATE CARD
    // =========================================================

    private LinearLayout createCard() {

        LinearLayout card =
                new LinearLayout(this);

        card.setOrientation(
                LinearLayout.VERTICAL
        );

        card.setPadding(
                dp(20),
                dp(20),
                dp(20),
                dp(20)
        );

        card.setBackgroundColor(CARD);

        card.setLayoutParams(
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );

        return card;
    }

    // =========================================================
    // CREATE SMALL CARD
    // =========================================================

    private LinearLayout createSmallCard() {

        LinearLayout card =
                new LinearLayout(this);

        card.setOrientation(
                LinearLayout.VERTICAL
        );

        card.setPadding(
                dp(16),
                dp(14),
                dp(16),
                dp(14)
        );

        card.setBackgroundColor(
                Color.rgb(39, 38, 48)
        );

        card.setLayoutParams(
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );

        return card;
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

        button.setTextColor(
                Color.rgb(55, 30, 90)
        );

        button.setAllCaps(false);

        button.setBackgroundColor(
                PURPLE
        );

        button.setLayoutParams(
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(58)
                )
        );

        return button;
    }

    // =========================================================
    // SPACE
    // =========================================================

    private void addSpace(
            LinearLayout parent,
            int dp
    ) {

        TextView space =
                new TextView(this);

        parent.addView(
                space,
                new LinearLayout.LayoutParams(
                        1,
                        dp(dp)
                )
        );
    }

    // =========================================================
    // DP
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