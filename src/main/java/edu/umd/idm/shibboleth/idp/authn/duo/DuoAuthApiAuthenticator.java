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

import java.security.GeneralSecurityException;

import javax.annotation.Nonnull;

import net.shibboleth.idp.authn.duo.DuoIntegration;

import edu.umd.idm.shibboleth.idp.authn.context.DuoAuthenticationContext;

/**
 * Strategy pattern component for Duo AuthApi authentication.
 *
 */
public interface DuoAuthApiAuthenticator {

    /**
     * Perform an authentication action via a Duo AuthApi endpoint.
     * 
     * @param duoContext Duo authentication context to use
     * @param duoIntegration Duo integration to use
     * 
     * @return a {@link DuoAuthApiResponse}
     * 
     * @throws GeneralSecurityException authentication failure
     */
    public DuoAuthApiResponse authenticate(@Nonnull final DuoAuthenticationContext duoContext,
            @Nonnull final DuoIntegration duoIntegration) throws GeneralSecurityException;
}