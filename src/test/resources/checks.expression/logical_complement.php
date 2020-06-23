<?php

/* true */
$a = !0 || $x; // NOK {{Expression "!0 || $x" is always true because usage of literal "!0".}}

$a = !"0" || $x; // NOK

$a = !'0' || $x; // NOK

$a = !"" || $x; // NOK

$a = !'' || $x; // NOK

$a = !!0 || $x; // OK

$a = !!!0 || $x; // NOK

$a = !!!"" || $x; // NOK

$a = !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"" || $x; // NOK


/* false */
$a = !1 && $x; // NOK {{Expression "!1 && $x" is always false because usage of literal "!1".}}

$a = !!0 && $x; // NOK {{Expression "!!0 && $x" is always false because usage of literal "!!0".}}

$a = !"1" && $x; // NOK

$a = !!"0" && $x; // NOK

$a = !!"" && $x; // NOK