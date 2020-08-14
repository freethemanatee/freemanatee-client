package me.zopac.freemanatee.setting.builder.primitive;

import me.zopac.freemanatee.setting.impl.BooleanSetting;
import me.zopac.freemanatee.setting.builder.SettingBuilder;

/**
 * Created by 086 on 13/10/2018.
 */
public class BooleanSettingBuilder extends SettingBuilder<Boolean> {
    @Override
    public BooleanSetting build() {
        return new BooleanSetting(initialValue, predicate(), consumer(), name, visibilityPredicate());
    }

    @Override
    public BooleanSettingBuilder withName(String name) {
        return (BooleanSettingBuilder) super.withName(name);
    }
}
