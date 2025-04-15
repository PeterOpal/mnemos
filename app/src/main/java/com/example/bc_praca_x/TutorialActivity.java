package com.example.bc_praca_x;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.bc_praca_x.database.entity.Card;
import com.example.bc_praca_x.database.entity.CardContent;
import com.example.bc_praca_x.database.entity.CardPack;
import com.example.bc_praca_x.database.entity.CardPart;
import com.example.bc_praca_x.database.entity.Category;
import com.example.bc_praca_x.database.enums.CardSide;
import com.example.bc_praca_x.database.enums.CardType;
import com.example.bc_praca_x.database.repository.CardRepository;
import com.example.bc_praca_x.database.viewmodel.CardPackViewModal;
import com.example.bc_praca_x.database.viewmodel.CardPartViewModel;
import com.example.bc_praca_x.database.viewmodel.CardViewModel;
import com.example.bc_praca_x.database.viewmodel.CategoryViewModel;
import com.example.bc_praca_x.database.viewmodel.MediaViewModel;
import com.example.bc_praca_x.helpers.ImageSaver;
import com.example.bc_praca_x.helpers.ItemInsertedToDB;
import com.example.bc_praca_x.models.CardBuilderCardItem;
import com.example.bc_praca_x.user_tutorial.TutorialViewModel;
import com.example.bc_praca_x.user_tutorial.UserTutorialPagerAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.tinkoff.scrollingpagerindicator.ScrollingPagerIndicator;

public class TutorialActivity extends ActivitySetup {

    private Button prevButton, nextButton;
    private ViewPager2 viewPager2;
    private TutorialViewModel tutorialViewModel;
    private MediaViewModel mediaViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        tutorialViewModel = new ViewModelProvider(this).get(TutorialViewModel.class);

        prevButton = findViewById(R.id.prevTutorial);
        nextButton = findViewById(R.id.nextTutorial);
        addButtonListener();

        viewPager2 = findViewById(R.id.viewPager);
        ScrollingPagerIndicator indicator = findViewById(R.id.tutorialPager);

        UserTutorialPagerAdapter adapter = new UserTutorialPagerAdapter(this);
        viewPager2.setAdapter(adapter);

