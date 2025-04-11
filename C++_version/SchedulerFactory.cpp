#include "SchedulerFactory.hpp"

Scheduler *SchedulerFactory::createScheduler(const std::string &type, std::vector<Process> &processes)
{
    if (type == "SJFNonPreemptive")
    {
        return new SJFNonPreemptive(processes);
    }
    // Add more scheduler types here as needed
    else
    {
        throw std::invalid_argument("Unknown scheduler type: " + type);
    }
}