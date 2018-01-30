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

import com.drakeet.rebase.api.type.Failure;
import com.mongodb.MongoWriteException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

/**
 * @author drakeet
 */
@Provider
public class WebExceptionMapper implements ExceptionMapper<Exception> {

    private static final String TAG = WebExceptionMapper.class.getSimpleName();


    @Override
    public Response toResponse(final Exception exception) {
        Log.e(TAG, "[toResponse]" + exception.getMessage());
        if (exception instanceof WebApplicationException) {
            Response _response = ((WebApplicationException) exception).getResponse();
            return Response.fromResponse(_response)
                .entity(new Failure(_response.getStatusInfo().getReasonPhrase()))
                .build();

        } else if (exception instanceof MongoWriteException) {
            MongoWriteException _exception = (MongoWriteException) exception;
            return Response.status(BAD_REQUEST)
                .entity(new Failure(_exception.getError().getMessage()))
                .build();

        } else if (exception instanceof NullPointerException) {
            return Response.status(NOT_FOUND)
                .entity(new Failure(exception.getMessage()))
                .build();

        } else {
            return Response.status(BAD_REQUEST)
                .entity(new Failure(exception.getMessage()))
                .build();
        }
    }
}
