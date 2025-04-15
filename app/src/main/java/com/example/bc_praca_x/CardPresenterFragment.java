package com.example.bc_praca_x;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.transition.Slide;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.example.bc_praca_x.algorithm.SM2;
import com.example.bc_praca_x.algorithm.SuccessRateCalculator;
import com.example.bc_praca_x.custom_dialog.CustomDialog;
import com.example.bc_praca_x.database.POJO.CardWithParts;
import com.example.bc_praca_x.database.entity.Card;
import com.example.bc_praca_x.database.entity.CardPart;
import com.example.bc_praca_x.database.enums.ActivityType;
import com.example.bc_praca_x.database.enums.CardSide;
import com.example.bc_praca_x.database.viewmodel.CardViewModel;
import com.example.bc_praca_x.database.viewmodel.MediaViewModel;
import com.example.bc_praca_x.database.viewmodel.TaskViewModel;
import com.example.bc_praca_x.helpers.HTMLStyler;
import com.example.bc_praca_x.helpers.TaskHelper;
import com.example.bc_praca_x.helpers.Timer;
import com.example.bc_praca_x.models.FreeModeWithRating;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import ru.noties.jlatexmath.JLatexMathDrawable;

public class CardPresenterFragment extends FragmentSetup {
    private long packId;
    private String packName, categoryName;
    private List<CardWithParts> cards;
    private CardViewModel cardViewModel;
    private MediaViewModel mediaViewModel;
    private TaskViewModel taskViewModel;
    private LinearLayout dynamicContentLayout, certaintyMeterButtons;
    private CardView flashCardContent;
    private TextView cardSideTextView, certaintyMeterText, progressBarText;
    private ProgressBar progressBar;
    private int currentCardIndex = 0;
    private boolean isFront = true;
    private SM2 sm2;
    private int mode, order;
    private List<Integer> SMRatings;
    private Button finishLearningSessionButton, freeModeNextCardButton;
    private OnBackPressedCallback callback;
    private Timer timer;
    private boolean sessionStarted, endedSession = false;
    private int currentSecondsWhenDisplayedNewCard;
    private GestureDetector gestureDetector;
    private FrameLayout flayout;
    private ScrollView scrollView;
    private ActivityType activityType;
    private TaskHelper taskHelper;
    private String frontLabel, backLabel;

    public CardPresenterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        packId = getArguments() != null ? getArguments().getLong("pack_id", 1) : -1;
        mode = getArguments() != null ? getArguments().getInt("mode") : 1;
        order = getArguments() != null ? getArguments().getInt("order") : 1;

        switch (mode) {
            case 0:
                activityType = ActivityType.FREE_MODE_WITH_LEARNING;
                break;
            case 1:
                activityType = ActivityType.FREE_MODE;
                break;
            case 2:
                activityType = ActivityType.ALGORITHM;
                break;
        }

        packName = "";
        categoryName = "";

        frontLabel = getString(R.string.FRONT);
        backLabel = getString(R.string.BACK);

        packName = getArguments() != null ? getArguments().getString("packageName") : "";
        categoryName = getArguments() != null ? getArguments().getString("categoryName") : "";

