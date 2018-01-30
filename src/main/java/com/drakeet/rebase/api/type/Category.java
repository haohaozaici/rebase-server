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

package com.drakeet.rebase.api.type;

import com.drakeet.rebase.api.constraint.CategoryKey;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

/**
 * @author drakeet
 */
public class Category extends Jsonable {

    public static final String KEY = "key";
    public static final String NAME = "name";
    public static final String RANK = "rank";
    public static final String OWNER = "owner";
    public static final String UPDATED_AT = "updated_at";
    public static final String CREATED_AT = "created_at";

    @NotNull @CategoryKey
    public String key;

    @NotNull @Length(min = 1, max = 12)
    public String name;

    @Max(65536)
    public int rank;
}
