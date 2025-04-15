package com.example.bc_praca_x.database.type_converter;

import androidx.room.TypeConverter;

import com.example.bc_praca_x.database.entity.CardContent;
import com.google.gson.Gson;

public class JsonConverter {
    private static final Gson gson = new Gson();

    @TypeConverter
    public static String fromCardContent(CardContent content) {
        return content == null ? null : gson.toJson(content);
    }

    @TypeConverter
    public static CardContent toCardContent(String json) {
        return json == null ? null : gson.fromJson(json, CardContent.class);
    }

}
