package com.meynier.ldap;

import com.meynier.ldap.enums.LdapOperator;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;


public class LdapFilterBuilderTest {


    @Test
    public void should_return_empty_string_with_null_entry(){
        String filter = LdapFilter.builder()
                .addFilter(null,null)
                .build();
        Assert.assertEquals("",filter);
    }

    @Test
    public void should_format_simple_filter(){
        String filter = LdapFilter.builder()
                .addFilter("cn", "Baptiste")
                .build();
        Assert.assertEquals("(cn=Baptiste)",filter);
    }

    @Test
    public void should_format_double_filter(){
        String filter =LdapFilter.builder()
                .addBinaryOperator(LdapOperator.AND)
                .addFirstFilter("cn", "Baptiste")
                .addSecondFilter("sn", "Meynier")
                .build();
        Assert.assertEquals("(&(cn=Baptiste)(sn=Meynier))",filter);
    }

    @Test
    public void should_format_double_filter_with_empty_value(){
        String filter = LdapFilter.builder()
                .addBinaryOperator(LdapOperator.AND)
                .addFirstFilter("cn", "Baptiste")
                .addSecondFilter(null, null)
                .build();
        Assert.assertEquals("(cn=Baptiste)",filter);
    }


    @Test
    public void should_format_three_filter(){
        String filter = LdapFilter.builder()
                .addBinaryOperator(LdapOperator.AND)
                .addFirstFilter("c", "France")
                .addSecondFilter("sn", "Meynier")
                .completeWithOperator(LdapOperator.AND)
                .completeWithFilter("l", "Paris")
                .build();
        Assert.assertEquals("(&(&(c=France)(sn=Meynier))(l=Paris))",filter);
    }

    @Test
    public void should_format_three_filter_with_different_operators(){
        String filter = LdapFilter.builder()
                .addBinaryOperator(LdapOperator.AND)
                .addFirstFilter("c", "France")
                .addSecondFilter("sn", "Meynier")
                .completeWithOperator(LdapOperator.OR)
                .completeWithFilter("l", "Paris")
                .build();
        Assert.assertEquals("(|(&(c=France)(sn=Meynier))(l=Paris))",filter);
    }


    @Test
    public void should_format_quadruple_filter(){
        String filter = LdapFilter.builder()
                .addBinaryOperator(LdapOperator.AND)
                .addFirstFilter("c", "France")
                .addSecondFilter("sn", "Meynier")
                .completeWithOperator(LdapOperator.OR)
                .completeWithFilter("l", "Paris")
                .completeWithOperator(LdapOperator.AND)
                .completeWithFilter("Initials","Mr")
                .build();
        Assert.assertEquals("(&(|(&(c=France)(sn=Meynier))(l=Paris))(Initials=Mr))",filter);
    }


    @Test
    public void should_format_quadruple_filter_with_empty_filter(){
        String filter = LdapFilter.builder()
                .addBinaryOperator(LdapOperator.AND)
                .addFirstFilter("c", "France")
                .addSecondFilter(null, "AValue")
                .completeWithOperator(LdapOperator.OR)
                .completeWithFilter("givenName","Baptiste")
                .completeWithOperator(LdapOperator.AND)
                .completeWithFilter("Initials","Mr")
                .build();
        Assert.assertEquals("(|(&(c=France)(givenName=Baptiste))(Initials=Mr))",filter);
    }


}
