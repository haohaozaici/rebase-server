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

import com.drakeet.rebase.api.type.Authorization;
import com.drakeet.rebase.api.type.User;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.bson.Document;

import static com.mongodb.client.model.Filters.eq;

/**
 * @author drakeet
 */
public class Authorizations {

    private static final String TAG = Authorizations.class.getSimpleName();


    /**
     * Verifies that the user's authorization.
     *
     * @param username The username of the user.
     * @param authorization The authorization of the user.
     * @throws IllegalArgumentException When the format of Authorization is unexpected.
     * @throws WebApplicationException When UNAUTHORIZED.
     */
    public static void verify(String username, String authorization) {
        final String accessToken;
        if (authorization.startsWith("token")) {
            accessToken = authorization.split(" ")[1];
        } else {
            throw new IllegalArgumentException("The format of Authorization is unexpected.");
        }
        Document user = MongoDBs.users().find(eq(User.USERNAME, username)).limit(1).first();
        if (user == null) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }
        Document auth = user.get(User.AUTHORIZATION, Document.class);
        if (Objects.equals(auth.getString(Authorization.ACCESS_TOKEN), accessToken)) {
            Log.i(TAG, "Verified successfully.");
        } else {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }
    }


    public static String issueToken(String username) {
        String key = UUID.randomUUID().toString().toUpperCase() +
            "|" + username +
            "|" + System.currentTimeMillis();

        return Hashes.sha1(key);
    }


    public static Document newInstance(String username) {
        return new Document()
            .append(Authorization.ACCESS_TOKEN, issueToken(username))
            .append(Authorization.UPDATED_AT, new Date());
    }
}
