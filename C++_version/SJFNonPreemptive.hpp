#ifndef SJFNONPREEMPTIVE_HPP
#define SJFNONPREEMPTIVE_HPP

#include "Scheduler.hpp"
#include <algorithm>

class SJFNonPreemptive : virtual public Scheduler
{
public:
    SJFNonPreemptive(std::vector<Process> &process);
    SJFNonPreemptive();
    int schedule() override;
};

#endif
