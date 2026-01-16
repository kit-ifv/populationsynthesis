package examples.layerscenario

import edu.kit.ifv.populationsynthesis.rules.Rule
import edu.kit.ifv.populationsynthesis.rules.composer.HierarchyComposer
import edu.kit.ifv.populationsynthesis.rules.composer.HierarchyRuleComposer
import edu.kit.ifv.populationsynthesis.rules.provider.HierarchicRuleProvider
import edu.kit.ifv.populationsynthesis.rules.provider.MapRuleProvider
import edu.kit.ifv.populationsynthesis.rules.provider.RuleProvider

class KonduriRuleProvider(val ruleProvider: RuleProvider<KonduriArea, KonduriHousehold> = KonduriRules): HierarchicRuleProvider<KonduriArea, KonduriHousehold> {
    override val composer: HierarchyRuleComposer<KonduriArea, KonduriHousehold> = HierarchyComposer(KonduriGraph)

    override fun getRules(target: KonduriArea): Collection<Rule<KonduriHousehold>> {
        return ruleProvider.getRules(target)
    }

    override fun getAllRules(): Map<KonduriArea, Collection<Rule<KonduriHousehold>>> {
        return ruleProvider.getAllRules()
    }
}

val KonduriRules: RuleProvider<KonduriArea, KonduriHousehold> = MapRuleProvider<KonduriArea, KonduriHousehold>().apply {
    addRules(KonduriRegion, RTypeGenerator(86, 61, 82).generateRules())
    addRules(KonduriGeographicUnit.geo1,
        HTypeGenerator(46, 51).generateRules() + PTypeGenerator(92, 88, 84).generateRules())

    addRules(KonduriGeographicUnit.geo2,
        HTypeGenerator(33, 99).generateRules() + PTypeGenerator(138, 122, 104).generateRules())
}