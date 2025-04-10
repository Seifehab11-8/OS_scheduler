#include "SchedulerFactory.hpp"

Scheduler *SchedulerFactory::createScheduler(const std::string &type, std::vector<Process> &processes)
{
    if (type == "SJFNonPreemptive")
    {
        return new SJFNonPreemptive(processes);
    }
    else if(type == "PriorityPreemptive") {
        return new PriorityPremScheduler(processes);
    }
    else if (type == "PriorityNonPreemptive") {
        return new PriorityNonPreemptive(processes);
    }
    else
    {
        throw std::invalid_argument("Unknown scheduler type: " + type);
    }
}
