package uk.matok.sonar.php.checks;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.php.api.tree.Tree.Kind;
import org.sonar.plugins.php.api.tree.declaration.MethodDeclarationTree;
import org.sonar.plugins.php.api.tree.expression.NameIdentifierTree;
import org.sonar.plugins.php.api.tree.statement.ReturnStatementTree;
import org.sonar.plugins.php.api.visitors.PHPVisitorCheck;

@Rule(
    key = ReturnInConstructorCheck.KEY,
    priority = Priority.MAJOR,
    name = "__construct contains return command",
    tags = {"confusing"}
)
public class ReturnInConstructorCheck extends PHPVisitorCheck {

    public static final String KEY = "R1";
    private static final String MESSAGE = "Remove return in constructor.";

    @Override
    public void visitReturnStatement(ReturnStatementTree tree) {

        if (tree.getParent().getParent().is(Kind.METHOD_DECLARATION)) {
            NameIdentifierTree nameIdentifier = ((MethodDeclarationTree) tree.getParent().getParent()).name();
            if (nameIdentifier.token().text().equals("__construct")) {
                context().newIssue(this, tree, MESSAGE);
            }
        }

        super.visitReturnStatement(tree);
    }
}
