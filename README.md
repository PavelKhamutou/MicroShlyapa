# MicroShlyapa

Write a simple macrogenerator (one level of definition and calls).
Macrodefinition:
#NAME( &1, &2, &3) { body &1 body &2 body &3 }
“body” is any text. 
Macrocall: 
$NAME(val1, val2, val3, ...)
The number of parameters in macrocall can be greater then in the 
definition. In such a case modulo operator should be used.
Example:
The definition of macro NAME contains 3 parameters.
For macrocall $NAME( XX, AA, BB, CC, DD) DD is the value of &2.



