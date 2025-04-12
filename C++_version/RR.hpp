#ifndef ROUND_ROBIN_SCHEDULER
#define ROUND_ROBIN_SCHEDULER

#include "Scheduler.hpp"

class RoundRobinScheduler : public Scheduler
{
private:
    int quantum;
    int current_index;

public:
    RoundRobinScheduler(std::vector<Process> &process, int q);
    RoundRobinScheduler(int q);
    int schedule() override;
};

#endif

