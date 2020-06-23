<?php

class DefaultCommand
{
    public function execute()
    {
        sleep(5); // OK

        time_sleep_until(time() + 5); // OK

        time_nanosleep(100, 0); // OK

        time_nanosleep(0, 30); // OK

        // wait for 2.5 seconds
        usleep(2500000); // OK
    }
}