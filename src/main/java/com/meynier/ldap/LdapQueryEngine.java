package com.meynier.ldap;


import com.meynier.ldap.strategy.impl.DefaultLdapQuery;
import com.meynier.ldap.strategy.LdapQueryStrategy;
import com.meynier.ldap.strategy.impl.PaginatedLdapQuery;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.*;
import java.util.*;

public class LdapQueryEngine {

    private static final String PAGED_RESULT_CONTROL_OID = "1.2.840.113556.1.4.319";

    private final LdapContext ldapContext;
    private final LdapQueryStrategy ldapQueryStrategy;


    public LdapQueryEngine(final Properties ldapConf) throws NamingException {
        Hashtable<String, String> ldapEnv = new Hashtable<>();
        ldapEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        ldapEnv.put(Context.PROVIDER_URL, ldapConf.get("ldap.url") + ":" + ldapConf.get("ldap.port"));
        ldapEnv.put(Context.SECURITY_AUTHENTICATION, ldapConf.getProperty("ldap.security.authentification"));
        ldapEnv.put(Context.SECURITY_PRINCIPAL, ldapConf.getProperty("ldap.user"));
        ldapEnv.put(Context.SECURITY_CREDENTIALS, ldapConf.getProperty("ldap.pwd"));
        ldapContext = new InitialLdapContext(ldapEnv, null);
        this.ldapQueryStrategy = isPagedResultControlSupported() ? new PaginatedLdapQuery() : new DefaultLdapQuery();
    }

    public List<SearchResult> search(String searchBase, String filter, String[] returningAttrs) {
        return this.ldapQueryStrategy.search(ldapContext, searchBase, filter, returningAttrs);
    }

    private boolean isPagedResultControlSupported() {
        try {
            final SearchControls ctl = new SearchControls();
            ctl.setReturningAttributes(new String[]{"supportedControl"});
            ctl.setSearchScope(SearchControls.OBJECT_SCOPE);

            /* search for the rootDSE object */
            final NamingEnumeration<SearchResult> results = ldapContext.search("", "(objectClass=*)", ctl);

            while (results.hasMore()) {
                final SearchResult entry = results.next();
                final NamingEnumeration<? extends Attribute> attrs = entry.getAttributes().getAll();
                while (attrs.hasMore()) {
                    final Attribute attr = attrs.next();
                    final NamingEnumeration<?> vals = attr.getAll();
                    while (vals.hasMore()) {
                        final String value = (String) vals.next();
                        if (value.equals(PAGED_RESULT_CONTROL_OID)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        } catch (final Exception e) {
            System.out.println("Exception when trying to know if the server support paged results." + e);
            return false;
        }
    }


}
