#include "Process.hpp"

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
