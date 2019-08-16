package com.meynier.ldap.strategy.impl;

import com.meynier.ldap.strategy.LdapQueryStrategy;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.SizeLimitExceededException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.PagedResultsControl;
import javax.naming.ldap.PagedResultsResponseControl;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PaginatedLdapQuery implements LdapQueryStrategy {

    @Override
    public List<SearchResult> search(final LdapContext ldapContext, String searchBase, String filter, String[] returningAttrs) {
        ArrayList<SearchResult> result = new ArrayList<>();
        final SearchControls ctls = new SearchControls();
        ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        ctls.setReturningAttributes(returningAttrs);
        ctls.setCountLimit(0); // set no limits

        try {
            byte[] cookie;
            ldapContext.setRequestControls(new Control[]{new PagedResultsControl(PAGE_SIZE, Control.NONCRITICAL)});
            do {
                final NamingEnumeration<SearchResult> answer = ldapContext.search(searchBase, filter, ctls);
                while (answer.hasMore()) {
                    result.add(answer.nextElement());
                }
                cookie = getCookie(ldapContext);
            } while (cookie != null);
        } catch (final SizeLimitExceededException e) {
            System.out.println(e);
        } catch (final Exception e) {
            System.out.println(e);
        }
        return result;
    }

    private static byte[] getCookie(final LdapContext ldapContext) throws NamingException {
        byte[] cookie = null;
        // Examine the paged results control response
        final Control[] controls = ldapContext.getResponseControls();
        if (Objects.nonNull(controls)) {
            for (Control control : controls) {
                if (control instanceof PagedResultsResponseControl) {
                    final PagedResultsResponseControl prrc = (PagedResultsResponseControl) control;
                    cookie = prrc.getCookie();
                }
            }
        }
        return cookie;
    }
}
