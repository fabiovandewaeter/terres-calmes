package com.terrescalmes.items;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Item {

    protected ItemId itemId;
    protected TextureRegion icon;

    public Item(ItemId itemId, TextureRegion icon) {
        this.itemId = itemId;
        this.icon = icon;
    }
}
