package com.example.campuseventapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends Activity {

    private LinearLayout eventContainer;
    private LinearLayout categoryContainer;
    private EditText searchInput;
    private TextView sectionTitle;
    private TextView resultCount;

    private final List<Event> allEvents = new ArrayList<>();
    private final Set<String> favorites = new HashSet<>();

    private String selectedCategory = "All";
    private boolean showingFavorites = false;
    private boolean showingRecommendations = false;

    private final String[] categories = {
            "All", "Technology", "Workshop", "Cultural", "Sports", "Seminar"
    };

    private static class Event {
        final String id;
        final String title;
        final String category;
        final String date;
        final String venue;
        final String description;
        final int interestedStudents;

        Event(String id, String title, String category, String date,
              String venue, String description, int interestedStudents) {
            this.id = id;
            this.title = title;
            this.category = category;
            this.date = date;
            this.venue = venue;
            this.description = description;
            this.interestedStudents = interestedStudents;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        eventContainer = findViewById(R.id.eventContainer);
        categoryContainer = findViewById(R.id.categoryContainer);
        searchInput = findViewById(R.id.searchInput);
        sectionTitle = findViewById(R.id.sectionTitle);
        resultCount = findViewById(R.id.resultCount);

        loadSampleEvents();
        buildCategories();
        setupSearch();

        findViewById(R.id.recommendButton).setOnClickListener(v -> {
            showingFavorites = false;
            showingRecommendations = true;
            selectedCategory = "All";
            sectionTitle.setText("Recommended for you");
            buildCategories();
            renderEvents();
        });

        findViewById(R.id.favoritesButton).setOnClickListener(v -> {
            showingFavorites = !showingFavorites;
            showingRecommendations = false;
            sectionTitle.setText(showingFavorites ? "Saved events" : "Upcoming events");
            renderEvents();
        });

        renderEvents();
    }

    private void loadSampleEvents() {
        allEvents.add(new Event("1", "AI & Machine Learning Hackathon",
                "Technology", "15 Nov 2026", "Innovation Lab",
                "Build an AI-powered solution with fellow students. Open to beginners and experienced developers.",
                120));

        allEvents.add(new Event("2", "Android App Development Workshop",
                "Workshop", "18 Nov 2026", "Computer Science Block",
                "Learn Android Studio, Java, XML layouts and mobile UI development.",
                85));

        allEvents.add(new Event("3", "Campus Cultural Fest",
                "Cultural", "21 Nov 2026", "Main Auditorium",
                "Enjoy music, dance, performances and student talent.",
                350));

        allEvents.add(new Event("4", "Robotics Challenge",
                "Technology", "25 Nov 2026", "Electronics Lab",
                "Explore robotics, sensors, embedded systems and automation.",
                95));

        allEvents.add(new Event("5", "Career and Placement Seminar",
                "Seminar", "28 Nov 2026", "Seminar Hall",
                "Learn about interview preparation, resumes and career paths.",
                160));

        allEvents.add(new Event("6", "Inter-College Cricket Tournament",
                "Sports", "30 Nov 2026", "College Sports Ground",
                "Compete with teams and support your college players.",
                200));
    }

    private void buildCategories() {
        categoryContainer.removeAllViews();

        for (String category : categories) {
            Button button = new Button(this);
            button.setText(category);
            button.setAllCaps(false);
            button.setTextSize(13);
            button.setPadding(14, 0, 14, 0);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, 48);
            params.setMargins(0, 0, 8, 0);
            categoryContainer.addView(button, params);

            updateCategoryButton(button, category);

            button.setOnClickListener(v -> {
                selectedCategory = category;
                showingFavorites = false;
                showingRecommendations = false;
                sectionTitle.setText("Upcoming events");
                buildCategories();
                renderEvents();
            });
        }
    }

    private void updateCategoryButton(Button button, String category) {
        boolean selected = category.equals(selectedCategory);
        button.setTextColor(selected ? Color.WHITE : Color.rgb(60, 65, 85));
        button.setBackground(makeBackground(
                selected ? "#5B4BDA" : "#FFFFFF", 24));
    }

    private GradientDrawable makeBackground(String color, int radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.parseColor(color));
        drawable.setCornerRadius(radius);
        return drawable;
    }

    private void setupSearch() {
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                renderEvents();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed.
            }
        });
    }

    private void renderEvents() {
        eventContainer.removeAllViews();

        String query = searchInput.getText().toString().trim().toLowerCase();
        List<Event> displayed = new ArrayList<>();

        for (Event event : allEvents) {
            boolean matchesSearch =
                    event.title.toLowerCase().contains(query)
                    || event.category.toLowerCase().contains(query)
                    || event.venue.toLowerCase().contains(query);

            boolean matchesCategory = selectedCategory.equals("All")
                    || event.category.equals(selectedCategory);

            boolean matchesFavorites = !showingFavorites || favorites.contains(event.id);

            if (matchesSearch && matchesCategory && matchesFavorites) {
                displayed.add(event);
            }
        }

        if (showingRecommendations) {
            Collections.sort(displayed, new Comparator<Event>() {
                @Override
                public int compare(Event first, Event second) {
                    return Integer.compare(score(second), score(first));
                }
            });
        }

        resultCount.setText(displayed.size() + " events found");

        if (displayed.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("No events found. Try another search or category.");
            empty.setTextSize(15);
            empty.setTextColor(Color.DKGRAY);
            empty.setPadding(8, 24, 8, 24);
            eventContainer.addView(empty);
            return;
        }

        for (Event event : displayed) {
            eventContainer.addView(createEventCard(event));
        }
    }

    private int score(Event event) {
        int score = event.interestedStudents;
        if (event.category.equals("Technology")) score += 100;
        if (event.category.equals("Workshop")) score += 80;
        return score;
    }

    private LinearLayout createEventCard(Event event) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(16, 16, 16, 16);
        card.setBackground(makeBackground("#FFFFFF", 20));

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.setMargins(0, 8, 0, 12);
        card.setLayoutParams(cardParams);
        card.setElevation(3);

        TextView category = new TextView(this);
        category.setText("✦  " + event.category);
        category.setTextColor(Color.rgb(91, 75, 218));
        category.setTextSize(13);
        category.setTypeface(null, Typeface.BOLD);
        card.addView(category);

        TextView title = new TextView(this);
        title.setText(event.title);
        title.setTextSize(19);
        title.setTextColor(Color.rgb(24, 32, 51));
        title.setTypeface(null, Typeface.BOLD);
        title.setPadding(0, 10, 0, 8);
        card.addView(title);

        TextView date = new TextView(this);
        date.setText("Date: " + event.date);
        date.setTextColor(Color.DKGRAY);
        date.setTextSize(14);
        card.addView(date);

        TextView venue = new TextView(this);
        venue.setText("Venue: " + event.venue);
        venue.setTextColor(Color.DKGRAY);
        venue.setTextSize(14);
        venue.setPadding(0, 6, 0, 6);
        card.addView(venue);

        TextView popularity = new TextView(this);
        popularity.setText(event.interestedStudents + " students interested");
        popularity.setTextColor(Color.GRAY);
        popularity.setTextSize(13);
        card.addView(popularity);

        LinearLayout actions = new LinearLayout(this);
        actions.setGravity(Gravity.CENTER_VERTICAL);
        actions.setOrientation(LinearLayout.HORIZONTAL);

        Button details = new Button(this);
        details.setText("View details");
        details.setAllCaps(false);

        Button save = new Button(this);
        save.setText(favorites.contains(event.id) ? "Saved" : "Save");
        save.setAllCaps(false);

        actions.addView(details, new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        actions.addView(save);
        card.addView(actions);

        details.setOnClickListener(v -> showEventDetails(event));

        save.setOnClickListener(v -> {
            if (favorites.contains(event.id)) {
                favorites.remove(event.id);
                Toast.makeText(this, "Removed from saved events",
                        Toast.LENGTH_SHORT).show();
            } else {
                favorites.add(event.id);
                Toast.makeText(this, "Event saved!",
                        Toast.LENGTH_SHORT).show();
            }
            renderEvents();
        });

        card.setOnClickListener(v -> showEventDetails(event));
        return card;
    }

    private void showEventDetails(Event event) {
        new AlertDialog.Builder(this)
                .setTitle(event.title)
                .setMessage("Category: " + event.category
                        + "\nDate: " + event.date
                        + "\nVenue: " + event.venue
                        + "\n\n" + event.description
                        + "\n\nStudents interested: " + event.interestedStudents)
                .setPositiveButton("Close", (dialog, which) -> { })
                .setNeutralButton(favorites.contains(event.id) ? "Unsave" : "Save",
                        (dialog, which) -> {
                            if (favorites.contains(event.id)) {
                                favorites.remove(event.id);
                            } else {
                                favorites.add(event.id);
                            }
                            renderEvents();
                        })
                .show();
    }
}
