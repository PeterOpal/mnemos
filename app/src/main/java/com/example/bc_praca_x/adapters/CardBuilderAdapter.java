package com.example.bc_praca_x.adapters;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bc_praca_x.EditorActivity;
import com.example.bc_praca_x.FormulaEditor;
import com.example.bc_praca_x.R;
import com.example.bc_praca_x.SelectAppImageActivity;
import com.example.bc_praca_x.database.enums.CardType;
import com.example.bc_praca_x.models.CardBuilderCardItem;

import java.util.List;

public class CardBuilderAdapter extends RecyclerView.Adapter<CardBuilderAdapter.CardViewHolder> {
    private List<CardBuilderCardItem> cardList;
    private Activity activity; //reference to activity
    private ActivityResultLauncher<PickVisualMediaRequest> pickImageFromGalleryLauncher;

    public CardBuilderAdapter(List<CardBuilderCardItem> cardList, Activity activity, ActivityResultLauncher<PickVisualMediaRequest> pickImageFromGalleryLauncher) {
        this.cardList = cardList;
        this.activity = activity;
        this.pickImageFromGalleryLauncher = pickImageFromGalleryLauncher;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_builder_card_item, parent, false);
        return new CardViewHolder(view, activity);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        CardBuilderCardItem card = cardList.get(position);
        holder.bind(card, position);
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    @Override
    public long getItemId(int position) {
        CardBuilderCardItem item = cardList.get(position);
        return item.id != null ? item.id : item.hashCode();
    }

    public void updateBlockContent(int position, int blockPosition, String newContent, boolean isFront) {
        cardList.get(position).getBlocks(isFront).get(blockPosition).content = newContent;
        notifyItemChanged(position);
    }

    public void deleteCardAt(int position, Runnable onAdapterReset) {
        if (position < 0 || position >= cardList.size()) return;

        cardList.remove(position);

        if (cardList.isEmpty()) {
            Log.d("AdapterDebug", "All cards deleted. Adding a blank card.");
            cardList.add(new CardBuilderCardItem());
        }

        if (onAdapterReset != null) onAdapterReset.run();
    }

    public class CardViewHolder extends RecyclerView.ViewHolder {
        private RecyclerView frontRecyclerView, backRecyclerView;
        private TextView frontLabelText, backLabelText;
        private CardBlockAdapter frontAdapter, backAdapter;
        private Button frontTextButton, frontImageButton, frontFormulaButton;
        private Button backTextButton, backImageButton, backFormulaButton;
        public Activity activity;
        private String frontText, backText;

