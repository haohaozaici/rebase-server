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

import com.drakeet.rebase.api.type.Category;
import javax.ws.rs.WebApplicationException;
import org.bson.Document;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.include;

/**
 * @author drakeet
 */
public class RebaseAsserts {

    public static void existCategory(String key) {
        Document result = MongoDBs.categories().find(eq(Category.KEY, key))
            .projection(include(Category.KEY))
            .limit(1).first();
        if (result == null) {
            throwNotFoundOf("category: " + key);
        }
    }


    public static void notNull(Object o, String argName) {
        if (o == null) {
            throwNotFoundOf(argName);
        }
    }


    private static void throwNotFoundOf(final String argName) {
        String message = String.format("The %s is not found", argName);
        throw new WebApplicationException(Responses.notFound(message));
    }
}
