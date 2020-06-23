package uk.matok.sonar.php.checks;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.php.tree.impl.expression.PrefixExpressionTreeImpl;
import org.sonar.plugins.php.api.tree.Tree.Kind;
import org.sonar.plugins.php.api.tree.expression.BinaryExpressionTree;
import org.sonar.plugins.php.api.tree.expression.ExpressionTree;
import org.sonar.plugins.php.api.tree.expression.LiteralTree;
import org.sonar.plugins.php.api.visitors.PHPVisitorCheck;

import java.util.Arrays;
import java.util.HashSet;

@Rule(
    key = ExpressionAlwaysTrueOrFalseCheck.KEY,
    priority = Priority.MAJOR,
    name = "Logical expression is always true / false",
    tags = {"confusing", "pitfall"}
)
public class ExpressionAlwaysTrueOrFalseCheck extends PHPVisitorCheck {

    public static final String KEY = "E1";
    private static final String MESSAGE = "Expression \"%s\" is always %s because usage of literal \"%s\".";

    @Override
    public void visitBinaryExpression(BinaryExpressionTree tree) {
        if (tree.is(Kind.CONDITIONAL_OR)) {
            if (checkForTrue(tree.leftOperand())) {
                raiseIssue(tree, tree.leftOperand(), "true");
            } else if (checkForTrue(tree.rightOperand())) {
                raiseIssue(tree, tree.rightOperand(), "true");
            }
        }

        if (tree.is(Kind.CONDITIONAL_AND)) {
            if (checkForFalse(tree.leftOperand())) {
                raiseIssue(tree, tree.leftOperand(), "false");
            } else if (checkForFalse(tree.rightOperand())) {
                raiseIssue(tree, tree.rightOperand(), "false");
            }
        }

        super.visitBinaryExpression(tree);
    }

    private boolean checkForTrue(ExpressionTree expressionTree) {
        switch (expressionTree.getKind()) {
            case BOOLEAN_LITERAL:
                return ((LiteralTree) expressionTree).token().text().equalsIgnoreCase("true");
            case NUMERIC_LITERAL:
                return !((LiteralTree) expressionTree).token().text().equals("0");
            case REGULAR_STRING_LITERAL:
                return isPHPStringToBoolenConversionTrue(expressionTree);
            case LOGICAL_COMPLEMENT:
                return isFalsePHPLiteral((PrefixExpressionTreeImpl) expressionTree);
        }

        return false;
    }

    private boolean checkForFalse(ExpressionTree expressionTree) {
        switch (expressionTree.getKind()) {
            case BOOLEAN_LITERAL:
                return ((LiteralTree) expressionTree).token().text().equalsIgnoreCase("false");
            case NUMERIC_LITERAL:
                return ((LiteralTree) expressionTree).token().text().equals("0");
            case REGULAR_STRING_LITERAL:
                return !isPHPStringToBoolenConversionTrue(expressionTree);
            case LOGICAL_COMPLEMENT:
                return !isFalsePHPLiteral((PrefixExpressionTreeImpl) expressionTree);
        }

        return false;
    }

    private boolean isPHPStringToBoolenConversionTrue(ExpressionTree expressionTree) {
        return expressionTree.toString().length() > 3 || (3 == expressionTree.toString().length() && '0' != expressionTree.toString().charAt(1));
    }

    private boolean isFalsePHPLiteral(PrefixExpressionTreeImpl expression) {
        HashSet<String> phpFalse = new HashSet<>(Arrays.asList("0", "\"\"", "''", "\"0\"", "'0'"));
        String expressionLiteral = expression.expression().toString();
        expressionLiteral = removeLogicalComplementPairs(expressionLiteral);

        return phpFalse.contains(expressionLiteral);
    }

    private String removeLogicalComplementPairs(String expression) {
        return expression.replace("!!", "");
    }

    private void raiseIssue(BinaryExpressionTree tree, ExpressionTree operand, String booleanLiteral) {
        String expression = getWholeExpression(tree);
        context().newIssue(this, tree, String.format(MESSAGE, expression, booleanLiteral, operand.toString().replace("\"", "")));
    }

    private String getWholeExpression(ExpressionTree tree) {
        while (tree.getParent().is(Kind.CONDITIONAL_OR, Kind.CONDITIONAL_AND)) {
            tree = (ExpressionTree) tree.getParent();
        }

        return tree.toString();
    }
}
