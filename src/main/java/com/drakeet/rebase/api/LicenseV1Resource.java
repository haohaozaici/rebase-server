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
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.bson.Document;
import org.bson.conversions.Bson;

import static com.drakeet.rebase.api.tool.ObjectIds.objectId;
import static com.drakeet.rebase.api.tool.Strings.isEmpty;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

/**
 * @author drakeet
 */
@Path("/licenses") public class LicenseV1Resource {

    /**
     * 校验激活码是否有效。如果激活码存在，并且设备 ID 没变，则返回原 License 对象。如果不存在，返回错误信息。
     * 如果存在，但设备变了，则根据 override 覆盖使用新的设备 ID。
     *
     * @param id 激活码 key
     * @param deviceId 设备 ID
     * @param override 是否覆盖设备 ID
     * @return 如果激活码有效，返回最新 License，否则返回错误
     */
    @GET @Path("{_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response verify(
        @PathParam("_id") String id,
        @QueryParam("device_id") String deviceId,
        @QueryParam("override") boolean override) {

        if (override && isEmpty(deviceId)) {
            return Responses.badRequest("device_id required");
        }
        Document license = MongoDBs.licenses().find(eq(License._ID, objectId(id))).first();
        RebaseAsserts.notNull(license, "license");
        if (override && !Objects.equals(deviceId, license.getString(License.DEVICE_ID))) {
            final Bson target = eq(License._ID, objectId(id));
            MongoDBs.licenses().updateOne(target, set(License.DEVICE_ID, deviceId));
            license.put(License.DEVICE_ID, deviceId);
        }
        return Response.ok(license).build();
    }
}