#ifndef SCHEDULER_FACTORY_HPP
#define SCHEDULER_FACTORY_HPP

#include "Scheduler.hpp"
#include "SJFNonPreemptive.hpp"
#include "PriorityPremScheduler.hpp"
#include <string>
#include <vector>

class SchedulerFactory
{
public:
    static Scheduler *createScheduler(const std::string &type, std::vector<Process> &processes);
};

#endif
