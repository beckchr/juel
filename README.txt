Java Unified Expression Language (JUEL)
=======================================

- JUEL is an implementation of the unified expression language (EL) as specified
  by the JSP 2.1 standard (JSR-245).

- Please refer to the EL specification for more information on the unified EL.
  It is available at http://jcp.org/aboutJava/communityprocess/final/jsr245.

- The JUEL distribution contains three jars: juel-2.1.x.jar includes the el api
  and implementation classes; juel-2.1.x-api.jar includes only the api classes,
  whereas juel-2.1.x-impl.jar includes only the implementation classes.
   
- The main JUEL jar may be run from the command line to dump the parse tree of
  an EL expression:
  $ java -jar juel-2.1.x.jar "#{unified(expression[language])}"
  +- #{...}
     |
     +- unified(...)
        |
        +- [...]
           |
           +- expression
           |
           +- language

- Enjoy!
