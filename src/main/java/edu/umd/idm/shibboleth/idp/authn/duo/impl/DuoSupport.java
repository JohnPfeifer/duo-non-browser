/*
 * Licensed to the University Corporation for Advanced Internet Development,
 * Inc. (UCAID) under one or more contributor license agreements.  See the
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.umd.idm.shibboleth.idp.authn.duo.impl;

import net.shibboleth.idp.authn.duo.DuoIntegration;
import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;

import java.net.URI;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nonnull;

import com.duosecurity.duoweb.Base64;
import com.duosecurity.duoweb.Util;

import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.NameValuePair;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * This should be integrated into net.shibboleth.idp.authn.duo.impl.DuoSupport.
 * 
 */
public final class DuoSupport {

    /** RFC 2822 formatter for date/time. */
    public static final DateTimeFormatter RFC_2822_DATE_FORMAT =
            DateTimeFormat.forPattern("EEE', 'dd' 'MMM' 'yyyy' 'HH:mm:ss' 'Z");

    /** Constructor. */
    private DuoSupport() {
    }

    /**
     * Sign a Duo request.
     * 
     * @param request the request to be signed
     * @param duo integration parameters to use
     * 
     * @throws InvalidKeyException bad skey value
     * @throws NoSuchAlgorithmException unknown encryption algorithm
     * @throws UnsupportedEncodingException failure from {@link URLEncoder}
     */
    @Nonnull @NotEmpty public static void signRequest(@Nonnull final RequestBuilder request,
            @Nonnull final DuoIntegration duo)
            throws InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
        final String ikey = duo.getIntegrationKey();
        final String skey = duo.getSecretKey();
        final int sigVersion = 2;
        final String date = new DateTime().toString(RFC_2822_DATE_FORMAT);
        final String canon = canonRequest(request, date, sigVersion);
        final String sig = Util.hmacSign(skey, canon);

        final String auth = ikey + ":" + sig;
        final String header = "Basic " + Base64.encodeBytes(auth.getBytes());
        request.addHeader("Authorization", header);
        request.addHeader("Date", date);
    }

    /**
     * The signature requires that the request parameters being in a particular order as specified in the API.
     * 
     * @param request the request
     * @param date the date
     * @param sigVersion the signature version
     * 
     * @return the parameters to be signed in their canonical order
     * 
     * @throws UnsupportedEncodingException failure from {@link URLEncoder}
     */
    protected static String canonRequest(@Nonnull final RequestBuilder request, @Nonnull final String date,
            final int sigVersion) throws UnsupportedEncodingException {
        final URI uri = request.getUri();
        String canon = "";
        if (sigVersion == 2) {
            canon += date + "\n";
        }
        canon += request.getMethod().toUpperCase() + "\n";
        canon += uri.getHost().toLowerCase() + "\n";
        canon += uri.getPath() + "\n";
        canon += createQueryString(request.getParameters());

        return canon;
    }

    /**
     * Builds a string representaion of the query string with the parameter names is alphabetical order. The names and
     * values are URL encoded and then they are concatenated with '&' in between.
     * 
     * @param params the name/value pairs to be joined
     * 
     * @return the canonical query string
     * 
     * @throws UnsupportedEncodingException failure from {@link URLEncoder}
     */
    private static String createQueryString(@Nonnull final List<NameValuePair> params)
            throws UnsupportedEncodingException {

        final ArrayList<String> args = new ArrayList<String>();

        // sort by name
        Collections.sort(params, new Comparator<NameValuePair>() {
            public int compare(final NameValuePair nvp1, final NameValuePair nvp2) {
                return nvp1.getName().compareTo(nvp2.getName());
            }
        });

        // URL encode and join the name/values with '='
        for (final NameValuePair nvp : params) {
            final String name = URLEncoder.encode(nvp.getName(), "UTF-8").replace("+", "%20").replace("*", "%2A")
                    .replace("%7E", "~");
            final String value = URLEncoder.encode(nvp.getValue(), "UTF-8").replace("+", "%20").replace("*", "%2A")
                    .replace("%7E", "~");
            args.add(name + "=" + value);
        }

        // concatenate everything togther with '&'
        return join(args.toArray(), "&");
    }

    /**
     * Concatenate an array of objects together with a specifed string seprater.
     * 
     * @param s the array of objects
     * @param glue the sting separator
     * 
     * @return the concatenated string
     */
    static String join(@Nonnull final Object[] s, @Nonnull final String glue) {
        final int k = s.length;
        if (k == 0) {
            return "";
        }
        final StringBuilder out = new StringBuilder();
        out.append(s[0]);
        for (int x = 1; x < k; ++x) {
            out.append(glue).append(s[x]);
        }
        return out.toString();
    }
}
