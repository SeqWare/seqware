package net.sourceforge.solexatools.authentication;

import java.io.IOException;
import java.util.HashMap;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;

import com.sun.security.auth.module.Krb5LoginModule;


/**
 * <p>UNCKerberos class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class UNCKerberos extends Authentication
{
  
  /**
   * {@inheritDoc}
   *
   * Get boolean true/false based on onyen/password pair.  No exception is thrown.
   */
  public boolean loginSuccess(String uid, String password)
  {
    String user = uid;
    if (uid.contains("@")) {
      String[] t = uid.split("\\@");
      user = t[0];
    }
    try { 
      return (getSubject(user, password, null) != null);
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println(e.getMessage());
    }
    return(false);
  }

  // does the heavy-lifting for methods, above
  /**
   * <p>getSubject.</p>
   *
   * @param uid a {@link java.lang.String} object.
   * @param password a {@link java.lang.String} object.
   * @param keytabfile a {@link java.lang.String} object.
   * @return a {@link javax.security.auth.Subject} object.
   * @throws javax.security.auth.login.LoginException if any.
   */
  protected Subject getSubject(String uid, String password, String keytabfile) throws LoginException
  {
    try
    {
      Subject j_subject = null;

      if (password != null)
      {
    	  
        Krb5LoginModule lm = new com.sun.security.auth.module.Krb5LoginModule();
        
        HashMap options = new HashMap();
        j_subject = new Subject();
        options.put("doNotPrompt", "false");
        //  options.put("debug", "true");
        
        lm.initialize(j_subject, new NamePasswordCallbackHandler(uid, password), null, options);
        lm.logout();
        lm.login();
        lm.commit();
      }
      else
      {
        // use the old way for keytabs
        Krb5LoginModule lm = new com.sun.security.auth.module.Krb5LoginModule();
        HashMap options = new HashMap();
        j_subject = new Subject();
        options.put("principal", uid);
        options.put("keyTab", keytabfile);
        options.put("useTicketCache", "false");
        options.put("useKeyTab", "true");
        options.put("storeKey", "true");
        options.put("doNotPrompt", "true");
        //  options.put("debug", "true");
        lm.initialize(j_subject, null, null, options);
        lm.logout();
        lm.login();
        lm.commit();
      }

      return j_subject;
    }
    catch (LoginException e)
    {
      throw e;
    }
  }

  /**
   * <p>isConfigError.</p>
   *
   * @param e a {@link java.lang.Throwable} object.
   * @return a boolean.
   */
  protected boolean isConfigError(Throwable e)
  {
    boolean rval = false;
    Throwable t = e.getCause();
    if (t != null)
    {
      rval = isConfigError(t);
    }

    if (!rval)
    {
      StackTraceElement[] E = e.getStackTrace();
      for (int i = 0; i < E.length; i++)
      {
        String s = E[i].getClassName();
        if ("sun.security.krb5.Config".equals(s))
        {
          rval = true;
          break;
        }
      }
    }
    return rval;
  }

  protected class NamePasswordCallbackHandler implements CallbackHandler
  {

    String username;
    String password;

    public NamePasswordCallbackHandler(String onyen, String pwd)
    {
      super();
      username = onyen;
      password = pwd;
    }

    public void handle(Callback[] callbacks) throws IOException,
    UnsupportedCallbackException
    {
      boolean n = false;
      boolean p = false;
      for (int i = 0; i < callbacks.length; i++)
      {
        Callback C = callbacks[i];
        if (C instanceof NameCallback)
        {
          NameCallback nc = (NameCallback) C;
          nc.setName(username);
          n = true;
        }
        else if (C instanceof PasswordCallback)
        {
          PasswordCallback pc = (PasswordCallback) C;

          // pc.setPassword( password.toCharArray() );
          byte[] bytes = password.getBytes();

          int length = password.length();
          char[] password_chars = new char[length];

          for (int j = 0; j < length; j++)
          {
            password_chars[j] = (char) bytes[j];//password.charAt(j);
          }

          pc.setPassword(password_chars);

          p = true;
        }
        if (n && p)
        {
          break;
        }
      }
    }
  }
}
