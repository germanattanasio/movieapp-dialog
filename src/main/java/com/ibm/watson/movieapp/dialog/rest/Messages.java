/* Copyright IBM Corp. 2015
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ibm.watson.movieapp.dialog.rest;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Locale;

/**
 * Serves up a message (properties) bundle for localization purposes.
 *
 */
public class Messages {
    private static final String BUNDLE_NAME = "locale/messages"; //$NON-NLS-1$
    private static Locale locale;
    private static ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    private Messages() {

    }

    public static void setInfo(Locale local) {
        locale = local;
        RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME, locale);
    }

    public static String getString(String key) {
        String val = "";
        try {
            // Require UTF-8 to render properties file properly
            val = new String(RESOURCE_BUNDLE.getString(key).getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            // fallback to English as something seems to be amiss with the property bundle
            UtilityFunctions.logger.error(MessageFormat.format("Failed to load value for key {0} due to UTF-8 encoding issue.", key), e); //$NON-NLS-1$
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
        return val;
    }
}
