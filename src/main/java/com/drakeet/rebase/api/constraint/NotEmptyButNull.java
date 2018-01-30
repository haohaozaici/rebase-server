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

package com.drakeet.rebase.api.constraint;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

/**
 * @author drakeet
 */
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = { NotEmptyButNull.Validator.class })
public @interface NotEmptyButNull {

    String message() default "{com.drakeet.rebase.api.constraint.NotEmptyButNull.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class Validator implements ConstraintValidator<NotEmptyButNull, String> {
        @Override
        public void initialize(final NotEmptyButNull notEmptyButNull) {
        }


        @Override
        public boolean isValid(final String value, final ConstraintValidatorContext context) {
            return value == null || !"".equals(value);
        }
    }
}
