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
package uk.matok.sonar.php.checks;

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.php.api.tree.Tree;
import org.sonar.plugins.php.api.tree.Tree.Kind;
import org.sonar.plugins.php.api.tree.declaration.NamespaceNameTree;
import org.sonar.plugins.php.api.tree.expression.ExpressionTree;
import org.sonar.plugins.php.api.tree.expression.FunctionCallTree;
import org.sonar.plugins.php.api.tree.expression.LiteralTree;
import org.sonar.plugins.php.api.visitors.PHPSubscriptionCheck;
import org.sonar.plugins.php.api.visitors.PHPVisitorCheck;

/**
 * Example of implementation of a check by extending {@link PHPSubscriptionCheck}.
 * PHPSubscriptionCheck provides method {@link PHPSubscriptionCheck#visitNode} to visit nodes of the Abstract Syntax Tree
 * that represents the source code. Provide the list of nodes you want to visit through {@link PHPSubscriptionCheck#nodesToVisit}.
 * <p>
 * Those methods should be overridden to process information
 * related to node and issue can be created via the context that can be
 * accessed through {@link PHPVisitorCheck#context()}.
 */
@Rule(
    key = SleepFunctionFamilyUseCheck.KEY,
    priority = Priority.MINOR,
    name = "Sleeping function should not be used outside command.",
    tags = {"convention"}
  )
public class SleepFunctionFamilyUseCheck extends PHPSubscriptionCheck {
    public static final String KEY = "sleep";

    @Override
    public List<Kind> nodesToVisit() {
        return ImmutableList.of(Kind.FUNCTION_CALL);
    }

    /**
    * Overriding method visiting the call "checks.expression" - wtf to create an issue
    * each time a call to "foo()" or "bar()" is done.
    */
    @Override
    public void visitNode(Tree tree) {
        ExpressionTree callee = ((FunctionCallTree) tree).callee();

        if (!isCommand()) {
            checkSleep(callee);
            checkTimeSleepUntil(callee);
            checkTimeNanoSleep(callee);
            checkUsleep(callee);
        }
    }

    private boolean isCommand() {
        String filename = context().getPhpFile().relativePath().getFileName().toString();

        return filename.endsWith("Command.php");
    }

    private void checkSleep(ExpressionTree callee) {
        if (isFunctionWithName(callee, "sleep")) {
            context().newIssue(this, callee, "Remove the usage of sleep().");
        }
    }

    private void checkTimeSleepUntil(ExpressionTree callee) {
        if (isFunctionWithName(callee, "time_sleep_until")) {
            context().newIssue(this, callee, "Remove the usage of time_sleep_until().");
        }
    }

    private void checkTimeNanoSleep(ExpressionTree callee) {
      if (isFunctionWithName(callee,"time_nanosleep") && hasAtLeastArguments(callee, 1)) {
          FunctionCallTree function = (FunctionCallTree) callee.getParent();
          ExpressionTree argument1 = function.arguments().get(0);

          if (argument1.is(Kind.NUMERIC_LITERAL) && Integer.valueOf(((LiteralTree) argument1).value()) > 0) {
              context().newIssue(this, callee, "Remove the usage of time_nanosleep() or set first argument to 0 seconds.");
          }
      }
    }

    private void checkUsleep(ExpressionTree callee) {
        if (isFunctionWithName(callee,"usleep") && hasAtLeastArguments(callee, 1)) {
            FunctionCallTree function = (FunctionCallTree) callee.getParent();
            ExpressionTree argument = function.arguments().get(0);

            if (argument.is(Kind.NUMERIC_LITERAL) && Integer.valueOf(((LiteralTree) argument).value()) >= 1000000) {
                context().newIssue(this, callee, "Remove the usage of usleep() or set microseconds below 1000000 (1s).");
            }
        }
    }

    private boolean isFunctionWithName(ExpressionTree callee, String functionName) {
        return callee.is(Kind.NAMESPACE_NAME) && ((NamespaceNameTree) callee).qualifiedName().equals(functionName);
    }

    private boolean hasAtLeastArguments(ExpressionTree callee, int count) {
        return ((FunctionCallTree) callee.getParent()).arguments().size() >= count;
    }
}
