#include "SchedulerFactory.hpp"
#include "PriorityNonPreemptive.hpp" // Ensure this header is included

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
    else if(type == "RoundRobinScheduler") {
        return new RoundRobinScheduler(processes , 2);		// Quantum = 2.
    }
    else
    {
        throw std::invalid_argument("Unknown scheduler type: " + type);
    }
    
    return NULL;
    
}
