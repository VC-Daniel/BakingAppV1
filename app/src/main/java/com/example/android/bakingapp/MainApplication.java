package com.example.android.bakingapp;

import android.app.Application;

import fr.arnaudguyon.logfilter.Log;

/**
 * Created by Daniel on 9/24/2017.
 * <p>
 * Initialising the logging level to be used in different release modes.
 * The logic to set the log level is based on th usage information at https://android-arsenal.com/details/1/4789
 * <p>
 * This is accomplished by using the 3rd party tool "Log Filter" which is found at
 * https://github.com/smart-fun/LogFilter?utm_source=android-arsenal.com&utm_medium=referral&utm_campaign=4789
 * <p>
 * "Log Filter" is provided with the following licence:
 * ##License##
 * Copyright 2016 Arnaud Guyon
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

public class MainApplication extends Application {

    private Log mLog;

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            mLog = new Log(Log.Priority.VERBOSE);
        } else {
            mLog = new Log(Log.Priority.ERROR);
        }
    }
}