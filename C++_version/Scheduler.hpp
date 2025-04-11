#ifndef SCHEDULER
#define SCHEDULER
#include "Process.hpp"
#include <vector>
#include <algorithm>
#include <iostream>
class Scheduler
{
protected:
    std::vector<Process> process_queue;
    std::vector<Process>::iterator running_process;
    int current_time;
    int running_id;

public:
    void addProcess(Process &process);
    virtual int schedule() = 0;
    bool finished();
    void printProcesses();
    Scheduler(std::vector<Process> &process);
    Scheduler();
};
#endif