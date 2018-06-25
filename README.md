# Hibernate Search 6 Proof-of-Concept

[![Build Status](https://travis-ci.org/hibernate/hibernate-search-6-poc.svg?branch=master)](https://travis-ci.org/hibernate/hibernate-search-6-poc)
[![Coverage Status](https://coveralls.io/repos/github/hibernate/hibernate-search-6-poc/badge.svg?branch=master)](https://coveralls.io/github/hibernate/hibernate-search-6-poc?branch=master)
[![Quality gate](https://sonarcloud.io/api/project_badges/measure?project=org.hibernate.search.v6poc%3Ahibernate-search-parent&metric=alert_status)](https://sonarcloud.io/dashboard?id=org.hibernate.search.v6poc%3Ahibernate-search-parent)

This is where the Hibernate Search team experiments with concepts that may,
or may not, end up in Hibernate Search 6.

WARNING: this is all experimental, and very much work in progress.
APIs may change at any time, documentation is nonexistent,
most features probably don't work completely yet,
and API compatibility with Search 5 is undoubtedly broken.

In short, you are welcome to try this library if you are curious,
but you should absolutely not expect to use it in a real-life project.

Documentation is still in its early stages,
but a technical documentation of the internal architecture can be generated through the following command,
executed from the root of your cloned repository: `mvn clean install -pl documentation`.
The documentation will be available as a HTML file at `documentation/target/asciidoctor/en-US/html_single/index.html`.
