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

import com.drakeet.rebase.api.constraint.CategoryKey;
import com.drakeet.rebase.api.constraint.NotEmptyButNull;
import com.drakeet.rebase.api.constraint.Username;
import com.drakeet.rebase.api.tool.Authorizations;
import com.drakeet.rebase.api.tool.Globals;
import com.drakeet.rebase.api.tool.MongoDBs;
import com.drakeet.rebase.api.tool.PATCH;
import com.drakeet.rebase.api.tool.RebaseAsserts;
import com.drakeet.rebase.api.tool.URIs;
import com.drakeet.rebase.api.type.Feed;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.hibernate.validator.constraints.Range;

import static com.drakeet.rebase.api.tool.Filters.filterNotNull;
import static com.drakeet.rebase.api.tool.MongoDBs.optionalSet;
import static com.drakeet.rebase.api.tool.ObjectIds.objectId;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.lt;
import static com.mongodb.client.model.Sorts.descending;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.currentDate;

/**
 * @author drakeet
 */
@Path("categories/{owner}/{category}/feeds") public class FeedResource {

    @Username @PathParam("owner") String owner;
    @CategoryKey @PathParam("category") String category;
    @HeaderParam("Authorization") String auth;


    @GET @Produces(MediaType.APPLICATION_JSON)
    public Response readAll(
        @NotEmptyButNull @QueryParam("last_id") String lastId,
        @Range(min = 1, max = Globals.MAX_SIZE) @DefaultValue("20") @QueryParam("size") int size) {

        RebaseAsserts.existCategory(category);
        List<Document> feeds = new ArrayList<>();
        List<Bson> filters = new ArrayList<>();
        if (lastId != null) {
            filters.add(lt(Feed._ID, objectId(lastId)));
        }
        filters.add(eq(Feed.CATEGORY, category));
        filters.add(eq(Feed.OWNER, owner));
        MongoDBs.feeds().find().sort(descending(Feed._ID))
            .filter(and(filters))
            .limit(size)
            .into(feeds);
        return Response.ok(feeds).build();
    }


    @POST @Consumes(MediaType.APPLICATION_JSON)
    public Response newFeed(@NotNull @Valid Feed feed) {
        Authorizations.verify(owner, auth);
        RebaseAsserts.existCategory(category);
        final Date now = new Date();
        Document document = new Document(Feed.CATEGORY, category)
            .append(Feed.TITLE, feed.title)
            .append(Feed.CONTENT, feed.content)
            .append(Feed.URL, feed.url)
            .append(Feed.COVER_URL, feed.coverUrl)
            .append(Feed.OWNER, owner)
            .append(Feed.CREATED_AT, now)
            .append(Feed.PUBLISHED_AT, now);
        MongoDBs.feeds().insertOne(document);
        return Response.created(
            URIs.create("categories", owner, category, "feeds",
                document.getObjectId(Feed._ID).toHexString()))
            .entity(document)
            .build();
    }


    @GET @Path("{_id}")
    public Response feedDetail(@PathParam("_id") String _id) {
        Document feed = MongoDBs.feeds()
            .find(eq(Feed._ID, objectId(_id)))
            .first();
        RebaseAsserts.notNull(feed, "feed");
        return Response.ok(feed).build();
    }


    @PATCH @Path("{_id}")
    public Response editFeed(@PathParam("_id") String _id, @NotNull Feed input) {
        Authorizations.verify(owner, auth);
        if (!isNullOrEmpty(input.category)) {
            RebaseAsserts.existCategory(input.category);
        }
        final Bson target = eq(Feed._ID, objectId(_id));
        MongoDBs.feeds().updateOne(target,
            combine(filterNotNull(
                optionalSet(Feed.TITLE, input.title),
                optionalSet(Feed.CONTENT, input.content),
                optionalSet(Feed.URL, input.url),
                optionalSet(Feed.CATEGORY, input.category),
                optionalSet(Feed.COVER_URL, input.coverUrl),
                currentDate(Feed.UPDATED_AT))
            )
        );
        Document feed = MongoDBs.feeds().find(target).first();
        RebaseAsserts.notNull(feed, "feed");
        return Response.ok(feed).build();
    }


    @DELETE @Path("{_id}")
    public Response deleteFeed(@PathParam("_id") String _id) {
        Authorizations.verify(owner, auth);
        MongoDBs.feeds().deleteOne(eq(Feed._ID, objectId(_id)));
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