        indicator.attachToPager(viewPager2);

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                changeButtonVisibility(position);
            }
        });
    }

    private void changeButtonVisibility(int position) {
        switch (position) {
            case 0:
                prevButton.setVisibility(Button.GONE);
                nextButton.setVisibility(Button.VISIBLE);
                nextButton.setText(getString(R.string.next));
                break;
            case 1:
                prevButton.setVisibility(Button.VISIBLE);
                nextButton.setVisibility(Button.VISIBLE);
                nextButton.setText(getString(R.string.next));
                break;
            case 2:
                prevButton.setVisibility(Button.VISIBLE);
                nextButton.setVisibility(Button.VISIBLE);
                nextButton.setText(getString(R.string.finish));
                break;
        }
    }

    private void addButtonListener() {
        prevButton.setOnClickListener(v -> {
            changePage("prev");
        });

        nextButton.setOnClickListener(v -> {
            changePage("next");
        });
    }

    private void changePage(String buttonType) {
        int currentItem = viewPager2.getCurrentItem();

        if (buttonType.equals("prev") && currentItem > 0) {
            viewPager2.setCurrentItem(currentItem - 1);
        } else if (buttonType.equals("next") && currentItem < 2) {
            viewPager2.setCurrentItem(currentItem + 1);
        } else if (buttonType.equals("next") && currentItem == 2) {

            // vzorovy obsah
            Boolean addSampleContent = tutorialViewModel.getAddSampleContent().getValue();
            addSampleContent = (addSampleContent != null) ? addSampleContent : false;
            if (addSampleContent) addSampleContent();

            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    private void addSampleContent() {
        String categories[] = new String[]{getString(R.string.English), getString(R.string.Math)};
        int mediaResIds[] = new int[]{R.drawable.english, R.drawable.math};

        CategoryViewModel categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        mediaViewModel = new ViewModelProvider(this).get(MediaViewModel.class);

        for (int i = 0; i < categories.length; i++) {
            final int index = i;
            Category category = new Category(categories[index], "", null);

            ImageSaver imageSaver = new ImageSaver(this);
            imageSaver.saveDrawable(mediaResIds[index], new ImageSaver.ImageSaveCallback() {
                @Override
                public void onImageSaved(String path) {
                    mediaViewModel.insertUserMedia(path, mediaId -> {
                        category.setMedia_id(mediaId);

                        categoryViewModel.insertAndGetId(category, new ItemInsertedToDB() {
                            @Override
                            public void onItemInserted(long id) {
                                addCardPackAndContent(id, index);
                            }
                        });
                    });
                }

                @Override
                public void onError(Exception e) {
                    Log.d("IMAGE", "onError: " + e.getMessage());
                }
            });
        }

    }

    private void addCardPackAndContent(long categoryId, int arrayId) {
        CardPackViewModal cardPackViewModal = new ViewModelProvider(this).get(CardPackViewModal.class);
        CardViewModel cardViewModel = new ViewModelProvider(this).get(CardViewModel.class);
        //(String name, long card_category_id)
        switch (arrayId) {
            case 0:
                CardPack cardPack_words = new CardPack(getString(R.string.words), categoryId);
                CardPack cardPack_animals = new CardPack(getString(R.string.animals), categoryId);

                cardPackViewModal.insertAndGetId(cardPack_words, new ItemInsertedToDB() {
                    @Override
                    public void onItemInserted(long id) {
                        final String frontText[] = {"What is a word for a place you live in?", "What is a word for an object that tells time?", "What is a word for something you eat in the morning?", "What is a word for a person who teaches?", "What is a word for water falling from the sky?", "What is a word for a room where you cook?", "What is a word for a person who writes books?", "What is a word for the opposite of cold?"};
                        final String backText[] = {"home", "clock", "breakfast", "teacher", "rain", "kitchen", "author", "hot"};

                        for (int i = 0; i < frontText.length; i++) {
                            Card newCard = new Card(0, 2.5, 0, new Date(), null, new Date(), id, 1);
                            int finalI = i;
                            cardViewModel.insert(newCard, new CardRepository.OnCardInsertedListener() {
                                @Override
                                public void onCardInserted(long newCardId) {
                                    CardBuilderCardItem cardContent = new CardBuilderCardItem();
                                    cardContent.addBlock(CardType.TEXT, frontText[finalI], true);
                                    cardContent.addBlock(CardType.TEXT, backText[finalI], false);
                                    insertCardContent(cardContent, newCardId);
                                }
                            });
                        }
                    }
                });

                cardPackViewModal.insertAndGetId(cardPack_animals, new ItemInsertedToDB() {
                    @Override
                    public void onItemInserted(long id) {
                        final String frontText[] = {
                                "What is a large animal with a trunk?",
                                "Small animal that hops?",
                                "What is a large animal known for its mane?",
                                "",
                                "Lives in water and has fins?",
                                "black and orange stripes",
                                "animal that can change its color to blend in",
                                ""
                        };

                        final String backText[] = {
                                "elephant", "frog", "lion", "bird", "fish",
                                "tiger", "chameleon", "deer"
                        };

                        final int mediaResIds[] = {
                                R.drawable.elephant, R.drawable.frog, R.drawable.lion, R.drawable.bird, R.drawable.fish, 0, 0, R.drawable.deer
                        };


                        for (int i = 0; i < frontText.length; i++) {
                            Card newCard = new Card(0, 2.5, 0, new Date(), null, new Date(), id, 1);
                            int finalI = i;
                            cardViewModel.insert(newCard, new CardRepository.OnCardInsertedListener() {
                                @Override
                                public void onCardInserted(long newCardId) {
                                    CardBuilderCardItem cardContent = new CardBuilderCardItem();
                                    if (!frontText[finalI].isEmpty()) {
                                        cardContent.addBlock(CardType.TEXT, frontText[finalI], true);
                                    }

                                    if (mediaResIds[finalI] != 0) {
                                        cardContent.addBlock(CardType.IMAGE, String.valueOf(mediaResIds[finalI]), true);
                                    }

                                    cardContent.addBlock(CardType.TEXT, backText[finalI], false);
                                    insertCardContent(cardContent, newCardId);
                                }
                            });
                        }
                    }
                });

                break;
            case 1:
                CardPack cardPack_logarithm = new CardPack(getString(R.string.logarithm), categoryId);
                cardPackViewModal.insertAndGetId(cardPack_logarithm, new ItemInsertedToDB() {
                            @Override
                            public void onItemInserted(long id) {
                                final String frontText[] = {getString(R.string.sample_content_math_front_1), getString(R.string.sample_content_math_front_2), getString(R.string.sample_content_math_front_3), getString(R.string.sample_content_math_front_4), getString(R.string.sample_content_math_front_5), getString(R.string.sample_content_math_front_6)};
                                final String frontFormula[] = {"\\( \\log_{10} 1000 \\)", "\\( \\log_2 8 = 3 \\)", "\\( y = \\log_a x \\)", "\\( \\log_5 125 \\)", "", ""};
                                final String backText[] = {"", getString(R.string.sample_content_math_back_2), "", "", getString(R.string.sample_content_math_back_5),""};
                                final String backFormula[] = {"\\( \\log_{10} 1000 = \\log_{10} (10^3) = 3 \\)", "\\( \\log_2 8 = 3 \\Rightarrow 2^3 = 8 \\)", "\\( y = \\log_a x \\Rightarrow x = a^y \\)", "\\( 125 = 5^3 \\Rightarrow \\log_5 125 = 3 \\)", "",
                                        "log_a \\left( \\frac{x}{y} \\right) = \\log_a x - \\log_a y"};

                                for (int i = 0; i < frontText.length; i++) {
                                    Card newCard = new Card(0, 2.5, 0, new Date(), null, new Date(), id, 1);
                                    int finalI = i;
                                    cardViewModel.insert(newCard, new CardRepository.OnCardInsertedListener() {
                                        @Override
                                        public void onCardInserted(long newCardId) {
                                            CardBuilderCardItem cardContent = new CardBuilderCardItem();

                                            if(!frontText[finalI].isEmpty()) {
                                                cardContent.addBlock(CardType.TEXT, frontText[finalI], true);
                                            }

                                            if(!frontFormula[finalI].isEmpty()) {
                                                cardContent.addBlock(CardType.FORMULA, frontFormula[finalI], true);
                                            }

                                            if(!backText[finalI].isEmpty()) {
                                                cardContent.addBlock(CardType.TEXT, backText[finalI], false);
                                            }

                                            if(!backFormula[finalI].isEmpty()) {
                                                cardContent.addBlock(CardType.FORMULA, backFormula[finalI], false);
                                            }
                                            insertCardContent(cardContent, newCardId);
                                        }
                                    });
                                }
                            }
                        });

                break;
        }
    }

    public void insertCardContent(CardBuilderCardItem cardBuilderCardItem, long newCardId) {
        CardPartViewModel cardPartViewModel = new ViewModelProvider(this).get(CardPartViewModel.class);
        for (CardBuilderCardItem.Block block : cardBuilderCardItem.getBlocks(true)) {
            setCardContent(block, cardContent -> {
                CardPart cardPart = new CardPart(CardSide.FRONT, block.type, cardContent, cardBuilderCardItem.getBlocks(true).indexOf(block), newCardId);
                cardPartViewModel.insert(cardPart);
            });
        }

        for (CardBuilderCardItem.Block block : cardBuilderCardItem.getBlocks(false)) {
            setCardContent(block, cardContent -> {
                CardPart cardPart = new CardPart(CardSide.BACK, block.type, cardContent, cardBuilderCardItem.getBlocks(false).indexOf(block), newCardId);
                cardPartViewModel.insert(cardPart);
            });
        }
    }

    private void setCardContent(CardBuilderCardItem.Block block, CardBuilderActivity.OnCardContentCreated callback) {
        if (block.type == CardType.IMAGE) {
            int drawableId = Integer.parseInt(block.content);
            saveBlockImage(drawableId, callback);
        } else {
            CardContent cardContent = new CardContent();
            cardContent.setContent(block.content);
            callback.onCardContentCreated(cardContent);
        }
    }

    private void saveBlockImage(int drawableId, CardBuilderActivity.OnCardContentCreated callback) {
        runOnUiThread(() -> {
            ImageSaver imageSaver = new ImageSaver(this);
            imageSaver.saveDrawable(drawableId, new ImageSaver.ImageSaveCallback() {
                @Override
                public void onImageSaved(String savedPath) {
                    runOnUiThread(() -> {
                        mediaViewModel.insertUserMedia(savedPath, mediaId -> {
                            CardContent cardContent = new CardContent();
                            cardContent.setMedia_id(String.valueOf(mediaId));
                            callback.onCardContentCreated(cardContent);
                        });
                    });
                }

                @Override
                public void onError(Exception e) {
                    Log.d("error", "Error saving image", e);
                }
            });
        });
    }
}

