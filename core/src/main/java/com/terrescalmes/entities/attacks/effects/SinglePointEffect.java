package com.terrescalmes.entities.attacks.effects;

import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.terrescalmes.entities.Entity;
import com.terrescalmes.entities.EntityManager;
import com.terrescalmes.items.StatModifier;

public class SinglePointEffect implements IAttackEffect {

    private int damage;

    public SinglePointEffect(int damage) {
        this.damage = damage;
    }

    @Override
    public void trigger(Entity source, Vector2 position, List<StatModifier> statModifiers) {
        Entity target = EntityManager.getInstance().getEntityAt(position.x, position.y);

        int modifiedDamages = applyStatModifiers(statModifiers);
        System.out.println(damage + " " + modifiedDamages);

        if (target != null && !target.equals(source) && !target.getFaction().equals(source.getFaction())) {
            target.takeDamage(modifiedDamages);
            if (target.isDead()) {
                source.onKill(target);
            }
        }
    }

    @Override
    public int applyStatModifiers(List<StatModifier> statModifiers) {
        if (statModifiers == null) {
            return damage;
        }

        int modifiedDamages = damage;
        for (StatModifier statModifier : statModifiers) {
            if (statModifier.getType().equals("damage")) {
                float value = statModifier.getValue();
                if (statModifier.isMultiplier()) {
                    modifiedDamages *= value;
                } else {
                    modifiedDamages += value;
                }

            }
        }

        return modifiedDamages;
    }
}