        //POZNAMKA: pocas testovanie sme zistili ze v niektorych zariadeniach nefungoval spravne, chybu sme zistili ze ViewPager2 treba optimalizovat, ale riesenie nechali sme bez zoradenie obsahu
        private void attachItemTouchHelper(RecyclerView recyclerView, CardBlockAdapter adapter) {
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                    ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END,
                    0) {
                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                    int fromPosition = viewHolder.getAdapterPosition();
                    int toPosition = target.getAdapterPosition();

                    adapter.moveItem(fromPosition, toPosition);
                    return true;
                }


                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {}
            });

            itemTouchHelper.attachToRecyclerView(recyclerView);
        }

        public CardViewHolder(View itemView, Activity activity) {
            super(itemView);
            this.activity = activity;
            frontRecyclerView = itemView.findViewById(R.id.frontRecyclerView);
            backRecyclerView = itemView.findViewById(R.id.backRecyclerView);

            frontTextButton = itemView.findViewById(R.id.button);
            frontImageButton = itemView.findViewById(R.id.button2);
            frontFormulaButton = itemView.findViewById(R.id.button3);

            backTextButton = itemView.findViewById(R.id.button4);
            backImageButton = itemView.findViewById(R.id.button5);
            backFormulaButton = itemView.findViewById(R.id.button7);

            frontLabelText = itemView.findViewById(R.id.frontLabelText);
            backLabelText = itemView.findViewById(R.id.backLabelText);

            frontText = activity.getString(R.string.Front);
            backText = activity.getString(R.string.Back);
        }


        public void bind(CardBuilderCardItem card, int position) {
            frontTextButton.setVisibility(View.VISIBLE);
            frontImageButton.setVisibility(View.VISIBLE);
            frontFormulaButton.setVisibility(View.VISIBLE);

            backTextButton.setVisibility(View.VISIBLE);
            backImageButton.setVisibility(View.VISIBLE);
            backFormulaButton.setVisibility(View.VISIBLE);

            for (CardBuilderCardItem.Block block : card.getBlocks(true)) {
                if (block.type == CardType.TEXT) {
                    frontTextButton.setVisibility(View.GONE);
                }
                if (block.type == CardType.IMAGE) {
                    frontImageButton.setVisibility(View.GONE);
                }
                if (block.type == CardType.FORMULA) {
                    frontFormulaButton.setVisibility(View.GONE);
                }
            }

            for (CardBuilderCardItem.Block block : card.getBlocks(false)) {
                if (block.type == CardType.TEXT) {
                    backTextButton.setVisibility(View.GONE);
                }
                if (block.type == CardType.IMAGE) {
                    backImageButton.setVisibility(View.GONE);
                }
                if (block.type == CardType.FORMULA) {
                    backFormulaButton.setVisibility(View.GONE);
                }
            }

            frontAdapter = new CardBlockAdapter(
                    card.getBlocks(true),
                    new CardBlockAdapter.OnTextClickListener() {
                        @Override
                        public void onTextClick(String textContent, int cardPosition) {
                            Intent intent = new Intent(activity, EditorActivity.class);
                            intent.putExtra("textContent", textContent);
                            intent.putExtra("blockPosition", cardPosition);
                            intent.putExtra("position", position);
                            intent.putExtra("isFront", true);
                            activity.startActivityForResult(intent, 1);
                        }
                    },
                    new CardBlockAdapter.OnFormulaClickListener() {
                        @Override
                        public void onFormulaClick(String formulaContent, int cardPosition) {
                            Intent intent = new Intent(activity, FormulaEditor.class);
                            intent.putExtra("formulaContent", formulaContent);
                            intent.putExtra("blockPosition", cardPosition);
                            intent.putExtra("position", position);
                            intent.putExtra("isFront", true);
                            activity.startActivityForResult(intent, 2);
                        }
                    },
                    new CardBlockAdapter.OnImageDelete() {
                        @Override
                        public void onImageDelete(int position) {
                            card.removeBlock(CardType.IMAGE, true);
                            frontImageButton.setVisibility(View.VISIBLE);
                            frontAdapter.notifyDataSetChanged();
                        }
                    },
                    new CardBlockAdapter.OnFormulaDelete() {
                        @Override
                        public void onFormulaDelete(int position) {
                            card.removeBlock(CardType.FORMULA, true);
                            frontFormulaButton.setVisibility(View.VISIBLE);
                            frontAdapter.notifyDataSetChanged();
                        }
                    },
                    new CardBlockAdapter.OnTextDelete() {
                        @Override
                        public void onTextDelete(int position) {
                            card.removeBlock(CardType.TEXT, true);
                            frontTextButton.setVisibility(View.VISIBLE);
                            frontAdapter.notifyDataSetChanged();
                        }
                    },
                    pickImageFromGalleryLauncher,
                    new CardBlockAdapter.OnAppImageClick() {
                        @Override
                        public void onAppImageClick(int cardPosition) {
                            Intent intent = new Intent(activity, SelectAppImageActivity.class);
                            intent.putExtra("blockPosition", cardPosition);
                            intent.putExtra("position", position);
                            intent.putExtra("isFront", true);
                            activity.startActivityForResult(intent, 99);
                        }
                    }
            );


            backAdapter = new CardBlockAdapter(card.getBlocks(false), new CardBlockAdapter.OnTextClickListener() {
                @Override
                public void onTextClick(String textContent, int cardPosition) {

                    Intent intent = new Intent(activity, EditorActivity.class);
                    intent.putExtra("textContent", textContent);
                    intent.putExtra("blockPosition", cardPosition);
                    intent.putExtra("position", position);
                    intent.putExtra("isFront", false);
                    activity.startActivityForResult(intent, 1);

                }
            }, new CardBlockAdapter.OnFormulaClickListener() {
                @Override
                public void onFormulaClick(String formulaContent, int cardPosition) {
                    Intent intent = new Intent(activity, FormulaEditor.class);
                    intent.putExtra("formulaContent", formulaContent);
                    intent.putExtra("blockPosition", cardPosition);
                    intent.putExtra("position", position);
                    intent.putExtra("isFront", false);
                    activity.startActivityForResult(intent, 2);
                }
            }, new CardBlockAdapter.OnImageDelete() {
                @Override
                public void onImageDelete(int position) {
                    card.removeBlock(CardType.IMAGE, false);
                    backImageButton.setVisibility(View.VISIBLE);
                    backAdapter.notifyDataSetChanged();
                }
            }, new CardBlockAdapter.OnFormulaDelete() {
                @Override
                public void onFormulaDelete(int position) {
                    card.removeBlock(CardType.FORMULA, false);
                    backFormulaButton.setVisibility(View.VISIBLE);
                    backAdapter.notifyDataSetChanged();
                }
            }, new CardBlockAdapter.OnTextDelete() {
                @Override
                public void onTextDelete(int position) {
                    card.removeBlock(CardType.TEXT, false);
                    backTextButton.setVisibility(View.VISIBLE);
                    backAdapter.notifyDataSetChanged();
                }
            }, pickImageFromGalleryLauncher,
               new CardBlockAdapter.OnAppImageClick() {
                @Override
                public void onAppImageClick(int cardPosition) {
                    Intent intent = new Intent(activity, SelectAppImageActivity.class);
                    intent.putExtra("blockPosition", cardPosition);
                    intent.putExtra("position", position);
                    intent.putExtra("isFront", false);
                    activity.startActivityForResult(intent, 99);
                }
            });

            frontLabelText.setText(frontText + " " + (position + 1));
            backLabelText.setText(backText + " " + (position + 1));

            frontRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            frontRecyclerView.setAdapter(frontAdapter);

            backRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            backRecyclerView.setAdapter(backAdapter);

            // POZNAMKA: VYSSIE JE KOMMENTAR
            //attachItemTouchHelper(frontRecyclerView, frontAdapter);
            //attachItemTouchHelper(backRecyclerView, backAdapter);

            frontTextButton.setOnClickListener(v -> addBlock(card, CardType.TEXT, "", true, frontTextButton));
            frontImageButton.setOnClickListener(v -> addBlock(card, CardType.IMAGE, "", true, frontImageButton));
            frontFormulaButton.setOnClickListener(v -> addBlock(card, CardType.FORMULA, "", true, frontFormulaButton));

            backTextButton.setOnClickListener(v -> addBlock(card, CardType.TEXT, "", false, backTextButton));
            backImageButton.setOnClickListener(v -> addBlock(card, CardType.IMAGE, "", false, backImageButton));
            backFormulaButton.setOnClickListener(v -> addBlock(card, CardType.FORMULA, "", false, backFormulaButton));

        }

        private void addBlock(CardBuilderCardItem card, CardType type, String content, boolean isFront, Button button) {
            if (card.canAddBlock(type, isFront)) {
                card.addBlock(type, content, isFront);
                (isFront ? frontAdapter : backAdapter).notifyDataSetChanged();
                button.setVisibility(View.GONE); // we hide the button
            }
        }
    }
}