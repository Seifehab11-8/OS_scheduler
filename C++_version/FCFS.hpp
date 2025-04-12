#ifndef FCFS_HPP
#define FCFS_HPP

#include "Scheduler.hpp"
#include <algorithm>

class FCFS : virtual public Scheduler
{
public:
    FCFS(std::vector<Process> &process);
    FCFS();
int schedule() override;
};

#endif
