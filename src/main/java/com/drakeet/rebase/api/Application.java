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

import com.drakeet.rebase.api.tool.ConstraintViolationExceptionMapper;
import com.drakeet.rebase.api.tool.GsonBodyProvider;
import com.drakeet.rebase.api.tool.Log;
import com.drakeet.rebase.api.tool.MongoDBs;
import com.drakeet.rebase.api.tool.ValidationConfigurationContextResolver;
import com.drakeet.rebase.api.tool.WebExceptionMapper;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

/**
 * @author drakeet
 */
public class Application extends ResourceConfig {

    public Application() {
        property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);
        property(ServerProperties.BV_DISABLE_VALIDATE_ON_EXECUTABLE_OVERRIDE_CHECK, true);

        register(UserResource.class);
        register(AuthorizationResource.class);
        register(CategoryResource.class);
        register(FeedResource.class);
        register(LicenseV1Resource.class);
        register(LicenseV2Resource.class);
        register(GsonBodyProvider.class);
        register(WebExceptionMapper.class);
        register(ConstraintViolationExceptionMapper.class);
        register(ValidationConfigurationContextResolver.class);

        Log.prefix = "------> [Rebase] ~ ";

        MongoDBs.setup();
    }
}
