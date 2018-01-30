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

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author drakeet
 */
public class Log {

    private static final Logger logger = new Logger();
    public static String prefix = "";


    public enum Level {
        INFO, WARNING, ERROR
    }


    private static String tag(Level level, String tag) {
        return String.format("[%s] %s %s", level, prefix, tag);
    }


    public static void i(String tag, String message) {
        logger.i(tag(Level.INFO, tag), message);
    }


    public static void w(String tag, String message) {
        logger.w(tag(Level.WARNING, tag), message);
    }


    public static void e(String tag, String msg) {
        logger.e(tag(Level.ERROR, tag), msg);
    }


    public static void e(String tag, String msg, Throwable tr) {
        logger.e(tag(Level.ERROR, tag), msg + '\n' + getStackTraceString(tr));
    }


    /**
     * Handy function to get a loggable stack trace from a Throwable.
     *
     * @param provided An exception to log.
     */
    public static String getStackTraceString(Throwable provided) {
        if (provided == null) {
            return "";
        }

        Throwable throwable = provided;
        while (throwable != null) {
            throwable = throwable.getCause();
        }

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        provided.printStackTrace(printWriter);
        printWriter.flush();
        return stringWriter.toString();
    }


    private static class Logger {

        Level level;


        void i(String tag, String message) {
            level = Level.INFO;
            System.out.println(tag + ": " + message);
        }


        void w(String tag, String message) {
            level = Level.WARNING;
            System.out.println(tag + ": " + message);
        }


        void e(String tag, String message) {
            level = Level.ERROR;
            System.err.println(tag + ": " + message);
        }
    }
}