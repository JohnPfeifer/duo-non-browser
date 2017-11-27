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

package edu.umd.idm.shibboleth.idp.authn.duo;

import javax.annotation.Nonnull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Describes the results of a Duo AuthAPI call. This is intended for
 * use with jackson {@link ObjectMapper}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DuoAuthApiResponse {

    /** the result. */
    @JsonProperty("result")
    @Nonnull private String result;
    
    /** the status message. */
    @JsonProperty("status_msg")
    @Nonnull private String statusMessage;
    
    /**
     * Get the Duo result string.
     * 
     * @return the result string
     */
    @Nonnull public String getResult() {
        return this.result;
    }
    
    /**
     * Get the Duo status message.
     * 
     * @return the Duo status message
     */
    @Nonnull public String getStatusMessage() {
        return this.statusMessage;
    }

}
