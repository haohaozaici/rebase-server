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

import com.drakeet.rebase.api.tool.MongoDBs;
import com.drakeet.rebase.api.tool.RebaseAsserts;
import com.drakeet.rebase.api.tool.Responses;
import com.drakeet.rebase.api.type.License;
import java.util.Objects;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.hibernate.validator.constraints.NotEmpty;

import static com.drakeet.rebase.api.tool.ObjectIds.objectId;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

/**
 * @author drakeet
 */
@Path("/v2/licenses") public class LicenseV2Resource {

    @GET @Path("{_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response check(@PathParam("_id") String id) {
        if (!ObjectId.isValid(id)) {
            return Responses.notFound("激活码无效");
        }
        Document license = MongoDBs.licenses().find(eq(License._ID, objectId(id))).first();
        RebaseAsserts.notNull(license, "license");
        return Response.ok(license).build();
    }


    @POST @Path("{_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response active(@PathParam("_id") String id, @NotEmpty @QueryParam("device_id") String deviceId) {
        if (!ObjectId.isValid(id)) {
            return Responses.notFound("激活码无效");
        }
        Document license = MongoDBs.licenses().find(eq(License._ID, objectId(id))).first();
        RebaseAsserts.notNull(license, "license");
        if (!Objects.equals(deviceId, license.getString(License.DEVICE_ID))) {
            final Bson target = eq(License._ID, objectId(id));
            MongoDBs.licenses().updateOne(target, set(License.DEVICE_ID, deviceId));
            license.put(License.DEVICE_ID, deviceId);
        }
        return Response.ok(license).build();
    }
}