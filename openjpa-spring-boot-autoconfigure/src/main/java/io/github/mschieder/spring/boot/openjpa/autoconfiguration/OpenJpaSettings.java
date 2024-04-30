/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.mschieder.spring.boot.openjpa.autoconfiguration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Supplier;

/**
 * Settings to apply when configuring Hibernate.
 *
 * @author Andy Wilkinson
 * @since 2.0.0
 */
public class OpenJpaSettings {

    private Supplier<String> ddlAuto;

    private Collection<OpenJpaPropertiesCustomizer> openJpaPropertiesCustomizers;

    public OpenJpaSettings ddlAuto(Supplier<String> ddlAuto) {
        this.ddlAuto = ddlAuto;
        return this;
    }

    public String getDdlAuto() {
        return (this.ddlAuto != null) ? this.ddlAuto.get() : null;
    }

    public OpenJpaSettings openJpaPropertiesCustomizers(
            Collection<OpenJpaPropertiesCustomizer> openJpaPropertiesCustomizers) {
        this.openJpaPropertiesCustomizers = new ArrayList<>(openJpaPropertiesCustomizers);
        return this;
    }

    public Collection<OpenJpaPropertiesCustomizer> getOpenJpaPropertiesCustomizers() {
        return this.openJpaPropertiesCustomizers;
    }

}

