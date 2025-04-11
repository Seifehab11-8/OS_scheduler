#ifndef PRIORITY_PREM_SCHEDULER
#define PRIORITY_PREM_SCHEDULER
#include "Scheduler.hpp"
class PriorityPremScheduler : public Scheduler {
    private:
    bool new_add_flag = true;
    public:
    PriorityPremScheduler();
    PriorityPremScheduler(std::vector<Process> processes);
    int schedule() override;
    void addProcess(Process & process) override;
    void printProcesses() override;
};


#endif