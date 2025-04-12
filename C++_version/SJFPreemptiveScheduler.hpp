#ifndef SJF_PREEMPTIVE_SCHEDULER_HPP
#define SJF_PREEMPTIVE_SCHEDULER_HPP

#include "Scheduler.hpp"
#include <vector>

class SJFPreemptiveScheduler : public Scheduler {
public:
    SJFPreemptiveScheduler();
    SJFPreemptiveScheduler(std::vector<Process>& processes);

    int schedule() override;
};

#endif // SJF_PREEMPTIVE_SCHEDULER_HPP
