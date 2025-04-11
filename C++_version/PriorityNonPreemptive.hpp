#ifndef PRIORITY_NON_PREEMPTIVE_HPP
#define PRIORITY_NON_PREEMPTIVE_HPP

#include "Scheduler.hpp"

class PriorityNonPreemptive : public Scheduler
{
public:
    PriorityNonPreemptive(std::vector<Process> &process);
    PriorityNonPreemptive();
    int schedule() override;
};

#endif
