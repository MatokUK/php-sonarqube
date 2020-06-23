<?php

class Foo
{
    public function __construct()
    {
        return $this;  // NOK {{Remove return in constructor.}}
    }

    public function init()
    {

        return $this; // OK
    }
}


function bar() {
    return 5; // OK
}