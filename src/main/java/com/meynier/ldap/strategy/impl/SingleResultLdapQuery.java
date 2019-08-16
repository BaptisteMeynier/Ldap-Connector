package com.meynier.ldap.strategy.impl;

import com.meynier.ldap.strategy.LdapQueryStrategy;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.SizeLimitExceededException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;
import java.util.ArrayList;
import java.util.List;

public class SingleResultLdapQuery implements LdapQueryStrategy {
    @Override
    public List<SearchResult> search(LdapContext ldapContext, String searchBase, String filter, String[] returningAttrs) {
        ArrayList<SearchResult> result = new ArrayList<>();
        final SearchControls ctls = new SearchControls();
        ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        ctls.setReturningAttributes(returningAttrs);
        ctls.setCountLimit(1); // set no limits

        try {
            final NamingEnumeration<SearchResult> answer = ldapContext.search(searchBase, filter, ctls);
            if (answer.hasMore()) {
                result.add(answer.nextElement());
            }
        } catch (NamingException e) {
            e.printStackTrace();
        }
        return result;
    }
}
