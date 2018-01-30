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
import com.drakeet.rebase.api.tool.Hashes;
import com.drakeet.rebase.api.tool.MongoDBs;
import com.drakeet.rebase.api.type.Failure;
import com.drakeet.rebase.api.type.User;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.hibernate.validator.constraints.NotEmpty;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;
import static javax.ws.rs.core.Response.Status.FORBIDDEN;

/**
 * @author drakeet
 */
@Path("/authorizations") public class AuthorizationResource {

    private static final String TAG = AuthorizationResource.class.getSimpleName();


    @GET @Path("{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response authorize(
        @Username @PathParam("username") String username,
        @NotEmpty @QueryParam("password") String password) {

        Bson filter = and(eq(User.USERNAME, username), eq(User.PASSWORD, Hashes.sha1(password)));
        Document newAuth = Authorizations.newInstance(username);
        Document user = MongoDBs.users().findOneAndUpdate(filter, set(User.AUTHORIZATION, newAuth));
        if (user == null) {
            return Response.status(FORBIDDEN)
                .entity(new Failure("The username or password is incorrect"))
                .build();
        } else {
            return Response.ok(newAuth).build();
        }
    }
}