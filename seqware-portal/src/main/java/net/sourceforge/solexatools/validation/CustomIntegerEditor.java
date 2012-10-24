package net.sourceforge.solexatools.validation;

import java.beans.PropertyEditorSupport;

/**
 * <p>CustomIntegerEditor class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class CustomIntegerEditor extends PropertyEditorSupport {
/**
 * <p>Constructor for CustomIntegerEditor.</p>
 */
public CustomIntegerEditor() {
}

/**
 * <p>getAsText.</p>
 *
 * @return a {@link java.lang.String} object.
 */
public String getAsText() {
Integer i = (Integer) getValue();
return i.toString();
}

/** {@inheritDoc} */
public void setAsText(String str) {
if( str == "" || str == null )
setValue(0);
else
setValue(Integer.parseInt(str));
}
} 
