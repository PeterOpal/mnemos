package com.example.bc_praca_x;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.bc_praca_x.adapters.CardBlockAdapter;
import com.example.bc_praca_x.adapters.CardBuilderAdapter;
import com.example.bc_praca_x.database.POJO.CardWithParts;
import com.example.bc_praca_x.database.entity.Card;
import com.example.bc_praca_x.database.entity.CardContent;
import com.example.bc_praca_x.database.entity.CardPart;
import com.example.bc_praca_x.database.enums.CardSide;
import com.example.bc_praca_x.database.enums.CardType;
import com.example.bc_praca_x.database.repository.CardRepository;
import com.example.bc_praca_x.database.viewmodel.CardPartViewModel;
import com.example.bc_praca_x.database.viewmodel.CardViewModel;
import com.example.bc_praca_x.database.viewmodel.MediaViewModel;
import com.example.bc_praca_x.database.viewmodel.TaskViewModel;
import com.example.bc_praca_x.helpers.ImagePicker;
import com.example.bc_praca_x.helpers.ImagePickerFromGallery;
import com.example.bc_praca_x.helpers.ImageSaver;
import com.example.bc_praca_x.helpers.TaskHelper;
import com.example.bc_praca_x.models.CardBuilderCardItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.tinkoff.scrollingpagerindicator.ScrollingPagerIndicator;

