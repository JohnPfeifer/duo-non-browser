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

import edu.umd.idm.shibboleth.idp.authn.context.DuoAuthenticationContext;
import edu.umd.idm.shibboleth.idp.authn.context.DuoResponseContext;
import edu.umd.idm.shibboleth.idp.authn.duo.DuoAuthApi;

import java.security.Principal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.security.auth.Subject;

import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;

import net.shibboleth.idp.authn.AbstractValidationAction;
import net.shibboleth.idp.authn.AuthnEventIds;
import net.shibboleth.idp.authn.context.AuthenticationContext;
import net.shibboleth.idp.authn.context.SubjectCanonicalizationContext;
import net.shibboleth.idp.authn.duo.DuoIntegration;
import net.shibboleth.idp.authn.duo.DuoPrincipal;
import net.shibboleth.idp.session.context.navigate.CanonicalUsernameLookupStrategy;
import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.logic.FunctionSupport;

import com.duosecurity.duoweb.DuoWebException;

/**
 * An action that checks for a {@link DuoApiAuthContect} and directly produces an
 * {@link net.shibboleth.idp.authn.AuthenticationResult} based on that identity by authenticating against the Duo
 * AuthAPI.
 * 
 * @event {@link org.opensaml.profile.action.EventIds#PROCEED_EVENT_ID}
 * @event {@link AuthnEventIds#AUTHN_EXCEPTION}
 * @event {@link AuthnEventIds#ACCOUNT_WARNING}
 * @event {@link AuthnEventIds#ACCOUNT_ERROR}
 * @event {@link AuthnEventIds#INVALID_CREDENTIALS}
 * @pre
 * 
 *      <pre>
 *      ProfileRequestContext.getSubcontext(AuthenticationContext.class).getAttemptedFlow() != null
 *      </pre>
 * 
 * @post If AuthenticationContext.getSubcontext(UsernamePasswordContext.class) != null, then an
 *       {@link net.shibboleth.idp.authn.AuthenticationResult} is saved to the {@link AuthenticationContext} on a
 *       successful login. On a failed login, the
 *       {@link AbstractValidationAction#handleError(ProfileRequestContext, AuthenticationContext, String, String)}
 *       method is called.
 */
public class ValidateUsernameAgainstDuoAuthApi extends AbstractValidationAction {

    /** Default prefix for metrics. */
    @Nonnull @NotEmpty private static final String DEFAULT_METRIC_NAME = "net.shibboleth.idp.authn.duo";

    /** Class logger. */
    @Nonnull @NotEmpty private final Logger log = LoggerFactory.getLogger(ValidateUsernameAgainstDuoAuthApi.class);

    /** DuoApi context for tokens. **/
    @Nonnull @NotEmpty private DuoAuthenticationContext duoContext;

    /** Duo integration to use. */
    @Nullable private DuoIntegration duoIntegration;

    /** Attempted username. */
    @Nullable @NotEmpty private String username;

    /** Lookp strategy for Duo integration. */
    @Nonnull private Function<ProfileRequestContext, DuoIntegration> duoIntegrationLookupStrategy;

    /** Lookup strategy for username to match against Duo identity. */
    @Nonnull private Function<ProfileRequestContext, String> usernameLookupStrategy;

    /** implementation of Duo AuthApi /auth endpoint. */
    @Nonnull private DuoAuthAuthenticator authAuthenticator;

    /** implementation of Duo AuthApi /preauth enpoint. */
    @Nonnull private DuoPreauthAuthenticator preauthAuthenticator;
    
    /** Constructor. */
    public ValidateUsernameAgainstDuoAuthApi() {
        duoIntegrationLookupStrategy = FunctionSupport.constant(null);
        usernameLookupStrategy = new CanonicalUsernameLookupStrategy();
        setMetricName(DEFAULT_METRIC_NAME);
    }

    /**
     * Set DuoIntegration lookup strategy to use.
     * 
     * @param strategy lookup strategy
     */
    public void
            setDuoIntegrationLookupStrategy(@Nonnull final Function<ProfileRequestContext, DuoIntegration> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        duoIntegrationLookupStrategy = Constraint.isNotNull(strategy, "DuoIntegration lookup strategy cannot be null");
    }

