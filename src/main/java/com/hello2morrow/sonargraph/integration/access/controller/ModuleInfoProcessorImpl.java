package com.hello2morrow.sonargraph.integration.access.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.hello2morrow.sonargraph.integration.access.model.IDependencyIssue;
import com.hello2morrow.sonargraph.integration.access.model.IElementIssue;
import com.hello2morrow.sonargraph.integration.access.model.IIssue;
import com.hello2morrow.sonargraph.integration.access.model.IMetricId;
import com.hello2morrow.sonargraph.integration.access.model.IMetricLevel;
import com.hello2morrow.sonargraph.integration.access.model.IMetricValue;
import com.hello2morrow.sonargraph.integration.access.model.INamedElement;
import com.hello2morrow.sonargraph.integration.access.model.IResolution;
import com.hello2morrow.sonargraph.integration.access.model.ISourceFile;
import com.hello2morrow.sonargraph.integration.access.model.internal.ModuleImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.SoftwareSystemImpl;

final class ModuleInfoProcessorImpl implements IModuleInfoProcessor
{
    private final ModuleImpl m_module;
    private final ISystemInfoProcessor m_systemInfoProcessor;

    public ModuleInfoProcessorImpl(final SoftwareSystemImpl softwareSystem, final ModuleImpl module)
    {
        assert softwareSystem != null : "Parameter 'softwareSystem' of method 'ModuleInfoProcessorImpl' must not be null";
        assert module != null : "Parameter 'module' of method 'ModuleInfoProcessorImpl' must not be null";

        m_systemInfoProcessor = new SystemInfoProcessorImpl(softwareSystem);
        m_module = module;
    }

    @Override
    public String getBaseDirectory()
    {
        return m_systemInfoProcessor.getBaseDirectory();
    }

    @Override
    public List<IIssue> getIssues(final Predicate<IIssue> filter)
    {
        final List<IIssue> systemIssues = m_systemInfoProcessor.getIssues(filter);
        return Collections.unmodifiableList(systemIssues.stream().filter((final IIssue issue) -> isModuleElementOriginOfIssue(issue))
                .collect(Collectors.toList()));
    }

    private boolean isModuleElementOriginOfIssue(final IIssue issue)
    {
        assert issue != null : "Parameter 'issue' of method 'isModuleElementInvolved' must not be null";

        if (issue instanceof IElementIssue)
        {
            final List<INamedElement> element = ((IElementIssue) issue).getAffectedElements();
            return element.stream().anyMatch((final INamedElement e) -> isElementContainedInModule(e));
        }
        if (issue instanceof IDependencyIssue)
        {
            final IDependencyIssue dependencyIssue = (IDependencyIssue) issue;
            return isElementContainedInModule(dependencyIssue.getFrom());
        }
        return true;
    }

    @Override
    public boolean isElementContainedInModule(final INamedElement element)
    {
        assert element != null : "Parameter 'element' of method 'isElementContainedInModule' must not be null";
        return m_module.hasElement(element);
    }

    @Override
    public List<IResolution> getResolutions(final Predicate<IResolution> filter)
    {
        final List<IResolution> systemResolutions = m_systemInfoProcessor.getResolutions(filter);

        final List<IResolution> moduleResolutions = new ArrayList<>();
        for (final IResolution next : systemResolutions)
        {
            if (next.getIssues().stream().filter((final IIssue issue) -> isModuleElementOriginOfIssue(issue)).findAny().isPresent())
            {
                moduleResolutions.add(next);
            }
        }
        return Collections.unmodifiableList(moduleResolutions);
    }

    @Override
    public Optional<IMetricId> getMetricId(final IMetricLevel level, final String metricId)
    {
        return m_module.getMetricIdsForLevel(level).stream().filter((final IMetricId id) -> id.getName().equals(metricId)).findAny();
    }

    @Override
    public Optional<IMetricValue> getMetricValueForElement(final IMetricId metricId, final IMetricLevel level, final String fqName)
    {
        return m_module.getMetricValueForElement(metricId, level, fqName);
    }

    @Override
    public List<IMetricId> getMetricIdsForLevel(final IMetricLevel level)
    {
        assert level != null : "Parameter 'level' of method 'getMetricIdsForLevel' must not be null";

        return m_module.getMetricIdsForLevel(level);
    }

    @Override
    public Map<ISourceFile, List<IIssue>> getIssuesForSourceFiles(final Predicate<IIssue> filter)
    {
        final List<IIssue> systemIssues = m_systemInfoProcessor.getIssues(filter);
        final Map<ISourceFile, List<IIssue>> resultMap = new HashMap<>();
        for (final IIssue issue : systemIssues)
        {
            if (issue instanceof IElementIssue)
            {
                final List<INamedElement> elements = ((IElementIssue) issue).getAffectedElements();
                for (final INamedElement next : elements)
                {
                    addSourceForIssue(resultMap, issue, next);
                }
            }
            if (issue instanceof IDependencyIssue)
            {
                final IDependencyIssue dependencyIssue = (IDependencyIssue) issue;
                addSourceForIssue(resultMap, issue, dependencyIssue.getFrom());
            }
        }

        return Collections.unmodifiableMap(resultMap);
    }

    private void addSourceForIssue(final Map<ISourceFile, List<IIssue>> resultMap, final IIssue issue, final INamedElement element)
    {
        assert resultMap != null : "Parameter 'resultMap' of method 'addSourceForIssue' must not be null";
        assert issue != null : "Parameter 'issue' of method 'addSourceForIssue' must not be null";
        assert element != null : "Parameter 'element' of method 'addSourceForIssue' must not be null";

        if (isElementContainedInModule(element))
        {
            final Optional<ISourceFile> source = m_module.getSourceForElement(element);
            if (source.isPresent())
            {
                final ISourceFile sourceFile = source.get();
                if (!resultMap.containsKey(sourceFile))
                {
                    resultMap.put(sourceFile, new ArrayList<IIssue>());
                }

                resultMap.get(sourceFile).add(issue);
            }
        }
    }

    @Override
    public Map<INamedElement, IMetricValue> getMetricValues(final String levelName, final String metricIdName)
    {
        return m_module.getMetricValues(levelName, metricIdName);
    }

    @Override
    public List<IMetricLevel> getMetricLevels()
    {
        return Collections.unmodifiableList(new ArrayList<>(m_module.getMetricLevels().values()));
    }

    @Override
    public Optional<IMetricLevel> getMetricLevel(final String level)
    {
        assert level != null && level.length() > 0 : "Parameter 'level' of method 'getMetricLevel' must not be empty";
        return Optional.ofNullable(m_module.getMetricLevels().get(level));
    }

    @Override
    public Optional<IMetricValue> getMetricValue(final String metricName)
    {
        return Optional.ofNullable(m_module.getMetricValues(IMetricLevel.MODULE, metricName).get(m_module));
    }
}