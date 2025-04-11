#include "Process.hpp"

int ID = 1;

Process::Process(int arrival_time,
                 int burst_time,
                 int priority) : id(ID), arrival_time(arrival_time), burst_time(burst_time), priority(priority)
{
    ID++;
    execution_time = 0;
    waiting_time = 0;
    turnaround_time = 0;
};
int Process::getID() const
{
    return id;
}

int Process::getPriority() const
{
    return priority;
}

int Process::getArrival_time() const
{
    return arrival_time;
}

int Process::getBurst_time() const
{
    return burst_time;
}

int Process::getExecution_time() const
{
    return execution_time;
}

int Process::getwaiting_time() const
{
    return waiting_time;
}

int Process::getturnaround_time() const
{
    return turnaround_time;
}

void Process::setID(int id)
{
    this->id = id;
}

void Process::setPriority(int priority)
{
    this->priority = priority;
}

void Process::setArrival_time(int arrival_time)
{
    this->arrival_time = arrival_time;
}

void Process::setBurst_time(int burst_time)
{
    this->burst_time = burst_time;
}

void Process::setExecution_time(int execution_time)
{
    this->execution_time = execution_time;
}

void Process::setwaiting_time(int waiting_time)
{
    this->waiting_time = waiting_time;
}
void Process::setturnaround_time(int turnaround_time)
{
    this->turnaround_time = turnaround_time;
}