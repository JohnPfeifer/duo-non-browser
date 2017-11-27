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
import javax.servlet.http.HttpServletRequest;

import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.net.HttpHeaders;

import net.shibboleth.idp.authn.AbstractExtractionAction;
import net.shibboleth.idp.authn.AuthnEventIds;
import net.shibboleth.idp.authn.context.AuthenticationContext;
import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;
import edu.umd.idm.shibboleth.idp.authn.context.DuoAuthenticationContext;
import edu.umd.idm.shibboleth.idp.authn.duo.DuoAuthApi;

/**
 * An action that extracts the Duo factor and device or passcode from the HTTP {@link HttpHeaders} headers or the
 * incoming request {@link HrrpServletRequest} parameters, creates a {@link DuoAuthenticationContext}, and attaches it
 * to the {@link AuthenticationContext}. The header values will take precedence over the parameters values.
 * 
 * @event {@link org.opensaml.profile.action.EventIds#PROCEED_EVENT_ID}
 * @event {@link AuthnEventIds#NO_CREDENTIALS}
 * @event {@link AuthnEventIds#INVALID_CREDENTIALS}
 * @pre
 * 
 *      <pre>
 *      ProfileRequestContext.getSubcontext(AuthenticationContext.class, false) != null
 *      </pre>
 * 
 * @post If getHttpServletRequest() != null, the content of the {@link HttpRequestSerlver} url parameters and the
 *       {@link HttpHeaders} are checked. The information found will be attached via a {@link DuoAuthenticationContext}.
 */
@SuppressWarnings("rawtypes")
public class ExtractDuoAuthticationContext extends AbstractExtractionAction {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(ExtractDuoAuthticationContext.class);

    /** flag indication if "auto" should be the default for factor and device. */
    private boolean autoAuthentiationSupoorted;

    /** Header name for factor. */
    @Nonnull @NotEmpty private String factorHeaderName;

    /** Header name for device. */
    @Nonnull @NotEmpty private String deviceHeaderName;

    /** Header name for passcode. */
    @Nonnull @NotEmpty private String passcodeHeaderName;

    /** parameter name for factor. */
    @Nonnull @NotEmpty private String factorParameterName;

    /** parameter name for device. */
    @Nonnull @NotEmpty private String deviceParameterName;

    /** parameter name for passcode. */
    @Nonnull @NotEmpty private String passcodeParameterName;

    /** flag indicating if HTTP headers should be examined. */
    private boolean useHeaders;

    /** flag indicating if HTTP parameteres should be examined. */
    private boolean useParameters;

    /** Constructor. */
    ExtractDuoAuthticationContext() {
        autoAuthentiationSupoorted = true;
        useHeaders = true;
        useParameters = true;

        factorHeaderName = "X-Shiboleth-Duo-Factor";
        deviceHeaderName = "X-Shiboleth-Duo-Device";
        passcodeHeaderName = "X-Shiboleth-Duo-Passcode";

        factorParameterName = "duoFactor";
        deviceParameterName = "duoDevice";
        passcodeParameterName = "duoPasscode";
    }

