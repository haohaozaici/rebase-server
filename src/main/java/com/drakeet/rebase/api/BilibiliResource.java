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

package com.drakeet.rebase.api;

import com.drakeet.rebase.api.tool.*;
import com.drakeet.rebase.api.tool.util.TimeUtil;
import com.drakeet.rebase.api.type.bilibili.BilibiliPic;
import com.drakeet.rebase.api.type.Failure;
import com.drakeet.rebase.api.type.bilibili.SplashPicRes;
import com.google.gson.Gson;
import com.mongodb.Tag;
import com.mongodb.client.FindIterable;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.regex;
import static com.mongodb.client.model.Projections.exclude;

/**
 * @author haohao
 */
@Path("/bilibilipic") public class BilibiliResource {

    private static final String TAG = BilibiliResource.class.getSimpleName();

    @GET @Path("sync")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sync() {
        OkHttpClient client = new OkHttpClient.Builder().build();

        String url = "http://app.bilibili.com/x/splash?plat=0&width=1080&height=1920";

        Request request = new Request.Builder()
            .url(url)
            .build();

        String res;
        try (okhttp3.Response response = client.newCall(request).execute()) {
            if (response.code() == 200) {
                res = response.body().string();
                Log.i(TAG, res);

                SplashPicRes splashPicRes = new Gson().fromJson(res, SplashPicRes.class);
                for (SplashPicRes.DataBean bean : splashPicRes.getData()) {
                    savePic(bean);
                }

            } else {
                return returnServerError(response.message());
            }
        } catch (IOException e) {
            e.printStackTrace();
            return returnServerError(e.getMessage());
        }

        return Response.ok(res).build();
    }

    private Response returnServerError(String message) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
            .entity(new Failure("bilibili pic server error: " + message))
            .build();
    }


    @GET @Path("{bilibili_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPic(@PathParam("bilibili_id") String bilibili_id) {
        Document pic = MongoDBs.bilibili_pics().find(eq(BilibiliPic.BILIBILI_ID, bilibili_id))
            .limit(1)
            .first();
        RebaseAsserts.notNull(pic, "bilibilipic");
        return Response.ok(pic).build();

    }

    @GET @Path("all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllPic() {


        FindIterable<Document> pics = MongoDBs.bilibili_pics().find();
//        RebaseAsserts.notNull(pic, "bilibilipic");
//        return Response.ok(pic).build();
        return null;

    }


    private void savePic(@NotNull SplashPicRes.DataBean bilibiliPic) {

        Document pic = MongoDBs.bilibili_pics().find(eq(BilibiliPic.BILIBILI_ID, bilibiliPic.getId()))
            .limit(1)
            .first();

        if (pic == null) {
            Document document = new Document(BilibiliPic.BILIBILI_ID, bilibiliPic.getId())
                .append(BilibiliPic.START_TIME, TimeUtil.millis2String(bilibiliPic.getStart_time() * 1000L))
                .append(BilibiliPic.END_TIME, TimeUtil.millis2String(bilibiliPic.getEnd_time() * 1000L))
                .append(BilibiliPic.IMAGE, bilibiliPic.getImage());
            MongoDBs.bilibili_pics().insertOne(document);
        }

    }

}