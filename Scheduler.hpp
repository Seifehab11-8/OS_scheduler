#ifndef SCHEDULER
#define SCHEDULER
#include "Process.hpp"
#include <list>
class Scheduler {
    protected:
    std::list<Process> ready_process;
    std::list<Process> gantt_chart_process;
    public:
    virtual void setProcess(Process &process) = 0;
    virtual std::list<Process> schedule() = 0;
    Scheduler(std::list<Process>&process);
    Scheduler();
};
#endif
