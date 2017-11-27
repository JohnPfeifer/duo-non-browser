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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import edu.umd.idm.shibboleth.idp.authn.duo.DuoAuthApiResponse;

/**
 * Describes the results of an authentication attempt via the Duo AuthAPI. This is intended for use with a jackson
 * {@link ObjectMapper}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DuoAuthResponse extends DuoAuthApiResponse {

    /** the status string. */
    @JsonProperty("status") @Nonnull private String status;

    /** the trusted device token string. */
    @JsonProperty("trusted_device_token") @Nullable private String trustedDeviceToken;

    /**
     * Get the Duo status string.
     * 
     * @return Duo status string
     */
    @Nonnull public String getStatus() {
        return status;
    }

    /**
     * Get the Duo trusted device token string.
     * 
     * @return Duo trusted device token string
     */
    @Nonnull public String getTrustedDeviceToken() {
        return trustedDeviceToken;
    }

}
