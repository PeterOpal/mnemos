package com.example.bc_praca_x.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.bc_praca_x.CardBuilderActivity;
import com.example.bc_praca_x.R;
import com.example.bc_praca_x.database.enums.CardType;
import com.example.bc_praca_x.helpers.HTMLStyler;
import com.example.bc_praca_x.models.CardBuilderCardItem;
import java.util.List;
import ru.noties.jlatexmath.JLatexMathDrawable;

public class CardBlockAdapter extends RecyclerView.Adapter<CardBlockAdapter.BlockViewHolder> {
    private List<CardBuilderCardItem.Block> blocks;
    private OnTextClickListener textClickListener;
    private OnFormulaClickListener formulaClickListener;
    private OnTextDelete textDelete;
    private OnImageDelete imageDelete;
    private OnFormulaDelete formulaDelete;
    private OnAppImageClick appImageClick;
    private int currentBlockIndex = -1;
    private ActivityResultLauncher<PickVisualMediaRequest> pickImageFromGalleryLauncher;

    public interface OnTextClickListener {
        void onTextClick(String textContent, int position);
    }

    public interface OnFormulaClickListener {
        void onFormulaClick(String formulaContent, int position);
    }

    public interface OnImageDelete {
        void onImageDelete(int position);
    }

    public interface OnFormulaDelete {
        void onFormulaDelete(int position);
    }

    public interface OnTextDelete {
        void onTextDelete(int position);
    }

    public interface OnAppImageClick {
        void onAppImageClick(int position);
    }

    public CardBlockAdapter(List<CardBuilderCardItem.Block> blocks, OnTextClickListener listener, OnFormulaClickListener formulaListener, OnImageDelete imageDelete, OnFormulaDelete onFormulaDelete, OnTextDelete onTextDelete, ActivityResultLauncher<PickVisualMediaRequest> pickImageFromGalleryLauncher, OnAppImageClick appImageSelector) {
        this.blocks = blocks;
        this.textClickListener = listener;
        this.formulaClickListener = formulaListener;
        this.imageDelete = imageDelete;
        this.formulaDelete = onFormulaDelete;
        this.textDelete = onTextDelete;

        this.pickImageFromGalleryLauncher = pickImageFromGalleryLauncher;
        this.appImageClick = appImageSelector;
    }

