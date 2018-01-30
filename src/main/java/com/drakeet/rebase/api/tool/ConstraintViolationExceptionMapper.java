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
import com.google.common.base.Joiner;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Priority;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.Priorities;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

/**
 * @author drakeet
 */
@Provider
@Priority(Priorities.USER)
public class ConstraintViolationExceptionMapper
    implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(final ConstraintViolationException exception) {
        List<String> violations = new ArrayList<>();
        exception.getConstraintViolations()
            .stream()
            .forEach(input -> violations.add(format(input)));
        Joiner joiner = Joiner.on("; ").skipNulls();
        String detail = joiner.join(violations);
        return Response.status(BAD_REQUEST)
            .entity(new Failure(detail))
            .build();
    }


    public String format(ConstraintViolation<?> violation) {
        return String.format("%s = %s: %s",
            violation.getPropertyPath(),
            violation.getInvalidValue(),
            violation.getMessage());
    }
}
