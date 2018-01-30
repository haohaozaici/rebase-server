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

import com.drakeet.rebase.api.type.bilibili.BilibiliPic;
import com.drakeet.rebase.api.type.Category;
import com.drakeet.rebase.api.type.User;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;

/**
 * @author drakeet
 */
public class MongoDBs {

    private static final String TAG = MongoDBs.class.getSimpleName();

    private static final int VERSION = 2;

    private static MongoDatabase db;
    private static MongoCollection<Document> users;
    private static MongoCollection<Document> categories;
    private static MongoCollection<Document> feeds;
    private static MongoCollection<Document> licenses;
    private static MongoCollection<Document> bilibili_pics;


    public static void setup() {
        MongoClient mongoClient = new MongoClient("104.207.155.124", 27017);
        db = mongoClient.getDatabase("rebase");
        Log.i(TAG, "[setUp] Connect to database successfully");
        initCollections(db);
    }


    private static void initCollections(MongoDatabase db) {
        try {
            db.createCollection("bilibili_pics");
            db.createCollection("users");
            db.createCollection("categories");
            db.createCollection("feeds");
            db.createCollection("licenses");
        } catch (Exception e) {
            Log.w(TAG, "[attemptCreateCollections] " + e.getMessage());
        }
        users = db.getCollection("users");
        categories = db.getCollection("categories");
        feeds = db.getCollection("feeds");
        licenses = db.getCollection("licenses");
        bilibili_pics = db.getCollection("bilibili_pics");

        users.createIndex(
            Indexes.ascending(User.USERNAME),
            new IndexOptions().unique(true));

        categories.createIndex(
            Indexes.ascending(Category.OWNER, Category.KEY),
            new IndexOptions().unique(true));

        bilibili_pics.createIndex(
            Indexes.ascending(BilibiliPic.BILIBILI_ID),
            new IndexOptions().unique(true));
    }


    public static MongoDatabase db() {
        return db;
    }


    public static MongoCollection<Document> users() {
        return users;
    }


    public static MongoCollection<Document> categories() {
        return categories;
    }


    public static MongoCollection<Document> feeds() {
        return feeds;
    }


    public static MongoCollection<Document> licenses() {
        return licenses;
    }

    public static MongoCollection<Document> bilibili_pics() {
        return bilibili_pics;
    }


    public static <TItem> Bson optionalSet(final String fieldName, final TItem value) {
        if (value == null) {
            return null;
        } else {
            return Updates.set(fieldName, value);
        }
    }
}