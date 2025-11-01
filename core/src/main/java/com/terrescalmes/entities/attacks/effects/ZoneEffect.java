package com.terrescalmes.entities.attacks.effects;

import java.util.List;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.terrescalmes.entities.Entity;
import com.terrescalmes.entities.EntityManager;
import com.terrescalmes.items.StatModifier;
import com.terrescalmes.util.Vector2I;

public class ZoneEffect implements IAttackEffect {

    private float radius;
    private int damage;

    public ZoneEffect(float radius, int damage) {
        this.radius = radius;
        this.damage = damage;
    }

    @Override
    public void trigger(Entity source, Vector2I position, List<StatModifier> statModifiers) {
        Circle explosionCircle = new Circle(position.x, position.y, radius);
        List<Entity> entities = EntityManager.getInstance().getEntitiesInCircle(explosionCircle);

        int modifiedDamages = applyStatModifiers(statModifiers);

        for (Entity entity : entities) {
            if (!entity.equals(source) && !entity.getFaction().equals(source.getFaction())) {
                entity.takeDamage(modifiedDamages);
                if (entity.isDead()) {
                    source.onKill(entity);
                }
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
