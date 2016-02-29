package com.hello2morrow.sonargraph.integration.access.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hello2morrow.sonargraph.integration.access.foundation.IOMessageCause;
import com.hello2morrow.sonargraph.integration.access.foundation.OperationResultWithOutcome;
import com.hello2morrow.sonargraph.integration.access.model.IBasicSoftwareSystemInfo;
import com.hello2morrow.sonargraph.integration.access.model.IExportMetaData;
import com.hello2morrow.sonargraph.integration.access.model.IIssueCategory;
import com.hello2morrow.sonargraph.integration.access.model.IMergedExportMetaData;
import com.hello2morrow.sonargraph.integration.access.model.IMergedIssueCategory;
import com.hello2morrow.sonargraph.integration.access.model.IMetricCategory;
import com.hello2morrow.sonargraph.integration.access.model.IMetricId;
import com.hello2morrow.sonargraph.integration.access.model.IMetricProvider;
import com.hello2morrow.sonargraph.integration.access.model.ISingleExportMetaData;
import com.hello2morrow.sonargraph.integration.access.model.internal.MergedExportMetaDataImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.MergedIssueCategoryImpl;
import com.hello2morrow.sonargraph.integration.access.model.internal.SingleExportMetaDataImpl;
import com.hello2morrow.sonargraph.integration.access.persistence.XmlExportMetaDataReader;

