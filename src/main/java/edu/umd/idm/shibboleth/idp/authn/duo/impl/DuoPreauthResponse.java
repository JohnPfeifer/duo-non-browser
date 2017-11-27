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

import java.net.URL;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import edu.umd.idm.shibboleth.idp.authn.duo.DuoAuthApiResponse;

/**
 * Describes the results of an pre-authentication attempt via the Duo AuthAPI.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DuoPreauthResponse extends DuoAuthApiResponse {

    /** the {@link List} of {@link DuoDeivces} registered. */
    @JsonProperty("devices") @Nonnull private List<DuoDevice> devices;

    /** the {@link URL} for the self-enrollment portal. */
    @JsonProperty("enroll_portal_url") @Nullable private URL enrollPortalURL;

    /**
     * Get the Duo devices.
     * 
     * @return Duo devices
     */
    @Nonnull public List<DuoDevice> getDevices() {
        return devices;
    }

    /**
     * Get the Duo enrollment portal url.
     * 
     * @return Duo enrollment portal url
     */
    @Nonnull public URL getEnrollPortalURL() {
        return enrollPortalURL;
    }

}
