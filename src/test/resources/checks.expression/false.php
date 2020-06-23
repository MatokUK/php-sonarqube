<?php

/* false */
$a = false && $x;  // NOK {{Expression "false && $x" is always false because usage of literal "false".}}

$b = false && $x && $y; // NOK {{Expression "false && $x && $y" is always false because usage of literal "false".}}

$c = $x && $y && false; // NOK {{Expression "$x && $y && false" is always false because usage of literal "false".}}

$d = $x && $y && false; // NOK {{Expression "$x && $y && false" is always false because usage of literal "false".}}

$e = $x && ($y && false); // NOK {{Expression "$y && false" is always false because usage of literal "false".}}

$f = $a && $b && $c && false && $d && $e; // NOK {{Expression "$a && $b && $c && false && $d && $e" is always false because usage of literal "false".}}


/* number (converted to true) */
$a = 0 && $x;  // NOK {{Expression "0 && $x" is always false because usage of literal "0".}}

$b = 0 && $x && $y; // NOK {{Expression "0 && $x && $y" is always false because usage of literal "0".}}

$c = $x && $y && 0;  // NOK {{Expression "$x && $y && 0" is always false because usage of literal "0".}}

$d = 2 && $x; // OK


/* string */
$a = "a" && $x; // OK

$b = "" && $x; // NOK {{Expression """ && $x" is always false because usage of literal "".}}

$b = '' && $x; // NOK {{Expression "'' && $x" is always false because usage of literal "''".}}

$c = "0" && $x; // NOK {{Expression ""0" && $x" is always false because usage of literal "0".}}

$d = '0' && $x; // NOK {{Expression "'0' && $x" is always false because usage of literal "'0'".}}