    /**
     * Set DuoIntegration details to use directly.
     * 
     * @param duo Duo integration details
     */
    public void setDuoIntegration(@Nonnull final DuoIntegration duo) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        Constraint.isNotNull(duo, "DuoIntegration cannot be null");
        duoIntegrationLookupStrategy = FunctionSupport.constant(duo);
    }

    /**
     * Set the lookup strategy to use for the username to match against Duo identity.
     * 
     * @param strategy lookup strategy
     */
    public void setUsernameLookupStrategy(@Nonnull final Function<ProfileRequestContext, String> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        usernameLookupStrategy = Constraint.isNotNull(strategy, "Username lookup strategy cannot be null");
    }

    /**
     * Set the {@link DuoAuthAuthenticator}.
     * 
     * @param authenticator a Duo AuthApi /auth endpoint implementation
     */
    public void setAuthAuthenticator(@Nonnull final DuoAuthAuthenticator authenticator) {
        authAuthenticator = authenticator;
    }

    /**
     * Set the {@link DuoPreauthAuthenticator}.
     * 
     * @param authenticator a Duo AuthApi /preauth endpoint implementation
     */
    public void setPreauthAuthenticator(@Nonnull final DuoPreauthAuthenticator authenticator) {
        preauthAuthenticator = authenticator;
    }

    /** {@inheritDoc} */
    @Override protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();

        if (authAuthenticator == null) {
            throw new ComponentInitializationException("Duo authAuthenticator cannot be null");
        }

        if (preauthAuthenticator == null) {
            throw new ComponentInitializationException("Duo preauthAuthenticator cannot be null");
        }
    }

    /** {@inheritDoc} */
    @Override protected boolean doPreExecute(@Nonnull final ProfileRequestContext profileRequestContext,
            @Nonnull final AuthenticationContext authenticationContext) {

        if (!super.doPreExecute(profileRequestContext, authenticationContext)) {
            return false;
        }

        duoIntegration = duoIntegrationLookupStrategy.apply(profileRequestContext);
        if (duoIntegration == null) {
            log.warn("{} No DuoIntegration returned by lookup strategy", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_PROFILE_CTX);
            recordFailure();
            return false;
        }

        username = usernameLookupStrategy.apply(profileRequestContext);
        if (username == null) {
            log.warn("{} No principal name available to cross-check Duo result", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, AuthnEventIds.NO_CREDENTIALS);
            return false;
        }

        duoContext = authenticationContext.getSubcontext(DuoAuthenticationContext.class);
        if (duoContext == null) {
            log.info("{} No DuoApiContext available within authentication context", getLogPrefix());
            handleError(profileRequestContext, authenticationContext, "Duo context missing",
                    AuthnEventIds.INVALID_AUTHN_CTX);
            recordFailure();
            return false;
        } else if (duoContext.getFactor() == null) {
            log.info("{} No factor available within DuoApiContext", getLogPrefix());
            handleError(profileRequestContext, authenticationContext, "No Duo factor",
                    AuthnEventIds.REQUEST_UNSUPPORTED);
            recordFailure();
            return false;
        }

        duoContext.setUsername(username);

        return true;
    }

    /** {@inheritDoc} */
    // CheckStyle: ReturnCount OFF
    @Override protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext,
            @Nonnull final AuthenticationContext authenticationContext) {

        log.trace("{} Triggering Duo auto authentication", getLogPrefix());

        // Make API call for phone verification
        try {

            // Duo AuthApi pre-authentication
            final DuoPreauthResponse preAuthResponse = preauthAuthenticator.authenticate(duoContext, duoIntegration);

            if (preAuthResponse == null) {
                log.info("{} Duo API preauthentication response missing", getLogPrefix());
                throw new DuoWebException("missing preauthentication response");
            }

            final String preAuthResult = preAuthResponse.getResult();

            if (preAuthResult.equals(DuoAuthApi.DUO_PREAUTH_RESULT_ALLOW)) {
                // user in bypass mode; treat as authenticated
                log.info("{} Duo pre-authentication (bypass) succeeded for '{}'", getLogPrefix(), username);
                recordSuccess();
                authenticationContext.getSubcontext(DuoResponseContext.class, true)
                        .setAuthenticationResponse(preAuthResponse);
                buildAuthenticationResult(profileRequestContext, authenticationContext);
                return;
            }

            if (!preAuthResult.equals(DuoAuthApi.DUO_PREAUTH_RESULT_AUTH)) {
                // either deny or enroll
                log.info("{} Duo pre-authentication failed for '{}': {}", getLogPrefix(), username,
                        preAuthResponse.getStatusMessage());
                handleError(profileRequestContext, authenticationContext,
                        String.format("%s:%s:%s", preAuthResult, username, preAuthResponse.getStatusMessage()),
                        AuthnEventIds.ACCOUNT_ERROR);
                recordFailure();
                return;
            }

            // Duo AuthAPI authentication
            final DuoAuthResponse authenticationResponse = authAuthenticator.authenticate(duoContext, duoIntegration);

            if (authenticationResponse == null) {
                log.info("{} Duo API preauthentication response missing", getLogPrefix());
                throw new DuoWebException("missing preauthentication response");
            }

            final String authResult = authenticationResponse.getResult();

            if (authResult.equals(DuoAuthApi.DUO_AUTH_RESULT_ALLOW)) {
                log.info("{} Duo authentication succeeded for '{}'", getLogPrefix(), username);
                recordSuccess();
                authenticationContext.getSubcontext(DuoResponseContext.class, true)
                        .setAuthenticationResponse(authenticationResponse);
                buildAuthenticationResult(profileRequestContext, authenticationContext);
            } else if (authResult.equals(DuoAuthApi.DUO_AUTH_RESULT_DENY)) {
                log.info("{} Duo authentication failed for '{}'", getLogPrefix(), username);
                handleError(profileRequestContext, authenticationContext, AuthnEventIds.INVALID_CREDENTIALS,
                        AuthnEventIds.INVALID_CREDENTIALS);
                recordFailure();
                return;
            } else {
                throw new DuoWebException("unexpected authentication response");
            }
        } catch (final Exception e) {
            log.error("{} Duo AuthAPI by {} produced exception", getLogPrefix(), username, e);
            handleError(profileRequestContext, authenticationContext, e, AuthnEventIds.AUTHN_EXCEPTION);
            recordFailure();
            return;
        }
    }
    // CheckStyle: ReturnCount OFF

    /** {@inheritDoc} */
    @Override protected Subject populateSubject(@Nonnull final Subject subject) {
        subject.getPrincipals().add(new DuoPrincipal(username));
        subject.getPrincipals().addAll(duoIntegration.getSupportedPrincipals(Principal.class));
        return subject;
    }

    /** {@inheritDoc} */
    @Override protected void buildAuthenticationResult(@Nonnull final ProfileRequestContext profileRequestContext,
            @Nonnull final AuthenticationContext authenticationContext) {
        super.buildAuthenticationResult(profileRequestContext, authenticationContext);

        // Bypass c14n. We already operate on a canonical name, so just re-confirm it.
        profileRequestContext.getSubcontext(SubjectCanonicalizationContext.class, true).setPrincipalName(username);
    }

}