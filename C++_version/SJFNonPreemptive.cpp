#include "SJFNonPreemptive.hpp"
#include <iostream>

SJFNonPreemptive::SJFNonPreemptive(std::vector<Process> &process) : Scheduler(process) {};
SJFNonPreemptive::SJFNonPreemptive() {};

int SJFNonPreemptive::schedule()
{
    int id;
    bool first_time = true;
    // as we work non premptive only search of new running of process if there is no
    if (running_id == 0)
    {
        running_process = std::min_element(process_queue.begin(), process_queue.end(), [this](const Process &a, const Process &b)
                                           {
        if (a.getArrival_time() > current_time || a.getBurst_time()==a.getExecution_time()) return false;
        if (b.getArrival_time() > current_time || b.getBurst_time()==b.getExecution_time()) return true;  
        return a.getBurst_time() < b.getBurst_time(); });
        if (running_process != process_queue.end() && running_process->getExecution_time() < running_process->getBurst_time() && running_process->getArrival_time() <= current_time)
        {
            running_id = running_process->getID();
        }
    }
    id = running_id;
    if (running_id != 0)
    {
        running_process->setExecution_time(running_process->getExecution_time() + 1);
        if (running_process->getBurst_time() == running_process->getExecution_time())
        {
            running_process->setwaiting_time((current_time + 1) - running_process->getArrival_time() - running_process->getBurst_time());
            running_process->setturnaround_time((current_time + 1) - running_process->getArrival_time());
            running_id = 0;
        }
    }
    current_time++;
    return id;
}