    @NonNull
    @Override
    public BlockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_block_item, parent, false);
        return new BlockViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlockViewHolder holder, int position) {
        CardBuilderCardItem.Block block = blocks.get(position);
        holder.bind(block);

        if (block.type == CardType.TEXT) {
            holder.webView.setOnClickListener(v -> {
                if (textClickListener != null) {
                    int blockIndex = holder.getAdapterPosition();
                    if (blockIndex != RecyclerView.NO_POSITION) {
                        textClickListener.onTextClick(block.content, blockIndex);
                    }
                }
            });
        } else if (block.type == CardType.FORMULA) {
            holder.formulaBlock.setOnClickListener(v -> {
                if (formulaClickListener != null) {
                    int blockIndex = holder.getAdapterPosition();
                    if (blockIndex != RecyclerView.NO_POSITION) {
                        formulaClickListener.onFormulaClick(block.content, blockIndex);
                    }
                }
            });
        } else if (block.type == CardType.IMAGE) {
            holder.imageBlock.findViewById(R.id.appImageButton).setOnClickListener(v -> {
                appImageClick.onAppImageClick(position);
            });
        }
    }

    @Override
    public int getItemCount() {
        return blocks.size();
    }

    public void moveItem(int fromPosition, int toPosition) {
        if (fromPosition < blocks.size() && toPosition < blocks.size()) {
            CardBuilderCardItem.Block item = blocks.remove(fromPosition);
            blocks.add(toPosition, item);
            notifyItemMoved(fromPosition, toPosition);
        }
    }

    public class BlockViewHolder extends RecyclerView.ViewHolder {
        private WebView webView;
        private RelativeLayout imageBlock, formulaBlock, textBlock;

        public BlockViewHolder(View itemView) {
            super(itemView);

            formulaBlock = itemView.findViewById(R.id.formula_holder);
            webView = itemView.findViewById(R.id.block_webview);
            textBlock = itemView.findViewById(R.id.text_holder);
            imageBlock = itemView.findViewById(R.id.image_holder);
        }

        @SuppressLint({"ClickableViewAccessibility", "SetJavaScriptEnabled"})
        public void bind(CardBuilderCardItem.Block block) {
            if (block.type == CardType.TEXT) {
                textBlock.setVisibility(View.VISIBLE);
                imageBlock.setVisibility(View.GONE);
                formulaBlock.setVisibility(View.GONE);

                ImageView deleteImageButton = textBlock.findViewById(R.id.removeTextBlock);
                deleteImageButton.setOnClickListener(v -> {
                    if (textDelete != null) {
                        textDelete.onTextDelete(getAdapterPosition());
                    }
                });

                TextView tapToEditText = textBlock.findViewById(R.id.tapToEditOverlay);

                if(block.content != null  && !block.content.isEmpty()) {
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT
                    );
                    webView.setLayoutParams(params);
                    tapToEditText.setVisibility(View.GONE);
                } else {
                    Context context = webView.getContext();
                    float scale = context.getResources().getDisplayMetrics().density;
                    int heightInPx = (int) (40 * scale + 0.5f);

                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            heightInPx
                    );
                    webView.setLayoutParams(params);
                    tapToEditText.setVisibility(View.VISIBLE);
                }

                String htmlContent = HTMLStyler.styleHTML( block.content);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.getSettings().setDomStorageEnabled(true);
                webView.loadDataWithBaseURL(null, htmlContent, "text/html", "utf-8", null);


                webView.setOnTouchListener(new View.OnTouchListener() {
                    private static final int LONG_PRESS_THRESHOLD = 500;
                    private boolean isLongPress = false;
                    private float startX, startY;
                    private static final int TOUCH_SLOP = 10;

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                isLongPress = false;
                                startX = event.getX();
                                startY = event.getY();
                                v.postDelayed(() -> isLongPress = true, LONG_PRESS_THRESHOLD);
                                return true;

                            case MotionEvent.ACTION_MOVE:
                                if (Math.abs(event.getX() - startX) > TOUCH_SLOP || Math.abs(event.getY() - startY) > TOUCH_SLOP) {
                                    v.getParent().requestDisallowInterceptTouchEvent(false);
                                    return true;
                                }
                                break;

                            case MotionEvent.ACTION_UP:
                                if (!isLongPress) {
                                    v.performClick();
                                }
                                return true;

                            case MotionEvent.ACTION_CANCEL:
                                isLongPress = false;
                                break;
                        }
                        return true;
                    }
                });

                webView.setOnLongClickListener(v -> true);
                webView.setLongClickable(false);
                webView.setHapticFeedbackEnabled(false);


            } else if (block.type == CardType.IMAGE) {
                formulaBlock.setVisibility(View.GONE);
                textBlock.setVisibility(View.GONE);
                imageBlock.setVisibility(View.VISIBLE);

                ImageView imageView = imageBlock.findViewById(R.id.block_image);
                Button openGallerySelectorButton = imageBlock.findViewById(R.id.galleryButton);

                ImageView deleteImageButton = imageBlock.findViewById(R.id.removeImageBlock);

                deleteImageButton.setOnClickListener(v -> {
                    if (imageDelete != null) {
                        imageDelete.onImageDelete(getAdapterPosition());
                    }
                });

                if (block.content != null) {
                    Glide.with(imageView.getContext()).load(block.content).into(imageView);
                }

                openGallerySelectorButton.setOnClickListener(v -> {
                    openImageSelector(getAdapterPosition(), v);
                });


            } else if (block.type == CardType.FORMULA) {
                textBlock.setVisibility(View.GONE);
                formulaBlock.setVisibility(View.VISIBLE);
                imageBlock.setVisibility(View.GONE);

                TextView formulaTapToEdit = formulaBlock.findViewById(R.id.tapToEditOverlayFormula);

                ImageView deleteImageButton = formulaBlock.findViewById(R.id.removeFormulaBlock);
                deleteImageButton.setOnClickListener(v -> {
                    if (formulaDelete != null) {
                        formulaDelete.onFormulaDelete(getAdapterPosition());
                    }
                });

                View formulaView = itemView.findViewById(R.id.latexView);
                JLatexMathDrawable drawable = JLatexMathDrawable.builder(block.content)
                        .textSize(70)
                        .padding(8)
                        .background(0xFFffffff)
                        .align(JLatexMathDrawable.ALIGN_CENTER)
                        .build();
                formulaView.setBackground(drawable);

                if(block.content != null && !block.content.isEmpty()) {
                    formulaTapToEdit.setVisibility(View.GONE);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT
                    );
                    formulaView.setLayoutParams(params);
                }
                else {
                    formulaTapToEdit.setVisibility(View.VISIBLE);
                    Context context = formulaView.getContext();
                    float scale = context.getResources().getDisplayMetrics().density;
                    int heightInPx = (int) (40 * scale + 0.5f);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            heightInPx
                    );
                    formulaView.setLayoutParams(params);
                }

            }
        }
    }

    private void openImageSelector(int position, View v) {
        currentBlockIndex = position;
        ((CardBuilderActivity) v.getContext()).setCurrentBlockAdapter(CardBlockAdapter.this);
        pickImageFromGalleryLauncher.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    private void openAppImageSelector(int position, View v) {
        currentBlockIndex = position;
        ((CardBuilderActivity) v.getContext()).setCurrentBlockAdapter(CardBlockAdapter.this);
    }

    public void updateImage(Uri imageUri) {
        if (currentBlockIndex != -1 && imageUri != null) {
            blocks.get(currentBlockIndex).content = imageUri.toString();
            notifyItemChanged(currentBlockIndex);
        }
    }
}
