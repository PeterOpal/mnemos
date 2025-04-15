package com.example.bc_praca_x.helpers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.bc_praca_x.database.DBHelper;
import com.example.bc_praca_x.database.entity.CardPart;
import com.example.bc_praca_x.database.entity.Category;
import com.example.bc_praca_x.database.entity.Media;
import com.example.bc_praca_x.database.enums.CardType;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DeleteUnusedMedia extends Worker {

    public DeleteUnusedMedia(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d("MnemosProcess", "Running cleanup task for media!");
        try {
            DBHelper db = DBHelper.getInstance(getApplicationContext());
            List<Media> allMedia = db.mediaDao().getAllImagesSync();
            List<CardPart> mediaCardParts = db.cardPartDao().getCardPartsByTypeSync(CardType.IMAGE);
            List<Category> mediaCategories = db.categoryDao().getAllCategoriesSync();

            Set<String> usedInCardParts = new HashSet<>();
            Set<String> usedInCategories = new HashSet<>();

            for (Category category : mediaCategories) {
                if (category.getMedia_id() != null) {
                    usedInCategories.add(String.valueOf(category.getMedia_id()));
                }
            }

            for (CardPart cardPart : mediaCardParts) {
                if (cardPart.getCardContent() != null && cardPart.getCardContent().getMedia_id() != null) {
                    usedInCardParts.add(cardPart.getCardContent().getMedia_id());
                }
            }

            Set<String> allUsedMediaIds = new HashSet<>();
            allUsedMediaIds.addAll(usedInCardParts);
            allUsedMediaIds.addAll(usedInCategories);

            for (Media media : allMedia) {
                String mediaId = String.valueOf(media.getId());
                if (!allUsedMediaIds.contains(mediaId)) { //the media is not used in any card part or category we can delete it
                    File file = new File(media.getPath());
                    if (file.exists()) {
                        Log.d("MnemosProcess", "Deleting unused media: " + mediaId + ", path: " + media.getPath());
                        file.delete(); //disk
                        db.mediaDao().deleteMediaById(media.getId()); //database
                    }
                }
            }


        } catch (Exception e) {
            Log.e("MnemosProcess", "Error during cleanup: " + e.getMessage());
            return Result.failure();
        }


        return Result.success();
    }
}
