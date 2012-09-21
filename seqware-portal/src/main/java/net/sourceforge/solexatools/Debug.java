package	net.sourceforge.solexatools;								// -*- tab-width: 4 -*-
import javax.servlet.http.HttpServletRequest;
import net.sourceforge.seqware.common.util.Log;

public class Debug {
	// Produces:	"((ImplementingClassName)InstanceClassName)"
	//       or:	"ClassName"
	// ... in the case where the implementing class and class of the instance differ
	// or are the same respectively.
	public static String describeInterfaceAndImplementation(
		Object instance, String implClassName
	) {
		String	instanceClassName			= (instance != null ? instance.getClass().getName() : "");
		boolean	implAlsoDefinesInterfaceP	= instanceClassName.equals(implClassName);
		int		afterLastDot;

		afterLastDot			= instanceClassName.lastIndexOf(".") + 1;
		if(afterLastDot < 0)	{ afterLastDot = 0; }
		instanceClassName = instanceClassName.substring(afterLastDot);

		afterLastDot			= implClassName.lastIndexOf(".") + 1;
		if(afterLastDot < 0)	{ afterLastDot = 0; }
		implClassName = implClassName.substring(afterLastDot);

		if(implAlsoDefinesInterfaceP)
			return implClassName;

		return "((" + implClassName + ")" + instanceClassName + ")";
	}

	public static void put(HttpServletRequest request) {
		putImpl(null,
				""
				+ "{ request.getPathInfo() = "		 + request.getPathInfo()
				+ ", request.getPathTranslated() = " + request.getPathTranslated()
				+ ", request.getRequestURI() = "	 + request.getRequestURI()
				+ ", request.getServletPath() = "	 + request.getServletPath()
				+ "}"
				);
	}

	public static void put(String message) {
		putImpl(null, message);
	}
	public static void put(Object instance, String message) {
		putImpl(instance, message);
	}

	// It is important that both interfaces above call this implementation
	// directly and not each other because of the levels of stack that
	// are ignored in the expression:
	//		StackTraceElement	caller	= stack[4];
	// ... below.
	private static void putImpl(Object instance, String message) {
		//return; /*  // Uncoment this line at the beginning with '//' in order to enable debug messages
		Thread				current	= Thread.currentThread();
		StackTraceElement[]	stack	= current.getStackTrace();
		StackTraceElement	caller	= stack[4];

		////////////////////////////////////////////////////////////////////////
		// build indentation to indicate call-depth
		String	indent	= "";
		for(int i = stack.length-1; i > 0; i--) {
			String implClassName	= stack[i].getClassName();
			if(!implClassName.startsWith("net.sourceforge.solexatools"))
				continue;

			// having more padding near "base" of stack is more important:
			//-for(int j = (int)(Math.log(i)/Math.log(2.0)); j > 0; j--) {
				indent += "    ";
			//-}
		}


		////////////////////////////////////////////////////////////////////////
		// build final message
		String	msg		=
			"T" + current.getId()		+ ": "
			+ caller.getFileName()		+ ":"	// maybe null
			+ caller.getLineNumber()	+ ":"	// maybe negative
		;

		while(msg.length() < 50) msg += " ";	// pad to fixed width

		msg += ""
			+ indent
			+ describeInterfaceAndImplementation(instance, caller.getClassName())
			+ "."
			+ caller.getMethodName()
			+ message;

		// show it
		Log.info(msg); //*/
	}
}

//ex:sw=4:ts=4:
