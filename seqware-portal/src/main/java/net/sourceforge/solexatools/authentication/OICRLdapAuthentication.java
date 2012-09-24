package net.sourceforge.solexatools.authentication;

import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchResult;

import org.apache.log4j.Logger;

import com.google.common.collect.Lists;

/**
 * This authentication plugin allows the SeqWare Portal to authenticate against
 * the Ontario Institute for Cancer Research (OICR) LDAP server.
 * 
 * Users will pass in an email address and password. An associated list of uids
 * that match the email address will be retrieved via an LDAP directory search.
 * More than one uid will be returned for users who have multiple credentials.
 * This plugin will loop through the uids and attempt to authenticate with the
 * provided password.
 */
public class OICRLdapAuthentication extends Authentication {

  static Logger log = Logger.getLogger(OICRLdapAuthentication.class);

  @Override
  public boolean loginSuccess(String uid, String password) {
    // uid value is an email address.
    return oicrLdapLogin(uid, password);
  }

  private boolean oicrLdapLogin(String email, String password) {
    List<String> uids = Lists.newArrayList();
    try {
      uids = getUids(findUidsByEmail(email));
    } catch (NamingException e) {
      log.debug("Failed to locate uids for user [" + email + "] due to ldap exception. ", e);
    }
    for (String uid : uids) {
      if (oicrLdapAuthenticate(uid, password)) {
        return true;
      }
    }

    return false;
  }

  private boolean oicrLdapAuthenticate(String uid, String password) {
    boolean result = false;

    Hashtable<String, String> env = new Hashtable<String, String>(11);
    env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
    env.put(Context.PROVIDER_URL, "ldap://10.0.0.100/");

    env.put(Context.SECURITY_AUTHENTICATION, "simple");
    env.put(Context.SECURITY_PRINCIPAL, "uid=" + uid + ", ou=People, dc=oicr, dc=on, dc=ca");
    env.put(Context.SECURITY_CREDENTIALS, password);

    try {
      DirContext context = new InitialDirContext(env);
      context.close();
      result = true;
    } catch (NamingException e) {
      log.debug("User [" + uid + "] failed to authenticate via ldap. ", e);
    }
    return result;
  }

  private NamingEnumeration<SearchResult> findUidsByEmail(String email) throws NamingException {
    NamingEnumeration<SearchResult> result;
    DirContext context = getDirContext();
    try {
      // Ignore case when matching.
      Attributes matchingAttributes = new BasicAttributes(true);
      // Search for object with 'mail' attribute that matches the provided
      // 'email' address.
      matchingAttributes.put(new BasicAttribute("mail", email));
      result = context.search("ou=People, dc=oicr, dc=on, dc=ca", matchingAttributes);
    } finally {
      context.close();
    }
    return result;
  }

  public DirContext getDirContext() throws NamingException {
    Hashtable<String, String> env = new Hashtable<String, String>(11);
    env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
    env.put(Context.PROVIDER_URL, "ldap://10.0.0.100/");
    DirContext ctx = new InitialDirContext(env);
    return ctx;
  }

  public List<String> getUids(NamingEnumeration<SearchResult> searchResultEnum) throws NamingException {
    List<String> uids = Lists.newArrayList();
    while (searchResultEnum.hasMore()) {
      SearchResult sr = (SearchResult) searchResultEnum.next();
      uids.add((String) sr.getAttributes().get("uid").get());
    }
    return uids;
  }
}
