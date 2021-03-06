/**
 * Sonargraph Integration Access
 * Copyright (C) 2016 hello2morrow GmbH
 * mailto: support AT hello2morrow DOT com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hello2morrow.sonargraph.integration.access.model.internal;

import com.hello2morrow.sonargraph.integration.access.model.IIssueCategory;
import com.hello2morrow.sonargraph.integration.access.model.IIssueProvider;
import com.hello2morrow.sonargraph.integration.access.model.IIssueType;
import com.hello2morrow.sonargraph.integration.access.model.Severity;

public class IssueTypeImpl extends ElementImpl implements IIssueType
{
    private static final long serialVersionUID = -8081570866899153886L;
    private final IIssueCategory category;
    private final Severity severity;
    private final String description;
    private final IIssueProvider provider;

    public IssueTypeImpl(final String name, final String presentationName, final Severity severity, final IIssueCategory category,
            final IIssueProvider provider, final String description)
    {
        super(name, presentationName);
        assert severity != null : "Parameter 'severity' of method 'IssueType' must not be null";
        assert category != null : "Parameter 'category' of method 'IssueType' must not be null";

        this.severity = severity;
        this.category = category;
        this.description = description != null ? description : "";
        this.provider = provider;
    }

    @Override
    public IIssueCategory getCategory()
    {
        return category;
    }

    @Override
    public Severity getSeverity()
    {
        return severity;
    }

    @Override
    public String getDescription()
    {
        return description;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((category == null) ? 0 : category.hashCode());
        result = prime * result + ((severity == null) ? 0 : severity.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((provider == null) ? 0 : provider.hashCode());
        return result;
    }

    @Override
    public IIssueProvider getProvider()
    {
        return provider;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (!super.equals(obj))
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final IssueTypeImpl other = (IssueTypeImpl) obj;
        if (category == null)
        {
            if (other.category != null)
            {
                return false;
            }
        }
        else if (!category.equals(other.category))
        {
            return false;
        }

        if (description == null)
        {
            if (other.description != null)
            {
                return false;
            }
        }
        else if (!description.equals(other.description))
        {
            return false;
        }

        if (provider == null)
        {
            if (other.provider != null)
            {
                return false;
            }
        }
        else if (!provider.equals(other.provider))
        {
            return false;
        }

        if (severity != other.severity)
        {
            return false;
        }
        return true;
    }
}
