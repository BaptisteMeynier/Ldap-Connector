package com.meynier.ldap.strategy;

import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;
import java.util.List;

public interface LdapQueryStrategy {
    void setLdapContext(LdapContext ldapContext);
    List<SearchResult> search(LdapContext ldapContext, String searchBase, String filter, String[] fieldsReturn);
}
