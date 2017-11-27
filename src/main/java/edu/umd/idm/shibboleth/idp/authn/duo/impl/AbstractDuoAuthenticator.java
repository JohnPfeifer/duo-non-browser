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

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;

import com.duosecurity.duoweb.DuoWebException;

import edu.umd.idm.shibboleth.idp.authn.duo.DuoAuthApiAuthenticator;
import net.shibboleth.utilities.java.support.component.AbstractInitializableComponent;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;

/**
 * A base class for authentication actions which call a Duo AuthApi endpont.
 */
@ThreadSafe
public abstract class AbstractDuoAuthenticator extends AbstractInitializableComponent
        implements DuoAuthApiAuthenticator {

    /** HttoClient for contacting Duo. */
    @Nullable private HttpClient httpClient;

    /** JSON object mapper. */
    @Nullable private ObjectMapper objectMapper;

    /**
     * Get the {@link HttpClient} to use for contacting Duo.
     * 
     * @return HttpClient
     */
    public HttpClient getHttpClient() {
        return httpClient;
    }

    /**
     * Set the {@link HttpClient} to use for contacting Duo.
     * 
     * @param client HttpClient
     */
    public void setHttpClient(@Nonnull final HttpClient client) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        httpClient = Constraint.isNotNull(client, "HTTP client cannot be null");
    }

    /**
     * Get the JSON {@link ObjectMapper}.
     * 
     * @return ObjectMapper
     */
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    /**
     * Set the JSON {@link ObjectMapper}.
     * 
     * @param mapper object mapper
     */
    public void setObjectMapper(@Nonnull final ObjectMapper mapper) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        objectMapper = Constraint.isNotNull(mapper, "Object mapper cannot me null");
    }

    /** {@inheritDoc} */
    @Override protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();

        if (httpClient == null) {
            throw new ComponentInitializationException("HttpClient cannot be null");
        }

        if (objectMapper == null) {
            throw new ComponentInitializationException("ObjectMapper cannot be null");
        }
    }

    /**
     * Performs a call to the Duo AuthApi. Upon a successful call, the JSON response is mapped into the appropriate type
     * of {@link DuoReposnseWrapper}.
     * 
     * @param request the prepared HTTP request
     * @param wrapperTypeRef the type of {@link DuoReposnseWrapper} to use
     * @param <T> the DuoResponse type being wrapped
     * 
     * @return a {@link DuoReposnseWrapper}
     * 
     * @throws Exception request failure
     */
    protected <T extends DuoResponseWrapper<?>> T doApiRequest(@Nonnull final HttpUriRequest request,
            final TypeReference<T> wrapperTypeRef) throws Exception {
        final ObjectMapper mapper = getObjectMapper();

        // make the request
        final HttpResponse httpResponse = getHttpClient().execute(request);

        // check the HTTP response code
        final int httpStatusCode = httpResponse.getStatusLine().getStatusCode();
        if (httpStatusCode == HttpStatus.SC_BAD_REQUEST) {
            final InputStream httpContent = httpResponse.getEntity().getContent();
            final DuoFailureResponse msg = objectMapper.readValue(httpContent, DuoFailureResponse.class);
            throw new DuoWebException(msg.getMessage() + "(" + msg.getMessageDetail() + ")");
        }
        if (httpStatusCode != HttpStatus.SC_OK) {
            throw new IOException("Non-ok status code (" + httpStatusCode + ") returned from Duo: "
                    + httpResponse.getStatusLine().getReasonPhrase());
        }

        // parse the JSON response
        final T duoResponse = mapper.readValue(httpResponse.getEntity().getContent(), wrapperTypeRef);

        if (duoResponse == null) {
            throw new DuoWebException("Unable to parse JSON response");
        } else if (!duoResponse.getStat().equals("OK")) {
            throw new DuoWebException("Unexpected 'STAT' value JSON response: " + duoResponse.getStat());
        }

        return duoResponse;
    }

}
