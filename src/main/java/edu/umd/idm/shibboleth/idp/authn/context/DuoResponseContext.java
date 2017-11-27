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

import edu.umd.idm.shibboleth.idp.authn.duo.DuoAuthApiResponse;
import edu.umd.idm.shibboleth.idp.authn.duo.impl.DuoAuthResponse;
import edu.umd.idm.shibboleth.idp.authn.duo.impl.DuoPreauthResponse;
import edu.umd.idm.shibboleth.idp.authn.duo.DuoAuthApi;

import net.shibboleth.idp.authn.context.AuthenticationContext;

import org.opensaml.messaging.context.BaseContext;

import com.google.common.base.MoreObjects;

/**
 * A context containing data about an Duo AuthAPI authentication operation.
 * 
 * @parent {@link AuthenticationContext}
 * @added After an Duo AuthAPI authentication attempt
 */
public class DuoResponseContext extends BaseContext {

    /** Authentication response. */
    @Nullable private DuoAuthApiResponse authenticationResponse;
    
    /**
     * Get the Duo authentication response.
     * 
     * @return Duo authentication response.
     */
    @Nullable public DuoAuthApiResponse getAuthenticationResponse() {
        return authenticationResponse;
    }
    
    /**
     * Set the Duo authentication response.
     * 
     * @param response of a Duo authentication
     * @return this context
     */
    @Nonnull public DuoResponseContext setAuthenticationResponse(@Nullable final DuoAuthApiResponse response) {
        this.authenticationResponse = response;
        return this;
    }
    
    /**
     * Check if the result of the response is allow state.
     * 
     * @return true if result of the response is allow state
     */
    public boolean isAllow() {
        return authenticationResponse.getResult().equals(DuoAuthApi.DUO_AUTH_RESULT_ALLOW);
    }
    
    /**
     * Check if the result of the response is deny state.
     * 
     * @return true if result of the response is deny state
     */
    public boolean isDeny() {
        return authenticationResponse.getResult().equals(DuoAuthApi.DUO_AUTH_RESULT_DENY);
    }
    
    /**
     * Check if the status of the response is bypass state.
     * 
     * @return true if status of the response is bypass state
     */
    public boolean isBypass() {
        if (authenticationResponse.getClass().isInstance(DuoPreauthResponse.class)) {
            return ((DuoPreauthResponse)authenticationResponse).getResult().equals(DuoAuthApi.DUO_PREAUTH_RESULT_ALLOW);
        } else { 
            return ((DuoAuthResponse)authenticationResponse).getStatus().equals(DuoAuthApi.DUO_AUTH_STATUS_BYPASS);
        }
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("duoApiResponse", authenticationResponse).toString();
    }

}
