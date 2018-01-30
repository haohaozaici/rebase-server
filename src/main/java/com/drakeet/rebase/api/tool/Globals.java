/*
 * Copyright (C) 2017 Drakeet <drakeet.me@gmail.com>
 *
 * This file is part of rebase-server
 *
 * rebase-server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * rebase-server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with rebase-server. If not, see <http://www.gnu.org/licenses/>.
 */

package com.drakeet.rebase.api.tool;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bson.types.ObjectId;

/**
 * @author drakeet
 */
public class Globals {

    public static final int LIMIT_CATEGORIES = 11;
    public static final String ENDPOINT = "https://api.drakeet.com/rebase";
    public static final int MAX_SIZE = 100;
    public static final int SIZE_USERNAME = 12;
    public static final int SIZE_CATEGORY = 64;
    public static final int LIMIT_REGISTER = 5;


    public static Gson newGson() {
        final GsonBuilder gsonBuilder = new GsonBuilder()
            .registerTypeAdapter(ObjectId.class, new ObjectIdSerializer())
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setPrettyPrinting()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");     // ISO 8601
        return gsonBuilder.create();
    }
}
