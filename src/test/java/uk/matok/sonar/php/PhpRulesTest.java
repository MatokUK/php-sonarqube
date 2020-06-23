package uk.matok.sonar.php;

import org.junit.Test;
import org.sonar.api.server.rule.RulesDefinition;

import static org.junit.Assert.assertEquals;

public class PhpRulesTest {
    @Test
    public void rules() {
        PhpRules rulesDefinition = new PhpRules();
        RulesDefinition.Context context = new RulesDefinition.Context();
        rulesDefinition.define(context);
        RulesDefinition.Repository repository = context.repository("custom");
        assertEquals(3, repository.rules().size());
    }
}
