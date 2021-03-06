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

import java.util.Optional;

import com.hello2morrow.sonargraph.integration.access.model.INamedElement;
import com.hello2morrow.sonargraph.integration.access.model.ISourceFile;

public class NamedElementImpl extends ElementWithDescriptionImpl implements INamedElement
{
    private static final long serialVersionUID = 7897215356427497745L;
    private final String kind;
    private final String presentationKind;
    private final int line;
    private final String fqName;
    private ISourceFile m_source;

    protected NamedElementImpl(final String kind, final String presentationKind, final String name, final String presentationName,
            final String fqName, final int line, final String description)
    {
        super(name, presentationName, description);

        assert fqName != null && fqName.length() > 0 : "Parameter 'fqName' of method 'NamedElementImpl' must not be empty";

        this.kind = kind;
        this.presentationKind = presentationKind;
        this.line = line;
        this.fqName = fqName;
    }

    public NamedElementImpl(final String kind, final String presentationKind, final String name, final String presentationName, final String fqName,
            final int line)
    {
        this(kind, presentationKind, name, presentationName, fqName, line, "");
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.IFqNamedElement#getKind()
     */
    @Override
    public final String getKind()
    {
        return kind;
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.IFqNamedElement#getFqName()
     */
    @Override
    public final String getFqName()
    {
        return fqName;
    }

    public final void setSourceFile(final ISourceFile source)
    {
        assert source != null : "Parameter 'source' of method 'setSourceFile' must not be null";
        m_source = source;
    }

    @Override
    public final Optional<ISourceFile> getSourceFile()
    {
        return Optional.ofNullable(m_source);
    }

    /* (non-Javadoc)
     * @see com.hello2morrow.sonargraph.integration.access.model.IFqNamedElement#getPresentationKind()
     */
    @Override
    public final String getPresentationKind()
    {
        return presentationKind;
    }

    @Override
    public final int getLineNumber()
    {
        return line;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + fqName.hashCode();
        result = prime * result + ((kind == null) ? 0 : kind.hashCode());
        result = prime * result + line;
        result = prime * result + ((presentationKind == null) ? 0 : presentationKind.hashCode());
        return result;
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
        final NamedElementImpl other = (NamedElementImpl) obj;
        if (fqName == null)
        {
            if (other.fqName != null)
            {
                return false;
            }
        }
        else if (!fqName.equals(other.fqName))
        {
            return false;
        }
        if (kind == null)
        {
            if (other.kind != null)
            {
                return false;
            }
        }
        else if (!kind.equals(other.kind))
        {
            return false;
        }
        if (line != other.line)
        {
            return false;
        }
        if (presentationKind == null)
        {
            if (other.presentationKind != null)
            {
                return false;
            }
        }
        else if (!presentationKind.equals(other.presentationKind))
        {
            return false;
        }
        return true;
    }
}