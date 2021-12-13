/*
 * Copyright 2021 OPPO ESA Stack Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package esa.commons.logging;

import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;

@TargetClass(className = "esa.commons.logging.LoggerFactory")
public final class LoggerFactorySubstitute {

    @Substitute
    private static LoggerDelegateFactory init() {
        LoggerDelegateFactory logger = JdkLoggerDelegateFactory.INSTANCE;
        logger.create(LoggerFactory.class.getName()).debug("Using jdk logger as the logging framework.");

        return logger;
    }

    LoggerFactorySubstitute() {
    }

}

