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

public class DefaultLdapQuery implements LdapQueryStrategy {
    @Override
    public List<SearchResult> search(LdapContext ldapContext, String searchBase, String filter, String[] returningAttrs) {
        ArrayList<SearchResult> result = new ArrayList<>();
        final SearchControls ctls = new SearchControls();
        ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        ctls.setReturningAttributes(returningAttrs);
        ctls.setCountLimit(countLimit); // set no limits

        int counter = 0;
        try {
            final NamingEnumeration<SearchResult> answer = ldapContext.search(searchBase, filter, ctls);
            while (answer.hasMore()) {
                result.add(answer.nextElement());
            }
            counter++;
        } catch (final SizeLimitExceededException e) {
            System.out.println("SizeLimitExceededException after " + counter + " records when getting all users from LDAP");
        } catch ( final NamingException e) {
            System.out.println("NamingException when trying to fetch deleted users from LDAP using ldapBase::" + searchBase + " on row::" + counter + e);
        } catch (final Exception e) {
            System.out.println("Exception when trying to fetch deleted users from LDAP using ldapBase::" + searchBase + " on row::" + counter + e);
        }
        return result;
    }
}
