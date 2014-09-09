package org.glotaran.core.ui.visualmodelling.common;

import java.beans.PropertyEditorSupport;

public class EnumPropertyEditor extends PropertyEditorSupport {

    public EnumPropertyEditor() {
        super();
    }

    @Override
    public String[] getTags() {
        return EnumTypes.getStrNames(getValue());
    }

    @Override
    public String getAsText() {
        Object obj = getValue();
        Object[] mValues = EnumTypes.getTagsNames(obj);
        for (int i = 0; i < mValues.length; i++) {
            if (obj.equals(mValues[i])) {
                return EnumTypes.getStrNames(obj)[i];
            }
        }
        return null;
    }

    @Override
    public void setAsText(String str) {
        String[] mTags = EnumTypes.getStrNames(getValue());
        for (int i = 0; i < mTags.length; i++) {
            if (str.equals(mTags[i])) {
                setValue(EnumTypes.getTagsNames(getValue())[i]);
                return;
            }
        }
    }
}
