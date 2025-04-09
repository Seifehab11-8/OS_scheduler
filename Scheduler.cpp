#include "Scheduler.hpp"

Scheduler::Scheduler(std::list<Process> &process)
{
    this->ready_process = process;
}

Scheduler::Scheduler()
{
}
