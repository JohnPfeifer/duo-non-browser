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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nullable;

/**
 * Describes the failure of a Duo AuthAPI call.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DuoFailureResponse {

    /** the failure status. */
    @JsonProperty("stat") @Nullable private String stat;

    /** the failure code. */
    @JsonProperty("code") @Nullable private String code;

    /** the failure message. */
    @JsonProperty("message") @Nullable private String message;

    /** the failure message detail. */
    @JsonProperty("message_detail") @Nullable private String messageDetail;

    /**
     * Get the failure status.
     * 
     * @return failure status
     */
    public String getStat() {
        return stat;
    }

    /**
     * Get the failure code.
     * 
     * @return failure code
     */
    public String getCode() {
        return code;
    }

    /**
     * Get the failure message.
     * 
     * @return failure message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Get the failure message details.
     * 
     * @return failure message details
     */
    public String getMessageDetail() {
        return messageDetail;
    }
}
