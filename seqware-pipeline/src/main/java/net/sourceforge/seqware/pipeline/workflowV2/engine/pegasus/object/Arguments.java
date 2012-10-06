package net.sourceforge.seqware.pipeline.workflowV2.engine.pegasus.object;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.seqware.pipeline.workflowV2.model.Argument;

import org.jdom.Element;

public class Arguments extends PegasusAbstract {

    private List<Argument> sysOptions;
    private List<Argument> moduleOptions;
    private Argument module;

    public Arguments() {
	this.sysOptions = new ArrayList<Argument>();
	this.moduleOptions = new ArrayList<Argument>();
    }

    public void addModuleOption(Argument arg) {
	this.moduleOptions.add(arg);
    }

    public void addModuleOption(String key, String value) {
	Argument arg = new Argument(key, value);
	this.addModuleOption(arg);
    }

    public void addSysOption(Argument arg) {
	if (arg.getKey().equals("--module")) {
	    this.module = arg;
	} else {
	    this.sysOptions.add(arg);
	}
    }

    public void addSysOption(String key, String value) {
	Argument arg = new Argument(key, value);
	this.addSysOption(arg);
    }

    @Override
    public Element serializeXML() {
	Element element = new Element("argument", NAMESPACE);
	element.setText(this.options2String(sysOptions, moduleOptions));
	return element;
    }

    private String options2String(List<Argument> options1,
	    List<Argument> options2) {
	StringBuilder sb = new StringBuilder();
	for (Argument arg : options1) {
	    sb.append(arg.getKey());
	    if (null != arg.getValue())
		sb.append(" " + arg.getValue() + "\n");
	    else
		sb.append("\n");
	}
	if (this.module != null) {
	    sb.append(this.module.getKey() + " " + this.module.getValue()
		    + "\n");
	}
	if (!options2.isEmpty()) {
	    sb.append("-- \n");
	    for (Argument arg : options2) {
		sb.append(arg.getKey());
		if (null != arg.getValue())
		    sb.append(" " + arg.getValue() + "\n");
		else
		    sb.append("\n");

	    }
	}
	return sb.toString();
    }

    public boolean hasOption(String key) {
	for (Argument arg : this.sysOptions) {
	    if (arg.getKey().equals(key))
		return true;
	}
	for (Argument arg : this.moduleOptions) {
	    if (arg.getKey().equals(key))
		return true;
	}
	return false;
    }

    public void removeModuleOption(String key) {
	Argument rem = null;
	for (Argument arg : this.moduleOptions) {
	    if (arg.getKey().equals(key)) {
		rem = arg;
		break;
	    }
	}
	if (rem != null) {
	    this.moduleOptions.remove(rem);
	}
    }

    public void updateOption(String key, String value) {
	// check module option first
	for (Argument arg : this.moduleOptions) {
	    if (arg.getKey().equals(key)) {
		arg.setValue(arg.getValue() + ":" + value);
		return;
	    }
	}
	// check sys option
	for (Argument arg : this.sysOptions) {
	    if (arg.getKey().equals(key)) {
		arg.setValue(arg.getValue() + ":" + value);
		return;
	    }
	}
    }

    public boolean hasModuleOption() {
	return !this.moduleOptions.isEmpty();
    }
}