        if (mode != 1) SMRatings = new ArrayList<>();

        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            this.timer = activity.getTimer();
            this.timer.reset();
            this.timer.start();
        }

        callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                CustomDialog dialog = CustomDialog.pauseDialogInstance("pauseDialog");
                timer.pause();
                dialog.show(getChildFragmentManager(), "custom_dialog");
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

        requireActivity().getSupportFragmentManager().setFragmentResultListener(
                "pause_dialog_session_finished", this, (requestKey, bundle) -> {
                    if (bundle.getBoolean("sessionFinished", false)) {
                        onFinishLearningSession();
                    }
                }
        );
    }

    @Override
    public void onResume() {
        super.onResume();
        timer.resume();

        if (floatingButtonController != null) {
            floatingButtonController.updateFloatingButton(
                    null,
                    null,
                    () -> {
                        CustomDialog dialog = CustomDialog.pauseDialogInstance("pauseDialog");
                        timer.pause();
                        dialog.show(getChildFragmentManager(), "custom_dialog");
                    },
                    true,
                    R.drawable.baseline_pause
            );
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        timer.pause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_card_presenter, container, false);
        dynamicContentLayout = view.findViewById(R.id.dynamicContent);
        flashCardContent = view.findViewById(R.id.flashCardContent);
        TextView breadcrumbs = view.findViewById(R.id.breadcrumbs);
        breadcrumbs.setText(capitalizeFirstLetter(categoryName) + " -> " + capitalizeFirstLetter(packName));

        flashCardContent.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        flashCardContent.setClickable(true);
        flashCardContent.setFocusable(true);

        certaintyMeterText = view.findViewById(R.id.CertaintyMeterText);
        certaintyMeterButtons = view.findViewById(R.id.CertaintyMeterButtons);
        freeModeNextCardButton = view.findViewById(R.id.free_mode_next_card);

        scrollView = view.findViewById(R.id.flashCardScrollView);

        gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                flashCardContent.callOnClick();
                return true;
            }
        });

        scrollView = view.findViewById(R.id.flashCardScrollView);

        sm2 = new SM2();
        initSMButtons(view);

        flayout = view.findViewById(R.id.flashCardFrame);
        flayout.setCameraDistance(12000 * getResources().getDisplayMetrics().density);

        progressBar = view.findViewById(R.id.progressBar);
        progressBarText = view.findViewById(R.id.progressText);
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        mediaViewModel = new ViewModelProvider(this).get(MediaViewModel.class);

        taskHelper = new TaskHelper(taskViewModel);

        finishLearningSessionButton = view.findViewById(R.id.finishLearningSession);

        cardSideTextView = view.findViewById(R.id.cardSideTextView);
        cardViewModel = new ViewModelProvider(this).get(CardViewModel.class);
        loadCards();

        return view;
    }

    private void loadCards() {
        cardViewModel.getCardsWithPartsByPackId(packId, mode == 2)
                .observe(getViewLifecycleOwner(), cardsWithParts -> {
                    if (!sessionStarted) {
                        if (order == 0) {        //created_at
                            cardsWithParts.sort((o1, o2) -> Long.compare(o1.card.id, o2.card.id));
                        } else if (order == 1) { //random
                            Collections.shuffle(cardsWithParts);
                        }
                        cards = new ArrayList<>(cardsWithParts);
                        if (!cards.isEmpty()) {
                            displayCard();
                            progressBarText.setText((currentCardIndex + 1) + "/" + cards.size());
                        }
                        attachListener();
                        sessionStarted = true;
                    }
                });
    }


    private void displayCard() {
        //PROGRESS BAR
        progressBar.setProgress((currentCardIndex + 1) * 100 / cards.size());
        progressBarText.setText((currentCardIndex + 1) + "/" + cards.size());

        //TIMER START FOR THE NEW CARD
        if (currentSecondsWhenDisplayedNewCard == 0) {
            currentSecondsWhenDisplayedNewCard = timer.getElapsedTime();
        }

        //DISPLAYING THE CURRENT CARD - FRONT OR BACK
        List<CardPart> parts = cards.get(currentCardIndex).cardParts;
        dynamicContentLayout.removeAllViews();
        Collections.sort(parts, (o1, o2) -> Integer.compare(o1.getOrderIndex(), o2.getOrderIndex()));

        for (CardPart part : parts) {
            if (isFront && (part.getSide() == CardSide.FRONT)) {

                switch (mode) {
                    case 0:
                    case 2:
                        certaintyMeterText.setVisibility(View.GONE);
                        certaintyMeterButtons.setVisibility(View.GONE);
                        break;
                    case 1:
                        freeModeNextCardButton.setVisibility(View.GONE);
                        break;
                }

                dynamicContentLayout.addView(createContentView(part));

            } else if (!isFront && (part.getSide() == CardSide.BACK)) {
                dynamicContentLayout.addView(createContentView(part));
                switch (mode) {
                    case 0: //free mode with rating
                    case 2: //algorithm mode
                        if (!endedSession) {
                            certaintyMeterText.setVisibility(View.VISIBLE);
                            certaintyMeterButtons.setVisibility(View.VISIBLE);
                        }
                        break;
                    case 1: //free mode
                        if (!endedSession) freeModeNextCardButton.setVisibility(View.VISIBLE);
                        else finishLearningSessionButton.setVisibility(View.VISIBLE);
                        break;
                }
            }
        }
    }

    //DINAMICALLY CREATE THE CARD CONTENT
    private View createContentView(CardPart part) {
        switch (part.getType()) {
            case TEXT:

                WebView webView = new WebView(getContext());
                WebSettings webSettings = webView.getSettings();
                setWebViewListener(webView);

                webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.getSettings().setDomStorageEnabled(false);
                webView.getSettings().setDefaultTextEncodingName("utf-8");

                webView.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                ));

                String content = HTMLStyler.styleHTML(part.getCardContent().getContent());
                webView.loadDataWithBaseURL(null, content, "text/html", "utf-8", null);

                return webView;

            case IMAGE:
                ImageView imageView = new ImageView(getContext());
                imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setAdjustViewBounds(true);

                try {
                    int mediaId = Integer.parseInt(part.getCardContent().getMedia_id());

                    mediaViewModel.getMedia(mediaId).observe(getViewLifecycleOwner(), media -> {
                        Glide.with(requireContext())
                                .load(media.getPath())
                                .placeholder(R.drawable.ic_launcher_background)
                                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                                .into(imageView);
                    });
                } catch (Exception e) {
                    Log.d("MEDIA", "Error loading image: " + e.getMessage());
                }

                return imageView;

            case FORMULA:
                JLatexMathDrawable drawable = JLatexMathDrawable.builder(part.getCardContent().getContent())
                        .textSize(70)
                        .padding(8)
                        .background(0xFFffffff)
                        .align(JLatexMathDrawable.ALIGN_CENTER)
                        .build();

                View formulaView = new View(getContext());
                formulaView.setBackground(drawable);
                return formulaView;

            default:
                Toast.makeText(getContext(), "Unknown card part type!", Toast.LENGTH_SHORT).show();
                return null;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void attachListener() {
        flashCardContent.setOnClickListener(v -> {
            if (currentCardIndex == cards.size() - 1 && activityType == ActivityType.FREE_MODE) {
                endedSession = true;
            }
            flipCard();
        });

        finishLearningSessionButton.setOnClickListener(v -> {
            onFinishLearningSession();
        });

        freeModeNextCardButton.setOnClickListener(v -> {
            if (currentCardIndex < cards.size() - 1) {
                currentCardIndex++;
                flipCard();
            }
        });

        scrollView.setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);
            return false;
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setWebViewListener(WebView webView) {
        webView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                flashCardContent.callOnClick();
            }
            return true;
        });
    }

    private void setSideLabel() {
        cardSideTextView.setText(isFront ? frontLabel : backLabel);
    }

    private void initSMButtons(View view) {
        ArrayList<Integer> colors = new ArrayList<>(
                Arrays.asList(Color.BLACK,
                        Color.RED,
                        Color.parseColor("#FF6200"),
                        Color.parseColor("#8AACE8"),
                        Color.parseColor("#00FF1E"),
                        Color.parseColor("#168207")));

        for (int i = 0; i < 6; i++) { //0-5 certainty
            Button button = view.findViewWithTag("SM" + i);
            int finalI = i;

            //BUTTON BORDER COLOR INIT
            GradientDrawable drawable = (GradientDrawable) ContextCompat.getDrawable(view.getContext(), R.drawable.sm_button);
            if (drawable != null) {
                drawable.setStroke(4, colors.get(finalI));
                button.setBackground(drawable);
            }

            //LISTENER TO CLICK
            button.setOnClickListener(v -> {
                int spentSeconds = timer.getElapsedTime() - currentSecondsWhenDisplayedNewCard;
                currentSecondsWhenDisplayedNewCard = 0;

                switch (mode) {
                    case 0: //free mode with rating
                        saveUserRating(finalI, spentSeconds);
                        break;
                    case 2: //algorithm mode
                        calcNextRepetition(finalI, spentSeconds);
                        break;
                }

                //IF WE HAVE MORE CARDS IN THE PACK - DISPLAY NEXT CARD
                if (currentCardIndex < cards.size() - 1) {
                    currentCardIndex++;
                    isFront = true;
                    displayCard();
                    setSideLabel();
                } else { //SHOW FINISH LEARNING SESSION BUTTON
                    endedSession = true;
                    finishLearningSessionButton.setVisibility(View.VISIBLE);
                    certaintyMeterText.setVisibility(View.GONE);
                    certaintyMeterButtons.setVisibility(View.GONE);
                }

            });
        }
    }

    //CALCULATE NEXT REPETITION - SM2 ALGORITHM
    private void calcNextRepetition(int certainty, int spentSeconds) {
        Card card = cards.get(currentCardIndex).card;
        sm2.setValues(certainty, card.repetitionsCount, card.easeFactor, card.SMinterval);
        Map<String, Object> smResults = sm2.calculateNewInterval();

        card.repetitionsCount = (int) smResults.get("n");
        card.SMinterval = (int) smResults.get("I");
        card.easeFactor = (double) smResults.get("EF");
        card.nextReviewDate = sm2.calculateNextReviewDate(card.SMinterval);
        card.lastReviewDate = new Date();
        card.timeSpentInMinutes = spentSeconds;

        cardViewModel.update(card);
        taskHelper.saveOrUpdateTask(card);
    }

    private void saveUserRating(int rating, int timeInSec) {
        SMRatings.add(rating);
    }

    private void onFinishLearningSession() {
        int totalSpentTime = timer.stop();

        Bundle bundle = new Bundle();
        bundle.putInt("totalSpentTime", totalSpentTime);
        bundle.putLong("packId", packId);
        bundle.putString("packageName", packName);
        bundle.putString("categoryName", categoryName);
        bundle.putSerializable("mode", activityType);

        if(mode == 1) bundle.putDouble("successRate", ((double) (currentCardIndex + 1)/cards.size()) *100);
        else bundle.putDouble("successRate", SuccessRateCalculator.calculateSuccessRate(SMRatings));

        bundle.putString("reviewedCards", (currentCardIndex + 1) + "/" + String.valueOf(cards.size()));

        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        Fragment fragment = new LearningSessionOverviewFragment();
        fragment.setArguments(bundle);

        fragmentManager.popBackStack(); // remove current fragment
        getParentFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, fragment)
                .addToBackStack(null)
                .commit();

    }

    private String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) {
            return "";
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    private void flipCard() {
        /*flayout.post(() -> {
            flashCardContent.setPivotX(flashCardContent.getWidth() / 2f);
            flashCardContent.setPivotY(flashCardContent.getHeight() / 2f);
        });

        flayout.animate()
                .rotationY(90f)
                .setDuration(50)
                .withEndAction(() -> {
                    isFront = !isFront;
                    displayCard();

                    flayout.setRotationY(-90f);
                    flayout.animate()
                            .rotationY(0f)
                            .setDuration(300)
                            .start();
                })
                .start();*/

        int slideType = isFront ? Gravity.START : Gravity.END;
        Slide slide = new Slide(slideType);
        slide.setDuration(300);
        TransitionManager.beginDelayedTransition(dynamicContentLayout, slide);

        isFront = !isFront;
        displayCard();
        setSideLabel();
        /*Fade fade = new Fade();
        fade.setDuration(300);
        TransitionManager.beginDelayedTransition(flayout, fade);

        isFront = !isFront;
        displayCard();
        setSideLabel();*/
    }

}