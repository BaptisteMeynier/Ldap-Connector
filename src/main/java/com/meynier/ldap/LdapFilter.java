package com.meynier.ldap;

import com.meynier.ldap.enums.LdapOperator;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

/**
 *                              _______________          ________________          _________________
 *                             |               |        |                |        |                 |
 *              -------------->|FirstFilterStep|------->|SecondFilterStep|------->|PartialFilterStep|
 *             |               |_______________|        |________________|   ---->|_________________|
 *       ______|_______                                          |          |             |
 *      |              |                                         |          |             |
 *  o-->| InitialScopeStep  |                                         |          |             |
 *      |______________|                                         |          |             |
 *             |                                              ___v__________|_            |
 *             |                                             |                |           |
 *              -------------------------------------------->| FinalFilterStep|<-----------
 *                                                           |________________|
 *                                                                  |
 *                                                                  |
 *                                                                  |
 *                                                                   -------------->[X]
 *
 *
 */
public class LdapFilter {

    private static String LDAP_SIMPLE_QUERY_PATTERN = "(%s=%s)";
    private static String LDAP_GENERIC_QUERY_PATTERN = "(%s%s%s)";

    public static InitialStep builder() {
        return new Builder();
    }

    public interface InitialStep {
        FirstFilterStep addBinaryOperator(LdapOperator operator);
        FinalFilterStep addFilter(String field, String value);
    }


    public interface FirstFilterStep {
        SecondFilterStep addFirstFilter(String field, String value);
    }

    public interface SecondFilterStep {
        FinalFilterStep addSecondFilter(String field, String value);
    }

    public interface FinalFilterStep {
        PartialFilterStep completeWithOperator(LdapOperator operator);
        String build();
    }

    public interface PartialFilterStep {
        FinalFilterStep completeWithFilter(String field, String value);
    }

    public static class Builder implements InitialStep, FirstFilterStep, SecondFilterStep, PartialFilterStep,FinalFilterStep {

        private Map<String, String> content = new HashMap<>();
        private Queue<LdapOperator> operators = new LinkedList<>();


        @Override
        public PartialFilterStep completeWithOperator(LdapOperator operator) {
            operators.add(operator);
            return this;
        }
        @Override
        public FirstFilterStep addBinaryOperator(LdapOperator operator) {
            operators.add(operator);
            return this;
        }

        @Override
        public FinalFilterStep addFilter(String field, String value) {
            this.addContent(field, value);
            return this;
        }

        @Override
        public SecondFilterStep addFirstFilter(String field, String value) {
            this.addContent(field, value);
            return this;
        }

        @Override
        public FinalFilterStep addSecondFilter(String field, String value) {
            this.addContent(field, value);
            return this;
        }

        @Override
        public FinalFilterStep completeWithFilter(String field, String value) {
            this.addContent(field, value);
            return this;
        }

        private void addContent(String field, String value) {
            if (Objects.nonNull(field)  && !"".equals(value)) {
                content.put(field, value);
            }
        }


        @Override
        public String build() {
            List<String> filters =
                    content.entrySet().stream()
                            .map(entry -> String.format(LDAP_SIMPLE_QUERY_PATTERN, entry.getKey(), entry.getValue()))
                            .collect(Collectors.toList());

            BinaryOperator<String> buildOperation =
                    (filter1, filter2) -> {
                        LdapOperator operator = operators.poll();
                        return String.format(LDAP_GENERIC_QUERY_PATTERN, operator.getValue(), filter1, filter2);
                    };

            return filters.stream()
                    .reduce(buildOperation)
                    .orElse("");
        }
    }
}