class MetaDataControllerImpl implements IMetaDataController
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MetaDataControllerImpl.class);

    @Override
    public OperationResultWithOutcome<IExportMetaData> loadExportMetaData(final File exportMetaDataFile)
    {
        assert exportMetaDataFile != null : "Parameter 'exportMetaDataFile' of method 'loadExportMetaData' must not be null";

        final OperationResultWithOutcome<IExportMetaData> result = new OperationResultWithOutcome<IExportMetaData>("Load meta-data from '"
                + exportMetaDataFile + "'");
        try (final FileInputStream inputStream = new FileInputStream(exportMetaDataFile))
        {
            final OperationResultWithOutcome<IExportMetaData> loadExportMetaData = loadExportMetaData(inputStream,
                    exportMetaDataFile.getCanonicalPath());
            result.addMessagesFrom(loadExportMetaData);
            result.setOutcome(loadExportMetaData.getOutcome());
        }
        catch (final FileNotFoundException ex)
        {
            result.addError(IOMessageCause.FILE_NOT_FOUND, ex);
        }
        catch (final IOException ex)
        {
            result.addError(IOMessageCause.IO_EXCEPTION, ex);
        }
        return result;
    }

    @Override
    public OperationResultWithOutcome<IExportMetaData> loadExportMetaData(final InputStream inputStream, final String identifier)
    {
        assert inputStream != null : "Parameter 'inputStream' of method 'loadExportMetaData' must not be null";
        assert identifier != null && identifier.length() > 0 : "Parameter 'identifier' of method 'loadExportMetaData' must not be empty";

        final OperationResultWithOutcome<IExportMetaData> result = new OperationResultWithOutcome<>("Load data from stream");
        final OperationResultWithOutcome<ISingleExportMetaData> readResult = internLoadExportMetaData(inputStream, identifier);
        result.addMessagesFrom(readResult);
        result.setOutcome(readResult.getOutcome());
        return result;
    }

    private OperationResultWithOutcome<ISingleExportMetaData> internLoadExportMetaData(final InputStream inputStream, final String identifier)
    {
        assert inputStream != null : "Parameter 'inputStream' of method 'loadExportMetaData' must not be null";

        final OperationResultWithOutcome<ISingleExportMetaData> result = new OperationResultWithOutcome<>("Load data from stream");
        final XmlExportMetaDataReader persistence = new XmlExportMetaDataReader();
        final Optional<SingleExportMetaDataImpl> readResult = persistence.readMetaDataFromStream(inputStream, identifier, result);
        if (!readResult.isPresent() && result.isSuccess())
        {
            result.addError(IOMessageCause.READ_ERROR, "Failed to read meta-data from stream");
        }

        if (result.isFailure())
        {
            return result;
        }

        if (readResult.isPresent())
        {
            result.setOutcome(readResult.get());
        }
        return result;
    }

    @Override
    public OperationResultWithOutcome<IMergedExportMetaData> mergeExportMetaDataFiles(final List<File> files)
    {
        assert files != null && files.size() > 0 : "Parameter 'files' of method 'mergeExportMetaDataFiles' must not be empty";

        final OperationResultWithOutcome<IMergedExportMetaData> result = new OperationResultWithOutcome<IMergedExportMetaData>(
                "Merge meta-data from files");

        final Map<String, ISingleExportMetaData> exportDataMap = new LinkedHashMap<>();
        final Map<String, IBasicSoftwareSystemInfo> systemMap = new HashMap<>();

        for (final File file : files)
        {
            if (!file.exists())
            {
                result.addWarning(IOMessageCause.FILE_NOT_FOUND, "File '" + file.getAbsolutePath() + "' does not exist.");
                continue;
            }

            if (!file.canRead())
            {
                result.addWarning(IOMessageCause.NO_PERMISSON, "No permission to read file '" + file.getAbsolutePath() + "'");
                continue;
            }

            try (FileInputStream inputStream = new FileInputStream(file))
            {
                final OperationResultWithOutcome<ISingleExportMetaData> readResult = internLoadExportMetaData(inputStream, file.getCanonicalPath());
                if (readResult.isSuccess())
                {
                    final ISingleExportMetaData metaData = readResult.getOutcome();
                    assert metaData != null : "if result is success, there must be a metaData object!";
                    final ISingleExportMetaData previouslyAdded = exportDataMap.get(metaData.getSystemInfo().getSystemId());
                    if (previouslyAdded == null)
                    {
                        LOGGER.info("Successfully loaded meta-data from '" + file.getAbsolutePath() + "'");
                        exportDataMap.put(metaData.getSystemInfo().getSystemId(), metaData);
                    }
                    else
                    {
                        if (previouslyAdded.getSystemInfo().getTimestamp() < metaData.getSystemInfo().getTimestamp())
                        {
                            LOGGER.warn("Replacing previously added info for system '" + previouslyAdded.getSystemInfo().getPath()
                                    + "' with information from '" + file.getAbsolutePath() + "'");
                            exportDataMap.put(metaData.getSystemInfo().getSystemId(), metaData);
                        }
                        else
                        {
                            LOGGER.warn("Content of export meta-data file '" + file.getAbsolutePath()
                                    + "' is older than already added info for system '" + previouslyAdded.getSystemInfo().getPath() + "'");
                        }
                    }
                }
            }
            catch (final IOException e)
            {
                result.addError(IOMessageCause.READ_ERROR, e);
                return result;
            }
        }

        final Map<String, IMergedIssueCategory> issueCategories = new LinkedHashMap<>();
        final Map<String, IMetricCategory> metricCategories = new LinkedHashMap<>();
        final Map<String, IMetricProvider> metricProviders = new LinkedHashMap<>();
        final Map<String, IMetricId> metricIds = new LinkedHashMap<>();

        for (final Map.Entry<String, ISingleExportMetaData> next : exportDataMap.entrySet())
        {
            final ISingleExportMetaData data = next.getValue();
            systemMap.put(data.getSystemInfo().getSystemId(), data.getSystemInfo());

            for (final Map.Entry<String, IIssueCategory> nextCat : data.getIssueCategories().entrySet())
            {
                if (!issueCategories.containsKey(nextCat.getKey()))
                {
                    final IIssueCategory category = nextCat.getValue();
                    issueCategories.put(nextCat.getKey(),
                            new MergedIssueCategoryImpl(category.getName(), category.getPresentationName(), data.getSystemInfo()));
                }
                else
                {
                    final IMergedIssueCategory category = issueCategories.get(nextCat.getKey());
                    category.addSystem(data.getSystemInfo());
                }
            }

            //TODO: Add further Merged* infos?
            for (final Map.Entry<String, IMetricCategory> nextCat : data.getMetricCategories().entrySet())
            {
                if (!metricCategories.containsKey(nextCat.getKey()))
                {
                    metricCategories.put(nextCat.getKey(), nextCat.getValue());
                }
            }

            for (final Map.Entry<String, IMetricProvider> nextProvider : data.getMetricProviders().entrySet())
            {
                if (!metricProviders.containsKey(nextProvider.getKey()))
                {
                    metricProviders.put(nextProvider.getKey(), nextProvider.getValue());
                }
            }

            for (final Map.Entry<String, IMetricId> nextId : data.getMetricIds().entrySet())
            {
                if (!metricIds.containsKey(nextId.getKey()))
                {
                    metricIds.put(nextId.getKey(), nextId.getValue());
                }
            }
        }

        final String identifier = exportDataMap.values().stream().map((final ISingleExportMetaData metaData) -> metaData.getResourceIdentifier())
                .reduce((path1, path2) -> path1 + ", " + path2).get();

        final MergedExportMetaDataImpl mergedExportMetaData = new MergedExportMetaDataImpl(systemMap.values(), issueCategories, metricCategories,
                metricProviders, metricIds, identifier);
        result.setOutcome(mergedExportMetaData);
        return result;
    }
}