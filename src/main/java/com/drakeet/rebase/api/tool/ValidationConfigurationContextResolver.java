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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import javax.validation.ParameterNameProvider;
import javax.validation.Validation;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.ContextResolver;
import org.glassfish.jersey.server.validation.ValidationConfig;
import org.glassfish.jersey.server.validation.internal.InjectingConstraintValidatorFactory;

/**
 * @author drakeet
 */
public class ValidationConfigurationContextResolver implements ContextResolver<ValidationConfig> {

    @Context private ResourceContext resourceContext;


    @Override
    public ValidationConfig getContext(final Class<?> type) {
        return new ValidationConfig()
            .constraintValidatorFactory(
                resourceContext.getResource(InjectingConstraintValidatorFactory.class))
            .parameterNameProvider(new RebaseParameterNameProvider());
    }


    private class RebaseParameterNameProvider implements ParameterNameProvider {

        private final ParameterNameProvider nameProvider;


        public RebaseParameterNameProvider() {
            nameProvider = Validation.byDefaultProvider()
                .configure()
                .getDefaultParameterNameProvider();
        }


        @Override
        public List<String> getParameterNames(final Constructor<?> constructor) {
            return nameProvider.getParameterNames(constructor);
        }


        @Override
        public List<String> getParameterNames(final Method method) {
            if ("readAll".equals(method.getName())) {
                return Arrays.asList("last_id", "size");
            }
            // TODO: 2017/2/2 more 
            return nameProvider.getParameterNames(method);
        }
    }
}
