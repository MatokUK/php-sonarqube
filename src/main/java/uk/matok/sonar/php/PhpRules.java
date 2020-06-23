/*
 * SonarQube PHP Custom Rules Example
 * Copyright (C) 2016-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package uk.matok.sonar.php;


import com.google.common.collect.ImmutableList;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinitionAnnotationLoader;
import org.sonar.plugins.php.api.visitors.PHPCustomRuleRepository;
import uk.matok.sonar.php.checks.ExpressionAlwaysTrueOrFalseCheck;
import uk.matok.sonar.php.checks.ReturnInConstructorCheck;
import uk.matok.sonar.php.checks.SleepFunctionFamilyUseCheck;

/**
 * Extension point to define a PHP rule repository.
 */
public class PhpRules implements RulesDefinition, PHPCustomRuleRepository {
    /**
     * Provide the repository key
     */
    @Override
    public String repositoryKey() {
        return "custom";
    }

    /**
     * Provide the list of checks class that implements rules
     * to be part of the rule repository
     */
    @Override
    public ImmutableList<Class> checkClasses() {
        return ImmutableList.of(
            SleepFunctionFamilyUseCheck.class,
            ExpressionAlwaysTrueOrFalseCheck.class,
            ReturnInConstructorCheck.class
        );
    }

    @Override
    public void define(Context context) {
        NewRepository repository = context.createRepository(repositoryKey(), "php").setName("Matok Rulez");

        // Load rule meta data from annotations
        RulesDefinitionAnnotationLoader annotationLoader = new RulesDefinitionAnnotationLoader();
        checkClasses().forEach(ruleClass -> annotationLoader.load(repository, ruleClass));

        // Optionally override html description from annotation with content from html files
        repository.rules().forEach(rule -> rule.setHtmlDescription(loadResource("/uk/matok/sonar/l10n/php/rules/" + rule.key() + ".html")));

        // Optionally define remediation costs
        Map<String, String> remediationCosts = new HashMap<>();
        remediationCosts.put(SleepFunctionFamilyUseCheck.KEY, "5min");
        remediationCosts.put(ExpressionAlwaysTrueOrFalseCheck.KEY, "5min");
        remediationCosts.put(ReturnInConstructorCheck.KEY, "5min");
        repository.rules().forEach(rule -> rule.setDebtRemediationFunction(
            rule.debtRemediationFunctions().constantPerIssue(remediationCosts.get(rule.key())))
        );

        repository.done();
    }

    private String loadResource(String path) {
        URL resource = getClass().getResource(path);
        if (resource == null) {
            throw new IllegalStateException("Resource not found: " + path);
        }
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        try (InputStream in = resource.openStream()) {
            byte[] buffer = new byte[1024];
            for (int len = in.read(buffer); len != -1; len = in.read(buffer)) {
                result.write(buffer, 0, len);
            }
            return new String(result.toByteArray(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read resource: " + path, e);
        }
    }
}