public class CardBuilderActivity extends ActivitySetup {
    private ViewPager2 viewPager;
    private CardBuilderAdapter adapter;
    private List<CardBuilderCardItem> cardList; //CardBuilderCardItem obsahuje jednu karticku, ktora obsahuje rozne bloky, manazuje aj front a back stranu
    private Button saveButton;
    private ImageView deleteCardButton;
    private CardViewModel cardViewModel;
    private CardPartViewModel cardPartViewModel;
    private TaskViewModel taskViewModel;
    private MediaViewModel mediaViewModel;
    private long cardPackId;
    private CardBlockAdapter currentBlockAdapter;
    ActivityResultLauncher<PickVisualMediaRequest> pickImageFromGalleryLauncher;
    private String csvData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_builder);

        Intent intent = getIntent();
        csvData = intent.getStringExtra("csvData");

        //toolbar
        Toolbar toolbar = findViewById(R.id.addCardsTool);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> finish());

        viewPager = findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(1);
        ScrollingPagerIndicator scrollingPagerIndicator = findViewById(R.id.tutorialPager);
        saveButton = findViewById(R.id.saveButton);
        deleteCardButton = findViewById(R.id.deleteCardButton);
        cardViewModel = new CardViewModel(getApplication());
        cardPartViewModel = new CardPartViewModel(getApplication());
        mediaViewModel = new MediaViewModel(getApplication());
        taskViewModel = new TaskViewModel(getApplication());
        cardPackId = getIntent().getLongExtra("cardPackId", 0);
        boolean update = getIntent().getBooleanExtra("update", false);
        if (update) loadCardsIfUpdate(cardPackId);

        cardList = new ArrayList<>();

        if (csvData != null) {
            importCards(csvData);
        } else if (!update) {
            cardList.add(new CardBuilderCardItem());
        }
        pickImageFromGalleryLauncher = ImagePickerFromGallery.createImagePicker(this, new ImagePicker() {
            @Override
            public void onImagePicked(Uri imageUri) {
                currentBlockAdapter.updateImage(imageUri);
            }
        });

        adapter = new CardBuilderAdapter(cardList, this, pickImageFromGalleryLauncher);

        viewPager.setAdapter(adapter);
        scrollingPagerIndicator.attachToPager(viewPager);

        deleteCardButton.setOnClickListener(v -> {
            int position = viewPager.getCurrentItem();

            if (cardList.get(position).id != null) {
                TaskHelper taskHelper = new TaskHelper(taskViewModel);
                taskHelper.syncCardWithTask(cardList.get(position).id, cardViewModel, this);
                cardViewModel.delete(cardList.get(position).id);
            }

            adapter.deleteCardAt(position, () -> {
                int newPosition = Math.min(position, cardList.size() - 1);

                viewPager.setAdapter(null);
                adapter = new CardBuilderAdapter(cardList, this, pickImageFromGalleryLauncher);
                adapter.setHasStableIds(true);
                viewPager.setAdapter(adapter);

                viewPager.post(() -> {
                    viewPager.setCurrentItem(newPosition, false);
                    scrollingPagerIndicator.attachToPager(viewPager);
                });
            });
        });


        saveButton.setOnClickListener(v -> {
            if (cardList.get(cardList.size() - 1).isFrontEmpty()) {
                Toast.makeText(this, "Front side is empty", Toast.LENGTH_SHORT).show();
                viewPager.setCurrentItem(cardList.size(), true);
                return;
            } else if (cardList.get(cardList.size() - 1).isBackEmpty()) {
                Toast.makeText(this, "Back side is empty", Toast.LENGTH_SHORT).show();
                viewPager.setCurrentItem(cardList.size(), true);
                return;
            }

            int newPosition = cardList.size(); //new position
            cardList.add(new CardBuilderCardItem());


            adapter.notifyItemInserted(newPosition);
            scrollingPagerIndicator.post(() -> scrollingPagerIndicator.attachToPager(viewPager));

            viewPager.postDelayed(() -> {
                if (newPosition < adapter.getItemCount()) {
                    viewPager.setCurrentItem(newPosition, true);
                }
            }, 150); // delay
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            int position = data.getIntExtra("position", -1);
            int blockPosition = data.getIntExtra("blockPosition", -1);
            boolean isFront = data.getBooleanExtra("isFront", true);
            String updatedContent = data.getStringExtra("updatedContent");
            if (requestCode == 1) {
                if (blockPosition != -1 && position != -1) {
                    adapter.updateBlockContent(position, blockPosition, updatedContent, isFront);
                } else {
                    Log.d("error", "Block position is -1");
                }
            }

            if (requestCode == 2) {
                if (blockPosition != -1 && position != -1) {
                    Log.d("imageee", "image path: " + updatedContent);
                    adapter.updateBlockContent(position, blockPosition, updatedContent, isFront);
                } else {
                    Log.d("error", "Block position is -1");
                }
            }

            //IMAGE FROM APP
            if (requestCode == 99) {
                if (blockPosition != -1 && position != -1) {
                    adapter.updateBlockContent(position, blockPosition, updatedContent, isFront);
                } else {
                    Log.d("error", "Block position is -1");
                }
            }
        }
    }

    //menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_category_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save) {

            saveAllCards();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveAllCards() {
        //VALIDACIA NAJPRV
        if (!validateCards()) return;

        for (CardBuilderCardItem card : cardList) {
            //int timeSpentInMinutes, double easeFactor, int repetitionsCount, Date nextReviewDate, Date lastReviewDate, Date createdAt, long cardPackId, int interval

            //KED IDE O UPDATE
            if (card.id != null) {
                cardPartViewModel.deleteCardPartsForCard(card.id, CardSide.FRONT);
                for (CardBuilderCardItem.Block block : card.getBlocks(true)) {
                    setCardContent(block, cardContent -> {
                        CardPart cardPart = new CardPart(CardSide.FRONT, block.type, cardContent, card.getBlocks(true).indexOf(block), card.id);
                        cardPartViewModel.insert(cardPart);
                    });
                }

                cardPartViewModel.deleteCardPartsForCard(card.id, CardSide.BACK);

                for (CardBuilderCardItem.Block block : card.getBlocks(false)) {
                    setCardContent(block, cardContent -> {
                        CardPart cardPart = new CardPart(CardSide.BACK, block.type, cardContent, card.getBlocks(false).indexOf(block), card.id);
                        cardPartViewModel.insert(cardPart);
                    });
                }
            } else { //KED IDE O NOVU KARTICKU

                Card newCard = new Card(0, 2.5, 0, new Date(), null, new Date(), cardPackId, 1);
                cardViewModel.insert(newCard, new CardRepository.OnCardInsertedListener() {
                    @Override
                    public void onCardInserted(long newCardId) {

                        for (CardBuilderCardItem.Block block : card.getBlocks(true)) {
                            setCardContent(block, cardContent -> {
                                CardPart cardPart = new CardPart(CardSide.FRONT, block.type, cardContent, card.getBlocks(true).indexOf(block), newCardId);
                                cardPartViewModel.insert(cardPart);
                            });
                        }

                        for (CardBuilderCardItem.Block block : card.getBlocks(false)) {
                            setCardContent(block, cardContent -> {
                                CardPart cardPart = new CardPart(CardSide.BACK, block.type, cardContent, card.getBlocks(false).indexOf(block), newCardId);
                                cardPartViewModel.insert(cardPart);
                            });
                        }
                    }
                });
            }
        }

        Intent resultIntent = new Intent();
        resultIntent.putExtra("cardPackId", 0);

        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void setCardContent(CardBuilderCardItem.Block block, OnCardContentCreated callback) { //convert value from block to cardContent to handle DB saving operation - JSON type
        if (block.type == CardType.IMAGE) {
            saveBlockImage(block.content, callback);
        } else {
            CardContent cardContent = new CardContent();
            cardContent.setContent(block.content);
            callback.onCardContentCreated(cardContent);
        }
    }

    private void saveBlockImage(String path, OnCardContentCreated callback) {
        if (path == null || path.isEmpty()) {
            Log.d("error", "Invalid image path");
            return;
        }

        runOnUiThread(() -> {
            // CHECK IF EXISTING MEDIA
            mediaViewModel.existingMedia(path).observe(this, media -> {
                if (media != null) {
                    CardContent cardContent = new CardContent();
                    cardContent.setMedia_id(String.valueOf(media.id));
                    callback.onCardContentCreated(cardContent);
                } else {
                    // SAVE IF NEW IMAGE
                    ImageSaver imageSaver = new ImageSaver(this);
                    imageSaver.saveImage(Uri.parse(path), new ImageSaver.ImageSaveCallback() {
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
                }
            });
        });
    }

    public void setCurrentBlockAdapter(CardBlockAdapter adapter) {
        this.currentBlockAdapter = adapter;
    }

    public interface OnCardContentCreated {
        void onCardContentCreated(CardContent cardContent);
    }

    private void loadCardsIfUpdate(long packId) {
        cardViewModel.getCardsWithPartsByPackId(packId, false)
                .observe(this, cardsWithParts -> {

                    List<CardBuilderCardItem> loadedCards = new ArrayList<>();

                    for (CardWithParts cardWithParts : cardsWithParts) {

                        CardBuilderCardItem card = new CardBuilderCardItem(cardWithParts.card.id);

                        List<CardBuilderCardItem.Block> frontBlocks = new ArrayList<>();
                        List<CardBuilderCardItem.Block> backBlocks = new ArrayList<>();

                        for (CardPart cardPart : cardWithParts.getSortedCardParts()) {
                            if (cardPart.getSide() == CardSide.FRONT) {
                                switch (cardPart.getType()) {
                                    case IMAGE:
                                        frontBlocks.add(new CardBuilderCardItem.Block(cardPart.getType(), "", cardPart.getId()));
                                        int finalIndex = frontBlocks.size() - 1;

                                        mediaViewModel.getMedia(Integer.parseInt(cardPart.getCardContent().getMedia_id())).observe(this, media -> {
                                            String imagePath = media != null ? media.path : null;
                                            frontBlocks.get(finalIndex).content = imagePath;
                                            adapter.notifyDataSetChanged();
                                        });
                                        break;
                                    default:
                                        frontBlocks.add(new CardBuilderCardItem.Block(cardPart.getType(), cardPart.getCardContent().getContent(), cardPart.getId()));
                                }
                            } else {
                                switch (cardPart.getType()) {
                                    case IMAGE:
                                        backBlocks.add(new CardBuilderCardItem.Block(cardPart.getType(), "", cardPart.getId()));
                                        int finalIndexB = backBlocks.size() - 1;
                                        mediaViewModel.getMedia(Integer.parseInt(cardPart.getCardContent().getMedia_id())).observe(this, media -> {
                                            String imagePath = media != null ? media.path : null;
                                            backBlocks.get(finalIndexB).content = imagePath;
                                            adapter.notifyDataSetChanged();
                                        });
                                        break;
                                    default:
                                        backBlocks.add(new CardBuilderCardItem.Block(cardPart.getType(), cardPart.getCardContent().getContent(), cardPart.getId()));
                                }
                            }
                        }
                        card.setFrontBlocks(frontBlocks);
                        card.setBackBlocks(backBlocks);

                        loadedCards.add(card);
                    }
                    cardList.clear();
                    cardList.addAll(loadedCards);
                    if (cardList.isEmpty()) cardList.add(new CardBuilderCardItem());

                    adapter.notifyDataSetChanged();
                });
    }

    private void importCards(String csvData) {
        String[] lines = csvData.split("\n");
        for (String line : lines) {
            String[] columns = line.split(",");

            if (columns.length >= 2) {
                String firstCol = columns[0].trim();
                String secondCol = columns[1].trim();
                CardBuilderCardItem card = new CardBuilderCardItem();

                List<CardBuilderCardItem.Block> frontBlocks = new ArrayList<>();
                frontBlocks.add(new CardBuilderCardItem.Block(CardType.TEXT, firstCol, null));

                List<CardBuilderCardItem.Block> backBlocks = new ArrayList<>();
                backBlocks.add(new CardBuilderCardItem.Block(CardType.TEXT, secondCol, null));

                card.setFrontBlocks(frontBlocks);
                card.setBackBlocks(backBlocks);

                cardList.add(card);
            }
        }
    }

    //SYNC pocas testovani nefungoval spravne, takze je zakomentovany a riesene cez delete a pridanie znova zaznamov
    /*private void syncCardPartsWithUI(CardBuilderCardItem card, CardSide side, List<CardType> uiTypes) {
        Observer<List<CardPart>> observer = new Observer<List<CardPart>>() {
            @Override
            public void onChanged(List<CardPart> dbParts) {

                for (CardPart dbPart : dbParts) {
                    if (!uiTypes.contains(dbPart.getType())) {
                        cardPartViewModel.delete(dbPart);
                    }
                }
                cardPartViewModel.getCardPartsForCard(card.id, side).removeObserver(this);
            }
        };
        cardPartViewModel.getCardPartsForCard(card.id, side).observeForever(observer);
    }*/

    private boolean validateCards() {
        for (int i = 0; i < cardList.size(); i++) {
            CardBuilderCardItem card = cardList.get(i);
            List<CardBuilderCardItem.Block> frontBlocks = card.getBlocks(true);
            List<CardBuilderCardItem.Block> backBlocks = card.getBlocks(false);

            if (frontBlocks.isEmpty()) {
                viewPager.setCurrentItem(i, true);
                Toast.makeText(this, getString(R.string.Card) + " " + (i + 1) + getString(R.string.front_part_empty), Toast.LENGTH_SHORT).show();
                return false;
            }

            for (CardBuilderCardItem.Block block : frontBlocks) {
                if (block.content == null || block.content.isEmpty()) {
                    String type = getBlockTypeName(block.type);
                    viewPager.setCurrentItem(i, true);
                    Toast.makeText(this, getString(R.string.Card) + " " + (i + 1) + ": " + type + getString(R.string.front_part_empty), Toast.LENGTH_SHORT).show();
                    return false;
                }
            }

            if (backBlocks.isEmpty()) {
                viewPager.setCurrentItem(i, true);
                Toast.makeText(this, getString(R.string.Card) + " " + (i + 1) + getString(R.string.back_part_empty), Toast.LENGTH_SHORT).show();
                return false;
            }

            for (CardBuilderCardItem.Block block : backBlocks) {
                if (block.content == null || block.content.isEmpty()) {
                    viewPager.setCurrentItem(i, true);
                    String type = getBlockTypeName(block.type);
                    Toast.makeText(this, getString(R.string.Card) + " " + (i + 1) + ": " + type + getString(R.string.back_part_empty), Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }

        return true;
    }

    private String getBlockTypeName(CardType type) {
        switch (type) {
            case IMAGE: return getString(R.string.image_block);
            case TEXT: return getString(R.string.text_block);
            case FORMULA: return getString(R.string.formula_block);
            default: return "";
        }
    }



}


