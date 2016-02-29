package com.hello2morrow.sonargraph.integration.access.apitest;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.hello2morrow.sonargraph.integration.access.controller.ControllerFactory;
import com.hello2morrow.sonargraph.integration.access.controller.IMetaDataController;
import com.hello2morrow.sonargraph.integration.access.foundation.OperationResultWithOutcome;
import com.hello2morrow.sonargraph.integration.access.foundation.TestFixture;
import com.hello2morrow.sonargraph.integration.access.model.IExportMetaData;
import com.hello2morrow.sonargraph.integration.access.model.IMergedExportMetaData;
import com.hello2morrow.sonargraph.integration.access.model.IMetricId;
import com.hello2morrow.sonargraph.integration.access.model.IMetricLevel;

public class ExportMetaDataControllerTest
{
    private static final String META_DATA_PATH = TestFixture.META_DATA_PATH;
    private static final String META_DATA_PATH1 = TestFixture.META_DATA_PATH1;
    private static final String META_DATA_PATH2 = TestFixture.META_DATA_PATH2;
    private static final String META_DATA_PATH_OUTDATED = TestFixture.META_DATA_PATH1_OLD;

    private IMetaDataController m_controller;

    @Before
    public void before()
    {
        m_controller = new ControllerFactory().createMetaDataController();
    }

    @Test
    public void testReadExportMetaData() throws IOException
    {
        final File exportMetaDataFile = new File(META_DATA_PATH);
        final OperationResultWithOutcome<IExportMetaData> result = m_controller.loadExportMetaData(exportMetaDataFile);
        assertTrue("Failed to load metric meta-data file: " + result.toString(), result.isSuccess());

        final IExportMetaData metaData = result.getOutcome();

        assertEquals("Identifier for metadata does not match", new File(META_DATA_PATH).getCanonicalPath(), metaData.getResourceIdentifier());
        assertEquals("Wrong issue categories size", 13, metaData.getIssueCategories().size());
        final String issueCategory = "ArchitectureViolation";
        assertNotNull("Issue Category '" + issueCategory + "' not found", metaData.getIssueCategories().get(issueCategory));

        assertEquals("Wrong metric categories size", 10, metaData.getMetricCategories().size());
        final String architectureCategory = "Architecture";
        assertNotNull("Metric category '" + architectureCategory + "' not found", metaData.getMetricCategories().get(architectureCategory));

        final String johnLakosCategoryName = "JohnLakos";
        assertEquals("Metric category '" + johnLakosCategoryName + "' has wrong presentation name", "John Lakos",
                metaData.getMetricCategories().get(johnLakosCategoryName).getPresentationName());

        final Map<String, IMetricLevel> metricLevelMap = metaData.getMetricLevels();
        assertEquals("Wrong number of levels", 8, metricLevelMap.size());

        final List<IMetricId> systemLevelMetrics = metaData.getMetricIdsForLevel(metricLevelMap.get("System"));
        assertEquals("Wrong number of metrics for System level", 45, systemLevelMetrics.size());

        final IMetricId coreLinesOfCode = metaData.getMetricIds().get("CoreLinesOfCode");
        assertThat("Wrong number of levels for metric", coreLinesOfCode.getLevels(),
                hasItems(metricLevelMap.get("System"), metricLevelMap.get("Module"), metricLevelMap.get("SourceFile")));

        assertEquals("Wrong number of metrics", 74, metaData.getMetricIds().size());
        final String scriptMetricId = "Unused Types";
        assertEquals("Metric id '" + scriptMetricId + "' has wrong presentation name", "Number of Unused Types",
                metaData.getMetricIds().get(scriptMetricId).getPresentationName());
    }

    @Test
    public void testMergeExportMetaDataFiles() throws IOException
    {
        final List<File> files = new ArrayList<>();
        files.add(new File(META_DATA_PATH1));
        files.add(new File(META_DATA_PATH2));
        files.add(new File(META_DATA_PATH_OUTDATED));
        final OperationResultWithOutcome<IMergedExportMetaData> result = m_controller.mergeExportMetaDataFiles(files);

        assertTrue("Success expected", result.isSuccess());
        final IMergedExportMetaData metaData = result.getOutcome();
        assertEquals("Wrong identifier", files.get(0).getCanonicalPath() + ", " + files.get(1).getCanonicalPath(), metaData.getResourceIdentifier());
        assertNotNull("Common metric not found", metaData.getMetricIds().get("CoreLinesOfCode"));
        assertNotNull("Individual metric of first file not found", metaData.getMetricIds().get("Unused Types"));
        assertNotNull("Individual metric of second file not found", metaData.getMetricIds().get("Unused Types2"));
        assertNull("Individual metric of outdated file must be omitted", metaData.getMetricIds().get("Unused Types3"));
    }
}