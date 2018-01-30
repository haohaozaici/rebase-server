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

import com.drakeet.rebase.api.constraint.Username;
import com.drakeet.rebase.api.tool.Authorizations;
import com.drakeet.rebase.api.tool.Globals;
import com.drakeet.rebase.api.tool.MongoDBs;
import com.drakeet.rebase.api.tool.URIs;
import com.drakeet.rebase.api.type.Category;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.bson.Document;

import static com.drakeet.rebase.api.type.Category.CREATED_AT;
import static com.drakeet.rebase.api.type.Category.KEY;
import static com.drakeet.rebase.api.type.Category.NAME;
import static com.drakeet.rebase.api.type.Category.OWNER;
import static com.drakeet.rebase.api.type.Category.RANK;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Sorts.ascending;

/**
 * @author drakeet
 */
@Path("/categories") public class CategoryResource {

    @HeaderParam("Authorization") String auth;


    @GET @Path("{owner}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response readAll(@Username @PathParam("owner") String owner) {
        List<Document> categories = new ArrayList<>();
        MongoDBs.categories().find()
            .filter(eq(OWNER, owner))
            .sort(ascending(RANK))
            .limit(Globals.LIMIT_CATEGORIES)
            .into(categories);
        return Response.ok(categories).build();
    }


    @POST @Path("{owner}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response newCategory(@Valid Category cat, @Username @PathParam("owner") String owner) {
        Authorizations.verify(owner, auth);
        Document category = new Document(KEY, cat.key)
            .append(NAME, cat.name)
            .append(RANK, cat.rank)
            .append(OWNER, owner)
            .append(CREATED_AT, new Date());
        MongoDBs.categories().insertOne(category);
        return Response.created(URIs.create("categories", owner, cat.key))
            .entity(category)
            .build();
    }
}