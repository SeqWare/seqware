package net.sourceforge.solexatools.validation;

import java.beans.PropertyEditorSupport;

public class CustomIntegerEditor extends PropertyEditorSupport {
public CustomIntegerEditor() {
}

public String getAsText() {
Integer i = (Integer) getValue();
return i.toString();
}

public void setAsText(String str) {
if( str == "" || str == null )
setValue(0);
else
setValue(Integer.parseInt(str));
}
} 