    /**
     * Set the factor header name.
     * 
     * @param headerName the factor header name
     */
    public void setFactorheaderName(@Nonnull @NotEmpty final String headerName) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        factorHeaderName = Constraint.isNotNull(StringSupport.trimOrNull(headerName),
                "Factor header name cannot be null or empty.");
    }

    /**
     * Set the device header name.
     * 
     * @param headerName the factor header name
     */
    public void setDeviceheaderName(@Nonnull @NotEmpty final String headerName) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        deviceHeaderName = Constraint.isNotNull(StringSupport.trimOrNull(headerName),
                "Device header name cannot be null or empty.");
    }

    /**
     * Set the passcode header name.
     * 
     * @param headerName the factor header name
     */
    public void setPasscodeheaderName(@Nonnull @NotEmpty final String headerName) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        passcodeHeaderName = Constraint.isNotNull(StringSupport.trimOrNull(headerName),
                "Passcode header name cannot be null or empty.");
    }

    /**
     * Set the factor parameter name.
     * 
     * @param parameterName the factor parameter name
     */
    public void setFactorParameterName(@Nonnull @NotEmpty final String parameterName) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        factorParameterName = Constraint.isNotNull(StringSupport.trimOrNull(parameterName),
                "Factor parameter name cannot be null or empty.");
    }

    /**
     * Set the device parameter name.
     * 
     * @param parameterName the factor parameter name
     */
    public void setDeviceParameterName(@Nonnull @NotEmpty final String parameterName) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        deviceParameterName = Constraint.isNotNull(StringSupport.trimOrNull(parameterName),
                "Device parameter name cannot be null or empty.");
    }

    /**
     * Set the passcode parameter name.
     * 
     * @param parameterName the factor parameter name
     */
    public void setPasscodeParameterName(@Nonnull @NotEmpty final String parameterName) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        passcodeParameterName = Constraint.isNotNull(StringSupport.trimOrNull(parameterName),
                "Passcode parameter name cannot be null or empty.");
    }

    /**
     * Is "auto" the default setting.
     * 
     * @return foo
     */
    public boolean isAutoAuthenticationSupported() {
        return autoAuthentiationSupoorted;
    }

    /**
     * Set the "auto" authentication support flag.
     * 
     * @param supported the flag
     */
    public void setAutoAuthenticationSupported(final boolean supported) {
        autoAuthentiationSupoorted = supported;
    }

    /**
     * Set the useHeaders flag indicating if the HTTP headers should be examined for Duo properties.
     * 
     * @param useHTTPHeaders the setting
     */
    public void setUseHeaders(final boolean useHTTPHeaders) {
        useHeaders = useHTTPHeaders;
    }

    /**
     * Set the useParameters flag indicating if the HTTP parameters should be examined for Duo properties.
     * 
     * @param useHTTPParameters the setting
     */
    public void setUseParameters(final boolean useHTTPParameters) {
        useParameters = useHTTPParameters;
    }

    /** {@inheritDoc} */
    // CheckStyle: ReturnCount OFF
    @Override protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext,
            @Nonnull final AuthenticationContext authenticationContext) {

        // initialize the Duo context
        final DuoAuthenticationContext duoCtx =
                authenticationContext.getSubcontext(DuoAuthenticationContext.class, true);
        duoCtx.setFactor(isAutoAuthenticationSupported() ? DuoAuthApi.DUO_FACTOR_AUTO : null);
        duoCtx.setDevice(null);
        duoCtx.setPasscode(null);

        // get the request context
        final HttpServletRequest request = getHttpServletRequest();
        if (request == null) {
            log.debug("{} Profile action does not contain an HttpServletRequest", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, AuthnEventIds.NO_CREDENTIALS);
            return;
        }

        // examine the URL parameters
        if (useParameters) {
            extractParameters(duoCtx, request);
        }

        // examine the HTTP headers
        if (useHeaders) {
            extractHeaders(duoCtx, request);
        }

        // verify that the factor is not null
        if (duoCtx.getFactor() == null) {
            log.debug("{} Profile action does not contain a Duo factor", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, AuthnEventIds.REQUEST_UNSUPPORTED);
            return;
        }

        // set the device to "auto" if needed
        if (isAutoAuthenticationSupported() && (duoCtx.getDevice() == null)
                && !duoCtx.getFactor().equals(DuoAuthApi.DUO_FACTOR_PASSCODE)) {
            duoCtx.setDevice(DuoAuthApi.DUO_DEVICE_AUTO);
        }

    }
    // CheckStyle: ReturnCount ON

    /**
     * Extracts the Duo API arguments passed in via the {@link HttpHeaders} request headers.
     * 
     * @param context the DuoApiAuthContext to store the parameters in
     * @param request current HTTP request
     * 
     * @return the DuoApiAuthContext
     */
    protected DuoAuthenticationContext extractHeaders(@Nonnull final DuoAuthenticationContext context,
            @Nonnull final HttpServletRequest request) {

        final String factor = request.getHeader(factorHeaderName);
        if (factor != null && !factor.isEmpty()) {
            context.setFactor(factor);
        }

        final String device = request.getHeader(deviceHeaderName);
        if (device != null && !device.isEmpty()) {
            context.setDevice(device);
        }

        final String passcode = request.getHeader(passcodeHeaderName);
        if (passcode != null && !passcode.isEmpty()) {
            context.setPasscode(passcode);
        }

        return context;
    }

    /**
     * Extracts the Duo API arguments passed in via the {@link HrrpServletRequest} parameters.
     * 
     * @param context the DuoApiAuthContext to store the parameters in
     * @param request current HTTP request
     * 
     * @return the DuoApiAuthContext
     */
    protected DuoAuthenticationContext extractParameters(@Nonnull final DuoAuthenticationContext context,
            @Nonnull final HttpServletRequest request) {

        final String factor = request.getParameter(factorParameterName);
        if (factor != null && !factor.isEmpty()) {
            context.setFactor(factor);
        }

        final String device = request.getParameter(deviceParameterName);
        if (device != null && !device.isEmpty()) {
            context.setDevice(device);
        }

        final String passcode = request.getParameter(passcodeParameterName);
        if (passcode != null && !passcode.isEmpty()) {
            context.setPasscode(passcode);
        }

        return context;
    }

}
