<?php

class DefaultController
{
    public function sleepAction(int $delay)
    {
        sleep(5); // NOK {{Remove the usage of sleep().}}

        sleep($delay); // NOK
    }

    public function timeSleepAction()
    {
        time_sleep_until(time() + 5); // NOK {{Remove the usage of time_sleep_until().}}
    }


    public function timeNanoSleepAction(int $delay)
    {
        time_nanosleep(100, 0); // NOK {{Remove the usage of time_nanosleep() or set first argument to 0 seconds.}}

        time_nanosleep(0, 30); // OK

        time_nanosleep($delay, 0); // OK - cannot resolve value... :/

        time_nanosleep(1 - 1, 0); // OK - cannot resolve value... :/

        time_nanosleep(); // OK - invalid syntax, but analysis should pass
    }

    public function usleepAction(int $delay)
    {
        // wait for 1 seconds
        usleep(1000000); // NOK

        // wait for 2.5 seconds
        usleep(2500000); // NOK


        // wait for 0.5 seconds
        usleep(500000); // OK

        usleep($delay); // OK - cannot resolve value... :/

        usleep(); // OK - invalid syntax, but analysis should pass
    }
}