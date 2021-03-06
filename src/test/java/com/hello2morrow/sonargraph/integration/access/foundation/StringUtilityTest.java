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
package com.hello2morrow.sonargraph.integration.access.foundation;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class StringUtilityTest
{
    @Test
    public void testConvertConstantNameToMixedCaseString()
    {
        assertEquals("NumberOfViolations", StringUtility.convertConstantNameToMixedCaseString("NUMBER_OF_VIOLATIONS", true, false));
    }

    @Test
    public void testConvertMixedCaseStringToConstantName()
    {
        assertEquals("NUMBER_OF_VIOLATIONS", StringUtility.convertMixedCaseStringToConstantName("NumberOfViolations"));
    }

    @Test
    public void testReplaceXMLWithHTMLExtension()
    {
        assertEquals("file.html", StringUtility.replaceXMLWithHTMLExtension("file.xml"));
    }

}
