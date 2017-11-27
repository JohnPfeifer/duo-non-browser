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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import edu.umd.idm.shibboleth.idp.authn.duo.DuoAuthApiResponse;

/**
 * Handle a generic object returned from the response that will come from the Duo 
 * AuthApi.
 *
 * @param <T> the subclass of {@link DuoAuthApiResponse} being wrapped
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DuoResponseWrapper<T extends DuoAuthApiResponse> {
    
    /** the inner response. */
    @JsonProperty("response")
    @Nonnull private T response;
    
    /** the response status. */
    @JsonProperty("stat")
    @Nonnull private String stat;
    
    /**
     * Get the inner response.
     * 
     * @return inner response
     */
    public T getResponse() {
        return response;
    }

    /**
     * Get the response status.
     * 
     * @return response status
     */
    public String getStat() {
        return stat;
    }

}
