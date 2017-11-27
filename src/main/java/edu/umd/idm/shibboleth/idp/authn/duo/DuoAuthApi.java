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

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;

/**
 * Constants defined in the Duo AuthAPI.
 */
public final class DuoAuthApi {

    /** Duo AuthApi parameter name. */
    @Nonnull @NotEmpty public static final String DUO_FACTOR = "factor";

    /** Duo AuthApi parameter name. */
    @Nonnull @NotEmpty public static final String DUO_DEVICE = "device";

    /** Duo AuthApi parameter name. */
    @Nonnull @NotEmpty public static final String DUO_PASSCODE = "passcode";

    /** Duo AuthApi factor "auto" value. */
    @Nonnull @NotEmpty public static final String DUO_FACTOR_AUTO = "auto";

    /** Duo AuthApi factor "push" value. */
    @Nonnull @NotEmpty public static final String DUO_FACTOR_PUSH = "push";

    /** Duo AuthApi factor "passcode" value. */
    @Nonnull @NotEmpty public static final String DUO_FACTOR_PASSCODE = "passcode";

    /** Duo AuthApi factor "sms" value. */
    @Nonnull @NotEmpty public static final String DUO_FACTOR_SMS = "sms";

    /** Duo AuthApi factor "enum" value. */
    @Nonnull @NotEmpty public static final String DUO_FACTOR_PHONE = "phone";

    /** Duo AuthApi device "auto" value. */
    @Nonnull @NotEmpty public static final String DUO_DEVICE_AUTO = "auto";

    /** Duo AuthApi preauth "allow" result value. */
    @Nonnull @NotEmpty public static final String DUO_PREAUTH_RESULT_ALLOW = "allow";

    /** Duo AuthApi preauth "auth" result value. */
    @Nonnull @NotEmpty public static final String DUO_PREAUTH_RESULT_AUTH = "auth";

    /** Duo AuthApi preauth "deny" result value. */
    @Nonnull @NotEmpty public static final String DUO_PREAUTH_RESULT_DENY = "deny";

    /** Duo AuthApi preauth "enroll" result value. */
    @Nonnull @NotEmpty public static final String DUO_PREAUTH_RESULT_ENROLL = "enroll";

    /** Duo AuthApi auth "allow" result value. */
    @Nonnull @NotEmpty public static final String DUO_AUTH_RESULT_ALLOW = "allow";

    /** Duo AuthApi auth "deny" result value. */
    @Nonnull @NotEmpty public static final String DUO_AUTH_RESULT_DENY = "deny";

    /** Duo AuthApi auth "bypass" result value. */
    @Nonnull @NotEmpty public static final String DUO_AUTH_STATUS_BYPASS = "bypass";

    /** Constructor. */
    private DuoAuthApi() {
    }

}
