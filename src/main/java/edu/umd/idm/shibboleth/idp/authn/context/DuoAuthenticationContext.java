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

package edu.umd.idm.shibboleth.idp.authn.context;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.messaging.context.BaseContext;

import net.shibboleth.idp.authn.context.AuthenticationContext;

/**
 * Context that carries the Duo factor and device or passcode to be used in validation.
 * 
 * @parent {@link AuthenticationContext}
 * @added After extracting the Duo factor and device or passcode during authentication
 */
public class DuoAuthenticationContext extends BaseContext {

    /** the usename. */
    @Nullable private String username;

    /** the factor. */
    @Nullable private String duoFactor;

    /** the device. */
    @Nullable private String duoDevice;

    /** the passcode. */
    @Nullable private String duoPasscode;

    /**
     * Gets the username.
     * 
     * @return the username
     */
    @Nullable public String getUsername() {
        return username;
    }

    /**
     * Sets the username.
     * 
     * @param name the username
     * 
     * @return this context
     */
    @Nonnull public DuoAuthenticationContext setUsername(@Nullable final String name) {
        username = name;
        return this;
    }

    /**
     * Gets the username.
     * 
     * @return the username
     */
    @Nullable public String getDevice() {
        return duoDevice;
    }

    /**
     * Sets the device.
     * 
     * @param device the Duo device identifier
     * 
     * @return this context
     */
    @Nonnull public DuoAuthenticationContext setDevice(@Nullable final String device) {
        duoDevice = device;
        return this;
    }

    /**
     * Gets the factor.
     * 
     * @return the factor
     */
    @Nullable public String getFactor() {
        return duoFactor;
    }

    /**
     * Sets the factor.
     * 
     * @param factor the Duo factor identifier
     * 
     * @return this context
     */
    @Nonnull public DuoAuthenticationContext setFactor(@Nullable final String factor) {
        duoFactor = factor;
        return this;
    }

    /**
     * Gets the passcode.
     * 
     * @return the passcode
     */
    @Nonnull public String getPasscode() {
        return duoPasscode;
    }

    /**
     * Sets the passcode.
     * 
     * @param passcode the Duo passcode
     * 
     * @return this context
     */
    @Nonnull public DuoAuthenticationContext setPasscode(@Nullable final String passcode) {
        duoPasscode = passcode;
        return this;
    }

}