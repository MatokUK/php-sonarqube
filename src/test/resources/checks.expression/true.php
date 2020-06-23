<?php

/* true */
$a = true || $x;  // NOK {{Expression "true || $x" is always true because usage of literal "true".}}

$b = true || $x || $y; // NOK {{Expression "true || $x || $y" is always true because usage of literal "true".}}

$c = $x || $y || true; // NOK {{Expression "$x || $y || true" is always true because usage of literal "true".}}

$d = $x && $y || true; // NOK {{Expression "$x && $y || true" is always true because usage of literal "true".}}

$e = $x && ($y || true); // NOK {{Expression "$y || true" is always true because usage of literal "true".}}

$f = $a || $b || $c || true || $d || $e; // NOK {{Expression "$a || $b || $c || true || $d || $e" is always true because usage of literal "true".}}

/* number (converted to true) */
$a = 2 || $x;  // NOK {{Expression "2 || $x" is always true because usage of literal "2".}}

$b = 2 || $x || $y; // NOK {{Expression "2 || $x || $y" is always true because usage of literal "2".}}

$c = $x || $y || 4;  // NOK {{Expression "$x || $y || 4" is always true because usage of literal "4".}}

$d = $x && $y || 4;  // NOK {{Expression "$x && $y || 4" is always true because usage of literal "4".}}

/* string */
$a = "a" || $x; // NOK {{Expression ""a" || $x" is always true because usage of literal "a".}}

$a = 'a' || $x; // NOK {{Expression "'a' || $x" is always true because usage of literal "'a'".}}

$b = "" || $x; // OK

$b = '' || $x; // OK

$c = "0" || $x; // OK

$c = '0' || $x; // OK

/* complex expression */
// NOK@+2 {{Expression "$a || $b || true || isValid($c) || 2 || $d" is always true because usage of literal "2".}}
// NOK@+1 {{Expression "$a || $b || true || isValid($c) || 2 || $d" is always true because usage of literal "true".}}
if ($a || $b || true || isValid($c) || 2 || $d) {

}

/* ok: function call */
$a = $x || $y;

$b = isValid($x) || $expressionB;

$c = $expressionA || isValid($x);


/* ok: method call */
$a = $obj->isValid($x) || $y;

$b = $expressionA || $obj->isValid($x);


/* ok: static call */
$a = Obj::isValid($x) || $y;

$b = $expressionA || Obj::isValid($x);
