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

package com.drakeet.rebase.api.type.bilibili;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * @author drakeet
 */
public class BilibiliPic {


    public static final String _ID = "_id";
    public static final String BILIBILI_ID = "bilibili_id";
    public static final String START_TIME = "start_time";
    public static final String END_TIME = "end_time";
    public static final String IMAGE = "image";

    @NotNull @Length(min = 1, max = 12)
    public String  bilibili_id;

    @NotNull @Length(min = 1, max = 32)
    public String start_time;

    @NotNull @Length(min = 1, max = 32)
    public String end_time;

    @NotNull @Length(min = 1, max = 512)
    public String image;

}